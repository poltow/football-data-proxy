package com.football.data.proxy.service.impl;

import static com.football.data.proxy.service.utils.Constants.COMPETITION_URI;
import static com.football.data.proxy.service.utils.ServiceUtils.formatStringNode;
import static com.football.data.proxy.service.utils.ServiceUtils.performGETcall;
import static com.football.data.proxy.service.utils.ServiceUtils.validateResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.football.data.proxy.domain.Competition;
import com.football.data.proxy.domain.Player;
import com.football.data.proxy.domain.Team;
import com.football.data.proxy.exception.FootballDataException;
import com.football.data.proxy.exception.LeagueAlreadyImportedException;
import com.football.data.proxy.exception.LeagueNotFoundException;
import com.football.data.proxy.repository.CompetitionRepository;
import com.football.data.proxy.service.CompetitionService;
import com.football.data.proxy.service.PlayerService;
import com.football.data.proxy.service.utils.TokenProvider;

@Service
@Transactional
public class CompetitionServiceImpl implements CompetitionService {

	Logger logger = LoggerFactory.getLogger(CompetitionServiceImpl.class);

	@Autowired
	private CompetitionRepository competitionRepository;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private TokenProvider tokenProvider;

	@Override
	public void importCompetition(String leagueCode) throws FootballDataException, IOException {

		JsonNode jsonCompetitionWithTeams = getCompetitionAndTeamsFromAPI(leagueCode);
		Competition competition = parseCompetition(jsonCompetitionWithTeams);
		List<Team> teams = parseCompetitionTeams(jsonCompetitionWithTeams, competition);

		addPlayersToTeams(teams);
		addTeamsToCompetition(competition, teams);

		competitionRepository.save(competition);
	}

	@Override
	public void validateNotAlreadyImported(String leagueCode) throws LeagueAlreadyImportedException {
		List<Competition> competitions = competitionRepository.findByCode(leagueCode);
		if (competitions != null && !competitions.isEmpty())
			throw new LeagueAlreadyImportedException("League already imported");
	}

	@Override
	public Competition getCompetitionByCode(String leagueCode) throws LeagueNotFoundException {
		List<Competition> comps = competitionRepository.findByCode(leagueCode);
		validateCompetitionResult(comps);
		return comps.get(0);
	}

	private void validateCompetitionResult(List<Competition> comps) throws LeagueNotFoundException {
		if (comps == null || comps.isEmpty())
			throw new LeagueNotFoundException("League not found");
	}

	private JsonNode getCompetitionAndTeamsFromAPI(String leagueCode) throws FootballDataException, IOException {
		ResponseEntity<String> response = performGETcall(String.format(COMPETITION_URI, leagueCode), getToken());
		validateResult(response.getBody());
		return new ObjectMapper().readTree(response.getBody());
	}

	private String getToken() {
		return tokenProvider.getToken();
	}

	private Competition parseCompetition(JsonNode jsonNode) throws IOException {

		jsonNode = jsonNode.get("competition");
		return createCompetition(jsonNode);
	}

	private Competition createCompetition(JsonNode node) {
		Competition c = new Competition();
		c.setId(node.get("id").asLong());
		c.setAreaName(formatStringNode(node.get("area").get("name")));
		c.setCode(formatStringNode(node.get("code")));
		c.setName(formatStringNode(node.get("name")));
		return c;
	}

	private List<Team> parseCompetitionTeams(JsonNode jsonNode, Competition competition) throws IOException {
		jsonNode = jsonNode.get("teams");
		return createTeams(jsonNode, competition);
	}

	private List<Team> createTeams(JsonNode jsonNode, Competition competition) {
		List<Team> teams = new ArrayList<Team>();
		for (JsonNode node : jsonNode) {
			Team team = createtTeam(node);
			team.getCompetitions().add(competition);
			teams.add(team);
		}
		return teams;
	}

	private Team createtTeam(JsonNode node) {
		Team t = new Team();
		t.setId(node.get("id").asLong());
		t.setName(formatStringNode(node.get("name")));
		t.setTla(formatStringNode(node.get("tla")));
		t.setShortName(formatStringNode(node.get("shortName")));
		t.setAreaName(formatStringNode(node.get("area").get("name")));
		t.setEmail(formatStringNode(node.get("email")));
		return t;
	}

	private void addTeamsToCompetition(Competition competition, List<Team> teams) {
		competition.getTeams().addAll(teams);
	}

	private void addPlayersToTeams(List<Team> teams) throws FootballDataException, IOException {
		for (Team team : teams) {
			List<Player> squad = playerService.getSquadForTeam(team);
			team.getPlayers().addAll(squad);
		}
	}

}
