package com.football.data.proxy.controller.rest;

import static org.springframework.http.HttpStatus.CREATED;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.football.data.proxy.exception.FootballDataException;
import com.football.data.proxy.service.FootballDataService;

@RestController
@RequestMapping("/api/v1")
public class FootballDataController {

	Logger logger = LoggerFactory.getLogger(FootballDataController.class);

	@Autowired
	FootballDataService footballDataService;

	@RequestMapping(method = RequestMethod.GET, value = "/import-league/{leagueCode}")
	@ResponseStatus(CREATED)
	public void importLeague(@PathVariable String leagueCode) throws FootballDataException {
		logger.info("Importing league:" + leagueCode);
		footballDataService.importLeague(leagueCode.toUpperCase());
		logger.info("Import complete");
	}

	@RequestMapping(method = RequestMethod.GET, value = "/total-players/{leagueCode}")
	public String getTotalPlayersFor(@PathVariable String leagueCode) throws FootballDataException {
		return footballDataService.getTotalPlayers(leagueCode.toUpperCase());
	}

}
