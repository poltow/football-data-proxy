package com.football.data.proxy.service;

import java.io.IOException;

import com.football.data.proxy.domain.Competition;
import com.football.data.proxy.exception.FootballDataException;
import com.football.data.proxy.exception.LeagueAlreadyImportedException;
import com.football.data.proxy.exception.LeagueNotFoundException;

public interface CompetitionService {

	void importCompetition(String leagueCode) throws FootballDataException, IOException;

	Competition getCompetitionByCode(String leagueCode) throws LeagueNotFoundException;

	void validateNotAlreadyImported(String leagueCode) throws LeagueAlreadyImportedException;

}
