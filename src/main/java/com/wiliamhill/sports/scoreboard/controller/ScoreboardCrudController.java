package com.wiliamhill.sports.scoreboard.controller;

import com.wiliamhill.sports.scoreboard.domain.Scoreboard;
import com.wiliamhill.sports.scoreboard.domain.ScoreboardEvent;
import com.wiliamhill.sports.scoreboard.repository.ScoreboardRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/scoreboard")
public class ScoreboardCrudController {

    private final ScoreboardRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    
    public ScoreboardCrudController(ScoreboardRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Flux<Scoreboard> getAllScoreboards() {
        return repository.findAll();
    }
    
    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Scoreboard>> getScoreboardById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Scoreboard> createScoreboard(@RequestBody Scoreboard scoreboard) {
        return repository.save(Scoreboard.builder()
                .event(scoreboard.getEvent())
                .score(scoreboard.getScore())
                .build()
        );
    }

    @PutMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Scoreboard>> updateScoreboard(@PathVariable Integer id, 
                                                             @RequestBody Scoreboard scoreboard) {
        return repository.findById(id)
                .flatMap(existingScoreboard -> repository.save(existingScoreboard
                        .withScore(scoreboard.getScore())
                        .withEvent(scoreboard.getEvent()))
                )
                .doOnNext(updatedScoreboard -> eventPublisher.publishEvent(
                        new ScoreboardEvent(this, updatedScoreboard)))
                .map(ResponseEntity::ok)    
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
