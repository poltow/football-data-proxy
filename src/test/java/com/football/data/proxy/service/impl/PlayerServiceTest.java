package com.football.data.proxy.service.impl;

import static com.football.data.TestConstants.JSON_PLAYERS;
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
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.http.ResponseEntity;

import com.football.data.proxy.domain.Player;
import com.football.data.proxy.domain.Team;
import com.football.data.proxy.service.PlayerService;
import com.football.data.proxy.service.utils.ServiceUtils;
import com.football.data.proxy.service.utils.TokenProvider;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PlayerServiceImpl.class, ServiceUtils.class })
public class PlayerServiceTest {

	private PlayerService playerService;

	private TokenProvider tokenProvider;

	ResponseEntity<String> playersResponseFromAPI = new ResponseEntity<String>(JSON_PLAYERS, OK);

	@Before
	public void setUp() {

		playerService = PowerMockito.spy(new PlayerServiceImpl());

		tokenProvider = PowerMockito.mock(TokenProvider.class);

		Whitebox.setInternalState(playerService, "tokenProvider", tokenProvider);
	}

	@Test
	public void getSquadForTeamShouldCallAPIAndParse() throws Exception {

		doReturn("jsonTeams").when(playerService, "getPlayersFromAPI", any());

		List<Player> players = new ArrayList<>();
		doReturn(players).when(playerService, "parseTeamPlayers", eq("jsonTeams"), any());

		List<Player> result = playerService.getSquadForTeam(new Team());

		assertEquals(players, result);
	}

	@Test
	public void getPlayersFromAPIShouldDoCallValidateAndReturnBody() throws Exception {
		mockStatic(ServiceUtils.class);
		doReturn(playersResponseFromAPI).when(ServiceUtils.class, "performGETcall", anyString(), any());
		doNothing().when(ServiceUtils.class, "validateResult", anyString());

		String result = Whitebox.invokeMethod(playerService, "getPlayersFromAPI", new Team());

		assertEquals(JSON_PLAYERS, result);
	}

	@Test
	public void getToken() throws Exception {

		Whitebox.invokeMethod(playerService, "getToken");

		verify(tokenProvider).getToken();

	}

	@Test
	public void parseTeamPlayersShouldExtractAndCreatePlayers() throws Exception {

		Team team = new Team();

		List<Player> result = Whitebox.invokeMethod(playerService, "parseTeamPlayers", JSON_PLAYERS, team);

		Player player = result.get(0);

		assertTrue(3188l == player.getId());

		assertEquals("David De Gea", player.getName());
		assertEquals("Goalkeeper", player.getPosition());
		Calendar cal = Calendar.getInstance();
		cal.setTime(player.getDateOfBirth());
		assertTrue(1990 == cal.get(Calendar.YEAR));
		assertTrue(10 == cal.get(Calendar.MONTH));
		assertTrue(07 == cal.get(Calendar.DAY_OF_MONTH));
		assertEquals("Spain", player.getCountryOfBirth());
		assertEquals("Spain", player.getNationality());
		assertTrue(player.getTeams().contains(team));
	}
}
