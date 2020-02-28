package com.football.data.proxy.service.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenProvider {

	@Value("${footballdata.token}")
	private String token;

	public String getToken() {
		return token;
	}

}
