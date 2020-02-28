package com.football.data.proxy.controller.rest;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.football.data.proxy.exception.FootballDataException;
import com.football.data.proxy.service.FootballDataService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ FootballDataController.class })
public class FootballDataControllerTest {

	@Mock
	FootballDataService footballDataService;

	@InjectMocks
	private FootballDataController footballDataController;

	@Test
	public void importLeagueShouldUppercaseCallService() throws FootballDataException {
		PowerMockito.doNothing().when(footballDataService).importLeague(eq("LEAGUECODE"));

		footballDataController.importLeague("leagueCode");

		verify(footballDataService).importLeague(eq("LEAGUECODE"));
	}

	@Test
	public void getTotalPlayersForShouldUppercaseCallService() throws FootballDataException {

		PowerMockito.doReturn("totalPlayers").when(footballDataService).getTotalPlayers(eq("LEAGUECODE"));

		String result = footballDataController.getTotalPlayersFor("leagueCode");

		verify(footballDataService).getTotalPlayers(eq("LEAGUECODE"));

		assert ("totalPlayers".equals(result));
	}

}
