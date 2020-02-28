package com.football.data.proxy.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.football.data.proxy.domain.Competition;
import com.football.data.proxy.domain.Player;
import com.football.data.proxy.domain.Team;
import com.football.data.proxy.exception.InvalidLeagueCodeException;
import com.football.data.proxy.service.CompetitionService;
import com.football.data.proxy.service.FootballDataService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ FootballDataServiceImpl.class })
public class FootballDataServiceTest {

	private FootballDataService footballDataService;

	private CompetitionService competitionService;

	@Before
	public void setUp() {

		footballDataService = PowerMockito.spy(new FootballDataServiceImpl());

		competitionService = PowerMockito.mock(CompetitionService.class);
		Whitebox.setInternalState(footballDataService, "competitionService", competitionService);
	}

	@Test(expected = InvalidLeagueCodeException.class)
	public void getTotalPlayersShouldThrowExceptionWhenEmptyInput() throws Exception {

		footballDataService.getTotalPlayers("");

	}

	@Test(expected = InvalidLeagueCodeException.class)
	public void getTotalPlayersShouldThrowExceptionWhenBadInput() throws Exception {
		footballDataService.getTotalPlayers(".__.");
	}

	@Test
	public void getTotalPlayersShouldRetrieveLeagueAndCalculateResult() throws Exception {
		Competition competition = createFullCompetition();

		PowerMockito.doReturn(competition).when(competitionService).getCompetitionByCode(eq("AB"));

		String result = footballDataService.getTotalPlayers("AB");

		assertEquals("{ \"total\" : 15 }", result);
	}

	private Competition createFullCompetition() {
		Competition competition = new Competition();
		for (int i = 0; i < 3; i++) {
			competition.getTeams().add(createFullTeam(i));
		}
		return competition;
	}

	private Team createFullTeam(int id) {
		String[] positions = { "Defense", "Goalkeeper", null };

		id *= 10;

		Team team = createTeam(id);
		for (int i = 0; i < 7; i++) {
			Player player = createPlayer(id + i);
			player.setPosition(positions[i % positions.length]);
			team.getPlayers().add(player);
		}
		return team;
	}

	private Team createTeam(int id) {
		Team team = new Team();
		team.setId((long) id);
		team.setName("name_" + id);
		team.setAreaName("areaName_" + id);
		return team;
	}

	private Player createPlayer(int id) {
		Player player = new Player();
		player.setId((long) id);
		player.setName("name_" + id);
		player.setCountryOfBirth("countryOfBirth_" + id);
		player.setNationality("nationality_" + id);
		player.setDateOfBirth(new Date());
		return player;
	}

	@Test(expected = InvalidLeagueCodeException.class)
	public void importLeagueShouldThrowExceptionWhenEmptyInput() throws Exception {

		footballDataService.importLeague("");

	}

	@Test(expected = InvalidLeagueCodeException.class)
	public void importLeagueShouldThrowExceptionWhenBadInput() throws Exception {
		footballDataService.importLeague(".__.");
	}

	@Test
	public void importLeaguehouldValidateNotImportedAndImport() throws Exception {

		PowerMockito.doNothing().when(competitionService).validateNotAlreadyImported(eq("AB"));
		PowerMockito.doNothing().when(competitionService).importCompetition(eq("AB"));

		footballDataService.importLeague("AB");

		verify(competitionService).validateNotAlreadyImported(eq("AB"));
		verify(competitionService).importCompetition(eq("AB"));
	}

}
