package com.football.data.proxy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.CONFLICT)
public class LeagueAlreadyImportedException extends FootballDataException {
	public LeagueAlreadyImportedException(String mensaje) {
		super(mensaje);
	}
}
