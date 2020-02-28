package com.football.data.proxy.service.impl;

import static com.football.data.proxy.service.utils.ServiceUtils.validateLeagueCodeFormat;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.football.data.proxy.domain.Competition;
import com.football.data.proxy.domain.Player;
import com.football.data.proxy.domain.Team;
import com.football.data.proxy.exception.FootballDataException;
import com.football.data.proxy.exception.FootballServerErrorException;
import com.football.data.proxy.service.CompetitionService;
import com.football.data.proxy.service.FootballDataService;

@Service
@Transactional
public class FootballDataServiceImpl implements FootballDataService {

	@Autowired
	private CompetitionService competitionService;

	@Override
	public String getTotalPlayers(String leagueCode) throws FootballDataException {
		validateLeagueCodeFormat(leagueCode);
		Competition competition = competitionService.getCompetitionByCode(leagueCode);
		return "{ \"total\" : " + getNumberOfPlayersFor(competition) + " }";
	}

	private int getNumberOfPlayersFor(Competition competition) {
		Set<Player> players = getPlayersFromTeams(competition.getTeams());
		List<Player> result = players.stream().filter(p -> p.getPosition() != null).collect(Collectors.toList());
		return result.size();
	}

	private Set<Player> getPlayersFromTeams(Set<Team> teams) {
		Set<Player> players = new HashSet<Player>();
		for (Team team : teams) {
			Set<Player> squad = team.getPlayers();
			players.addAll(squad);
		}
		return players;
	}

	@Override
	public void importLeague(String leagueCode) throws FootballDataException {
		validateLeagueCodeFormat(leagueCode);
		validateNotAlreadyImported(leagueCode);
		importCompetition(leagueCode);
	}

	private void validateNotAlreadyImported(String leagueCode) throws FootballDataException {
		competitionService.validateNotAlreadyImported(leagueCode);
	}

	private void importCompetition(String leagueCode) throws FootballDataException {
		try {
			competitionService.importCompetition(leagueCode);
		} catch (IOException e) {
			throw new FootballServerErrorException("Server Error");
		}
	}
}
