
# Scoreboard interview task

## About

This service is able to create, find and update scoreboards and store them using a persistence layer.
It also pushes scores updates to the client browser using SSE (Server-Sent Events)

## Implementation

* It has been implemented using a reactive approach with Spring Boot and Spring Webflux.
* For the persistence layer an in-memory embedded H2 database is used along with a spring data R2DBC based repository.
* Spring events are created on successful scoreboard updates and they're are sent to clients as SSE 
via a basic spring integration messaging system.
* A basic web page has been included to easily subscribe and observe scoreboard push updates.
* Some initial data is put into the database when the service starts by a CommandLineRunner.
* lombok library is used to reduce boilerplate code.
* Other useful features/tools included are Spring actuator, Swagger UI and maven wrapper.   

Test Strategy:
* Spring Boot based tests without mocks, each test covers a complete use case scenario.
* The same in-memory embedded database is used by tests and it's recreated before each test execution.
* jacoco-maven-plugin is used for test coverage, and 100% line coverage on maven verify phase is enforced.

## Usage

* #### Clean, build, run tests and test coverage
```
$ ./mvnw clean verify
```

* #### Run service
```
$ ./mvnw spring-boot:run
```

* #### Get all scoreboards
```
$ curl --location --request GET 'http://localhost:8080/scoreboard'
```

* #### Get scoreboard by id
```
$ curl --location --request GET 'http://localhost:8080/scoreboard/1'
```

* #### Create scoreboard
```
$ curl --location --request POST 'http://localhost:8080/scoreboard' \
--header 'Content-Type: application/json' \
--data-raw '{
    "event": "Real Oviedo vs CF Fuenlabrada",
    "score": "1-0"
}'
```

* #### Update scoreboard
```
$ curl --location --request PUT 'http://localhost:8080/scoreboard/3' \
--header 'Content-Type: application/json' \
--data-raw '{
    "event": "Real Oviedo vs CF Fuenlabrada",
    "score": "1-1"
}'
```
* #### Get scoreboard events (SSE)
```
$ curl http://localhost:8080/scoreboard/events
```
Alternatively you can just navigate to <http://localhost:8080/index.html>

### Other useful links

* #### Spring actuator info endpoint
  It contains service build and git information
    * <http://localhost:8080/actuator/info>

* #### Swagger UI
  It contains auto generated service API documentation
    * <http://localhost:8080/swagger-ui.html>

* #### Simple web page to test Scoreboard SSE (Server-Sent Events)
  * <http://localhost:8080/index.html>