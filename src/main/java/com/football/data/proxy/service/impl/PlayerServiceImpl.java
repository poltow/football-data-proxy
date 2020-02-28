package com.football.data.proxy.service.impl;

import static com.football.data.proxy.service.utils.Constants.PLAYERS_URI;
import static com.football.data.proxy.service.utils.ServiceUtils.formatDateNode;
import static com.football.data.proxy.service.utils.ServiceUtils.formatStringNode;
import static com.football.data.proxy.service.utils.ServiceUtils.performGETcall;
import static com.football.data.proxy.service.utils.ServiceUtils.validateResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.football.data.proxy.domain.Player;
import com.football.data.proxy.domain.Team;
import com.football.data.proxy.exception.FootballDataException;
import com.football.data.proxy.service.PlayerService;
import com.football.data.proxy.service.utils.TokenProvider;

@Service
@Transactional
public class PlayerServiceImpl implements PlayerService {

	@Autowired
	private TokenProvider tokenProvider;

	@Override
	public List<Player> getSquadForTeam(Team team) throws FootballDataException, IOException {
		String jsonTeams = getPlayersFromAPI(team);
		return parseTeamPlayers(jsonTeams, team);
	}

	private String getPlayersFromAPI(Team team) throws FootballDataException {
		ResponseEntity<String> response = performGETcall(String.format(PLAYERS_URI, team.getId()), getToken());
		validateResult(response.getBody());
		return response.getBody();
	}

	private String getToken() {
		return tokenProvider.getToken();
	}

	private List<Player> parseTeamPlayers(String jsonTeams, Team team) throws IOException {
		JsonNode jsonNode = new ObjectMapper().readTree(jsonTeams);
		return getTeamPlayersFromSquadNode(team, jsonNode.get("squad"));
	}

	private List<Player> getTeamPlayersFromSquadNode(Team team, JsonNode squadNode) {
		List<Player> players = new ArrayList<Player>();
		for (JsonNode node : squadNode) {
			Player player = createPlayerFromNode(node);
			player.getTeams().add(team);
			players.add(player);
		}
		return players;
	}

	private Player createPlayerFromNode(JsonNode node) {
		Player p = new Player();
		p.setId(node.get("id").asLong());
		p.setName(formatStringNode(node.get("name")));
		p.setPosition(formatStringNode(node.get("position")));
		p.setDateOfBirth(formatDateNode(node.get("dateOfBirth")));
		p.setCountryOfBirth(formatStringNode(node.get("countryOfBirth")));
		p.setNationality(formatStringNode(node.get("nationality")));
		return p;
	}

}
