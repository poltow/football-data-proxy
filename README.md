# Football Data CODE CHALLENGE 

Requirements: 

The goal is to make a project that exposes an API with an HTTP GET in this URI: /import-league/{leagueCode}. 

E.g., it must be possible to invoke the service using this URL: http://localhost:<port>/import-league/CL

The service implementation must get data using the given {leagueCode}, by making requests 
to the http://www.football-data.org/ API (you can see the documentation entering to the site, use the API v2), and import the data into a DB (MySQL is suggested, but you can use any DB of your preference). 


The data requested is:

	Competition ("name", "code", "areaName")
	
	Team ("name", "tla", "shortName", "areaName", "email")
	
	Player("name", "position", "dateOfBirth", "countryOfBirth", "nationality")

Feel free to add to this data structure any other field that you might need (for the foreign keys relationship).

Additionally, expose an HTTP GET in URI /total-players/{leagueCode},  with a simple JSON response like this: {"total" : N } and HTTP Code 200 where N is the total amount of 
players belonging to all teams that participate in the given league (leagueCode). This service must rely exclusively on the data saved inside the DB (it must not access the API football-data.org). 
If the given leagueCode is not present into the DB, it should respond an HTTP Code 404.

Once you have finished the project, you must upload all the relevant files inside a ZIP compressed file. 
It must include all the sources, plus the files related to project configuration and/or dependency management. 
 
Remarks
    You are allowed to use any library related to the language in which you are implementing the project.
    You must provide the SQL for data structure creation; it is a plus that the project automatically creates the structure 
    (if it doesn't exist) when it runs the first time.
    All the mentioned DB entities must keep their proper relationships (the players with which team they belong to; 
    the teams in which leagues participate).
    The API responses for /import-league/{leagueCode} are:
         
         HttpCode 201, {"message": "Successfully imported"} --> When the leagueCode was successfully imported.
         
         HttpCode 409, {"message": "League already imported"} --> If the given leagueCode was already imported into the DB 
         (and in this case, it doesn't need to be imported again).
         
         HttpCode 404, {"message": "Not found" } --> if the leagueCode was not found.
         
         HttpCode 504, {"message": "Server Error" } --> If there is any connectivity issue either with the football API or the DB server.

It might happen that when a given leagueCode is being imported, the league has participant teams that are already imported (because each team might belong to one or more leagues). For these cases, it must add the relationship between the league and the team(s) and omit the process of the preexistent teams and their players).

# SOLUTION:

    Development tools/frameworks
        - Eclipse
        - Maven
        - MySql 8
        - Spring Boot
        - JUnit, Mockito, PowerMock

## Setup

	- Create a database named football
	
	CREATE DATABASE `football` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
	
	- Set the following info in src/main/resoruces/application.properties file:
	
			footballdata.token=your token from http://www.football-data.org/ (sign up for getting a token)
			spring.datasource.password=yourpass for mysql
			spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL8Dialect (or your mysql version dialect)

## Running the app
    - By command line: mvn spring-boot:run
    - The app will run on http://localhost:8080


## Use of the program
    The program can be accesed by API Rest requests.
    
    Note: The program will do API calls to football-data API services.
    The result will differ based on the purchased plan.
    This affects the import-league response time in a direct way.
    For the free plan, the following are the possible valid league codes:
    WC, CL, BL1, DED, BSA, PD, FL1, ELC, PPL, EC, SA, PL

# API REST DOCS

### Import league

    GET /api/v1/import-league/{leagueCode}

    This will import all league, teams and players info from football-data API .

    Query parameters 
        .leagueCode: League code.

    Request Example
        curl -v -X GET http://localhost:8080/api/v1/import-league/WC \
        -H "Content-Type: application/json" 
 
    Response example
       HTTP/1.1 201 CREATED


### Get total players

    GET /api/v1/total-players/{leagueCode}

    This will return the total number of players that participated in a given league.

    Query parameters 
        .leagueCode: League code.

    Request Example
        curl -v -X GET http://localhost:8080/api/v1/total-players/WC \
        -H "Content-Type: application/json" 


    Response example
		{ "total" : 697 }

