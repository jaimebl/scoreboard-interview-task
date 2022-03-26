package com.wiliamhill.sports.scoreboard.repository;

import com.wiliamhill.sports.scoreboard.domain.Scoreboard;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ScoreboardRepository extends ReactiveCrudRepository<Scoreboard, Integer> {}
