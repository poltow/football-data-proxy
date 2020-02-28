package com.football.data.proxy.service.impl;

import static com.football.data.TestConstants.JSON_TEAMS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.springframework.http.HttpStatus.OK;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.football.data.proxy.domain.Competition;
import com.football.data.proxy.domain.Player;
import com.football.data.proxy.domain.Team;
import com.football.data.proxy.exception.LeagueAlreadyImportedException;
import com.football.data.proxy.exception.LeagueNotFoundException;
import com.football.data.proxy.repository.CompetitionRepository;
import com.football.data.proxy.service.CompetitionService;
import com.football.data.proxy.service.PlayerService;
import com.football.data.proxy.service.utils.ServiceUtils;
import com.football.data.proxy.service.utils.TokenProvider;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CompetitionServiceImpl.class, ServiceUtils.class })
public class CompetitionServiceTest {

	private PlayerService playerService;

	private CompetitionService competitionService;

	private CompetitionRepository competitionRepository;

	private TokenProvider tokenProvider;

	ResponseEntity<String> teamsResponseFromAPI = new ResponseEntity<String>(JSON_TEAMS, OK);

	@Before
	public void setUp() {

		competitionService = PowerMockito.spy(new CompetitionServiceImpl());

		competitionRepository = PowerMockito.mock(CompetitionRepository.class);
		playerService = PowerMockito.mock(PlayerService.class);
		tokenProvider = PowerMockito.mock(TokenProvider.class);

		Whitebox.setInternalState(competitionService, "playerService", playerService);
		Whitebox.setInternalState(competitionService, "competitionRepository", competitionRepository);
		Whitebox.setInternalState(competitionService, "tokenProvider", tokenProvider);
	}

	@Test
	public void importCompetitionShouldCallAPIParseAndSave() throws Exception {

		JsonNode node = PowerMockito.mock(JsonNode.class);
		doReturn(node).when(competitionService, "getCompetitionAndTeamsFromAPI", anyString());

		Competition competition = new Competition();
		doReturn(competition).when(competitionService, "parseCompetition", any());

		List<Team> teams = new ArrayList<>();
		doReturn(teams).when(competitionService, "parseCompetitionTeams", any(), any());

		doNothing().when(competitionService, "addPlayersToTeams", any());
		doNothing().when(competitionService, "addTeamsToCompetition", any(), any());
		doReturn(competition).when(competitionRepository).save(any());

		competitionService.importCompetition("leagueCode");

		PowerMockito.verifyPrivate(competitionService).invoke("getCompetitionAndTeamsFromAPI", eq("leagueCode"));
		PowerMockito.verifyPrivate(competitionService).invoke("parseCompetition", eq(node));
		PowerMockito.verifyPrivate(competitionService).invoke("parseCompetitionTeams", eq(node), eq(competition));
		PowerMockito.verifyPrivate(competitionService).invoke("addPlayersToTeams", eq(teams));
		PowerMockito.verifyPrivate(competitionService).invoke("addTeamsToCompetition", eq(competition), eq(teams));
		verify(competitionRepository).save(eq(competition));
	}

	@Test(expected = LeagueAlreadyImportedException.class)
	public void validateNotAlreadyImportedShouldThrowExceptionWhenResultIsFound() throws Exception {
		List<Competition> comps = new ArrayList<Competition>();
		comps.add(new Competition());

		doReturn(comps).when(competitionRepository).findByCode(anyString());

		competitionService.validateNotAlreadyImported("leagueCode");
	}

	@Test
	public void validateNotAlreadyImportedShouldPassWhenResultIsEmpty() throws Exception {
		List<Competition> comps = new ArrayList<Competition>();

		doReturn(comps).when(competitionRepository).findByCode(anyString());

		competitionService.validateNotAlreadyImported("leagueCode");

		verify(competitionRepository).findByCode(eq("leagueCode"));
	}

	@Test
	public void validateNotAlreadyImportedShouldPassWhenResultIsNull() throws Exception {
		List<Competition> comps = null;

		doReturn(comps).when(competitionRepository).findByCode(anyString());

		competitionService.validateNotAlreadyImported("leagueCode");

		verify(competitionRepository).findByCode(eq("leagueCode"));
	}

	@Test
	public void getCompetitionByCodeShouldReturnFirstCompetition() throws Exception {
		List<Competition> comps = new ArrayList<Competition>();
		for (int i = 0; i < 4; i++) {
			Competition competition = new Competition();
			competition.setId((long) i);
			comps.add(competition);
		}

		doReturn(comps).when(competitionRepository).findByCode(anyString());

		doNothing().when(competitionService, "validateCompetitionResult", eq(comps));

		Competition result = competitionService.getCompetitionByCode("leagueCode");

		PowerMockito.verifyPrivate(competitionService).invoke("validateCompetitionResult", eq(comps));

		assert (0 == result.getId());

	}

	@Test(expected = LeagueNotFoundException.class)
	public void validateCompetitionResultShouldThrowExceptionWhenResultIsEmpty() throws Exception {

		List<Competition> comps = new ArrayList<Competition>();

		Whitebox.invokeMethod(competitionService, "validateCompetitionResult", comps);

	}

	@Test(expected = LeagueNotFoundException.class)
	public void validateCompetitionResultShouldThrowExceptionWhenResultIsNull() throws Exception {
		List<Competition> comps = null;

		Whitebox.invokeMethod(competitionService, "validateCompetitionResult", comps);
	}

	@Test
	public void validateCompetitionResultShouldPassWithNotEmptyResult() throws Exception {
		List<Competition> comps = new ArrayList<Competition>();
		comps.add(new Competition());

		Whitebox.invokeMethod(competitionService, "validateCompetitionResult", comps);
	}

	@Test
	public void getCompetitionAndTeamsFromAPIShouldDoCallValidateAndParseResult() throws Exception {
		mockStatic(ServiceUtils.class);
		doReturn(teamsResponseFromAPI).when(ServiceUtils.class, "performGETcall", anyString(), any());
		doNothing().when(ServiceUtils.class, "validateResult", anyString());

		JsonNode result = Whitebox.invokeMethod(competitionService, "getCompetitionAndTeamsFromAPI", "leagueCode");

		assertTrue(result.get("competition") != null);
		assertTrue(result.get("teams") != null);

	}

	@Test
	public void getToken() throws Exception {

		Whitebox.invokeMethod(competitionService, "getToken");

		verify(tokenProvider).getToken();

	}

	@Test
	public void parseCompetitionShouldExtractAndCreateCompetition() throws Exception {

		JsonNode leagueNode = new ObjectMapper().readTree(teamsResponseFromAPI.getBody());
		Competition result = Whitebox.invokeMethod(competitionService, "parseCompetition", leagueNode);

		assertTrue(2018l == result.getId());
		assertEquals("European Championship", result.getName());
		assertEquals("EC", result.getCode());
		assertEquals("Europe", result.getAreaName());
		assertTrue(result.getTeams().isEmpty());
	}

	@Test
	public void parseCompetitionTeamsShouldExtractAndCreateCompetitionTeams() throws Exception {

		Competition competition = new Competition();

		JsonNode leagueNode = new ObjectMapper().readTree(teamsResponseFromAPI.getBody());
		List<Team> result = Whitebox.invokeMethod(competitionService, "parseCompetitionTeams", leagueNode, competition);

		assertTrue(759l == result.get(0).getId());
		assertEquals("Germany", result.get(0).getName());
		assertEquals("Germany", result.get(0).getShortName());
		assertEquals("GER", result.get(0).getTla());
		assertEquals("Germany", result.get(0).getAreaName());
		assertEquals("info@dfb.de", result.get(0).getEmail());
		assertTrue(result.get(0).getCompetitions().contains(competition));
	}

	@Test
	public void addTeamsToCompetition() throws Exception {

		Competition competition = new Competition();
		List<Team> teams = new ArrayList<>();
		Team team1 = new Team();
		teams.add(team1);
		Team team2 = new Team();
		teams.add(team2);
		Team team3 = new Team();
		teams.add(team3);

		Whitebox.invokeMethod(competitionService, "addTeamsToCompetition", competition, teams);

		assert (competition.getTeams().contains(team1));
		assert (competition.getTeams().contains(team2));
		assert (competition.getTeams().contains(team3));
	}

	@Test
	public void addPlayersToTeams() throws Exception {

		List<Team> teams = new ArrayList<>();
		Team team1 = new Team();
		teams.add(team1);
		Team team2 = new Team();
		teams.add(team2);

		List<Player> squad = new ArrayList<>();
		Player player1 = new Player();
		squad.add(player1);
		Player player2 = new Player();
		squad.add(player2);

		doReturn(squad).when(playerService).getSquadForTeam(any());

		Whitebox.invokeMethod(competitionService, "addPlayersToTeams", teams);

		assert (team1.getPlayers().contains(player1));
		assert (team1.getPlayers().contains(player2));
		assert (team2.getPlayers().contains(player1));
		assert (team2.getPlayers().contains(player2));
	}

}
