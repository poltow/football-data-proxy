package com.football.data.proxy.service.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.football.data.proxy.exception.FootballServerErrorException;
import com.football.data.proxy.exception.InvalidLeagueCodeException;

@RunWith(PowerMockRunner.class)
public class ServiceUtilsTest {

	@Test(expected = InvalidLeagueCodeException.class)
	public void validateLeagueCodeFormatShouldThrowErrorWhenNullInput() throws Exception {

		String input = null;
		ServiceUtils.validateLeagueCodeFormat(input);

	}

	@Test(expected = InvalidLeagueCodeException.class)
	public void validateLeagueCodeFormatShouldThrowErrorWhenEmptyInput() throws Exception {

		String input = "";
		ServiceUtils.validateLeagueCodeFormat(input);

	}

	@Test(expected = InvalidLeagueCodeException.class)
	public void validateLeagueCodeFormatShouldThrowErrorWhenBadInput() throws Exception {

		String input = "...";
		ServiceUtils.validateLeagueCodeFormat(input);

	}

	@Test
	public void validateLeagueCodeFormatShouldPassWhenInputOk() throws Exception {

		String input = "WC";
		ServiceUtils.validateLeagueCodeFormat(input);

	}

	@Test
	public void formatStringNodeShouldReturnFormatedString() throws Exception {

		String json = "\"Spain\"";
		String result = ServiceUtils.formatStringNode(new ObjectMapper().readTree(json));

		assertEquals("Spain", result);

	}

	@Test
	public void formatDateNodeShouldReturnFormatedString() throws Exception {

		String json = "\"2020-06-12T12:16:01Z\"";
		Date result = ServiceUtils.formatDateNode(new ObjectMapper().readTree(json));

		Calendar cal = Calendar.getInstance();
		cal.setTime(result);
		assertTrue(2020 == cal.get(Calendar.YEAR));
		assertTrue(05 == cal.get(Calendar.MONTH));
		assertTrue(12 == cal.get(Calendar.DAY_OF_MONTH));

	}

	@Test(expected = FootballServerErrorException.class)
	public void validateResultShouldThrowErrorWhenNullResult() throws Exception {

		String input = null;
		ServiceUtils.validateResult(input);

	}

	@Test(expected = FootballServerErrorException.class)
	public void validateResultShouldThrowErrorWhenEmptyResult() throws Exception {

		String input = "";
		ServiceUtils.validateResult(input);

	}

	@Test
	public void validateResultShouldPassWhenResultOk() throws Exception {

		String input = "result";
		ServiceUtils.validateResult(input);

	}
}
