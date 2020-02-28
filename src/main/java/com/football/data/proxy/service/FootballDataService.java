package com.football.data.proxy.service;

import com.football.data.proxy.exception.FootballDataException;

public interface FootballDataService {

	void importLeague(String leagueCode) throws FootballDataException;

	String getTotalPlayers(String leagueCode) throws FootballDataException;

}
