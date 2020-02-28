package com.football.data.proxy.service.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.football.data.proxy.exception.FootballDataException;
import com.football.data.proxy.exception.FootballServerErrorException;
import com.football.data.proxy.exception.InvalidLeagueCodeException;

@Component
public class ServiceUtils {

	static Logger logger = LoggerFactory.getLogger(ServiceUtils.class);

	private ServiceUtils() {
	}

	public static void validateLeagueCodeFormat(String leagueCode) throws FootballDataException {
		if (leagueCode == null || (!Pattern.compile("^[A-Z0-9]+$").matcher(leagueCode).matches()))
			throw new InvalidLeagueCodeException("League code must match ^[A-Z]+$ pattern");

	}

	public static String formatStringNode(JsonNode node) {
		return node.toString().replace("\"", "");
	}

	public static Date formatDateNode(JsonNode jsonNode) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		try {
			return format.parse(formatStringNode(jsonNode));
		} catch (ParseException e) {
			return null;
		}
	}

	public static void validateResult(String result) throws FootballServerErrorException {
		if (result == null || result.isEmpty())
			throw new FootballServerErrorException("Server Error");
	}

	public static ResponseEntity<String> performGETcall(String uri, String token) throws FootballDataException {

		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Auth-Token", token);
		HttpEntity<String> entity = new HttpEntity<>("body", headers);
		try {
			ResponseEntity<String> response = new RestTemplate().exchange(uri, HttpMethod.GET, entity, String.class);
			validateRemainingCalls(response);
			return response;
		} catch (HttpClientErrorException e) {
			throw new FootballServerErrorException(e.getLocalizedMessage());
		}

	}

	private static void validateRemainingCalls(ResponseEntity<String> response) {

		HttpHeaders responseHeaders = response.getHeaders();

		int requestCounter = getRequestCounter(responseHeaders);
		int availableRequests = getAvailableRequests(responseHeaders);
		if (availableRequests == 0 && requestCounter > 0) {
			int waitSeconds = (requestCounter + 1);
			logger.info("Retrieving data...");
			try {
				while (waitSeconds > 0) {
					logger.info("WAIT for " + waitSeconds + " seconds.");
					Thread.sleep(10000);
					waitSeconds -= 10;
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static int getRequestCounter(HttpHeaders responseHeaders) {
		int requestCounter = 0;
		if (responseHeaders.get("X-RequestCounter-Reset") != null) {
			String RequestCounterReset = responseHeaders.get("X-RequestCounter-Reset").get(0);
			requestCounter = Integer.parseInt(RequestCounterReset);
		}
		return requestCounter;
	}

	private static int getAvailableRequests(HttpHeaders responseHeaders) {
		int availableRequests = 0;
		if (responseHeaders.get("X-Requests-Available-Minute") != null) {
			String availableMinute = responseHeaders.get("X-Requests-Available-Minute").get(0);
			availableRequests = Integer.parseInt(availableMinute);
		}
		return availableRequests;
	}

}