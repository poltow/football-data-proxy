package com.football.data.proxy.service;

import java.io.IOException;
import java.util.List;

import com.football.data.proxy.domain.Player;
import com.football.data.proxy.domain.Team;
import com.football.data.proxy.exception.FootballDataException;

public interface PlayerService {

	List<Player> getSquadForTeam(Team team) throws FootballDataException, IOException;

}
