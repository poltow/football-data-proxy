package com.football.data;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.football.data.proxy.controller.rest.FootballDataControllerTest;
import com.football.data.proxy.service.impl.CompetitionServiceTest;
import com.football.data.proxy.service.impl.FootballDataServiceTest;
import com.football.data.proxy.service.impl.PlayerServiceTest;
import com.football.data.proxy.service.utils.ServiceUtilsTest;

@RunWith(Suite.class)
@SuiteClasses({
		FootballDataControllerTest.class, //
		CompetitionServiceTest.class, //
		FootballDataServiceTest.class, //
		PlayerServiceTest.class, //
		ServiceUtilsTest.class })
public class AllTests {

}
