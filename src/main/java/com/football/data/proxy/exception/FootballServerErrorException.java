package com.football.data.proxy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
public class FootballServerErrorException extends FootballDataException {
	public FootballServerErrorException(String mensaje) {
		super(mensaje);
	}
}
