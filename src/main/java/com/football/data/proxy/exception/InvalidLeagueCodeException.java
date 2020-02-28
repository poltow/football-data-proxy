package com.football.data.proxy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidLeagueCodeException extends FootballDataException {
	public InvalidLeagueCodeException(String mensaje) {
		super(mensaje);
	}
}
