package com.wiliamhill.sports.scoreboard.controller;

import com.wiliamhill.sports.scoreboard.domain.Scoreboard;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
@AutoConfigureWebTestClient
class ScoreboardControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ScoreboardEventsController scoreboardEventsController;
    
    @Autowired
    private ConnectionFactory connectionFactory;
    
    @BeforeEach
    void restartDatabase(@Value("classpath:restart_test_database.sql") Resource restartDbScript) {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, restartDbScript)
                        .then(Mono.from(connection.close())))
                .subscribe();
    }

    @Test
    void getAllScoreboards_happyPath_scoreboardsReturned() {
        webTestClient.get()
                .uri("/scoreboard")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$.[0].id").isEqualTo(1)
                .jsonPath("$.[0].event").isEqualTo("Team 1 vs Team 2")
                .jsonPath("$.[0].score").isEqualTo("0-1")
                .jsonPath("$.[1].id").isEqualTo(2)
                .jsonPath("$.[1].event").isEqualTo("Team 3 vs Team 4")
                .jsonPath("$.[1].score").isEqualTo("3-3");
    }

    @Test
    void getScoreboardOne_happyPath_scoreboardReturned() {
        webTestClient.get()
                .uri("/scoreboard/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.event").isEqualTo("Team 1 vs Team 2")
                .jsonPath("$.score").isEqualTo("0-1");
    }

    @Test
    void getScoreboard_scoreboardDoesNotExist_notFoundReturned() {
        webTestClient.get()
                .uri("/scoreboard/9999")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody();
    }
    
    @Test
    void createScoreboard_happyPath_scoreboardCreated() {
        Scoreboard requestBody = Scoreboard.builder()
                .event("Team 5 vs Team 6").score("1-0").build();

        webTestClient.post()
                .uri("/scoreboard")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.event").isEqualTo("Team 5 vs Team 6")
                .jsonPath("$.score").isEqualTo("1-0");
    }    
    
    @Test
    void updateScoreboard_happyPath_scoreboardUpdatedAndEventUpdateSentToSubscriber() {
       
        Scoreboard updatedScoreBoard = Scoreboard.builder()
                .event("Team 1 vs Team 2").score("1-1").build();

        StepVerifier eventUpdateVerifier = StepVerifier.create(scoreboardEventsController.getScoreboardEvents())
                .expectNextMatches(event -> {
                    Scoreboard scoreboard = event.getScoreboard();
                    return "1-1".equals(scoreboard.getScore()) && "Team 1 vs Team 2".equals(scoreboard.getEvent());
                })
                .thenCancel()
                .verifyLater();
        
        webTestClient.put()
                .uri(uriBuilder -> uriBuilder.path("/scoreboard/{id}").build(1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(updatedScoreBoard)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.event").isEqualTo("Team 1 vs Team 2")
                .jsonPath("$.score").isEqualTo("1-1");

        eventUpdateVerifier.verify();
    }        
    
    @Test
    void updateScoreboard_scoreboardDoesNotExist_notFoundReturnedAndEventUpdateNotSentToSubscriber() {
       
        Scoreboard updatedScoreBoard = Scoreboard.builder()
                .event("Team 1 vs Team 2").score("1-1").build();

        StepVerifier eventUpdateVerifier = StepVerifier.withVirtualTime(() -> 
                        scoreboardEventsController.getScoreboardEvents())
                .expectSubscription()
                .expectNoEvent(Duration.ofHours(1))
                .thenCancel()
                .verifyLater();
        
        webTestClient.put()
                .uri(uriBuilder -> uriBuilder.path("/scoreboard/{id}").build(9999))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(updatedScoreBoard)
                .exchange()
                .expectStatus().isNotFound();

        eventUpdateVerifier.verify();
    }
}
