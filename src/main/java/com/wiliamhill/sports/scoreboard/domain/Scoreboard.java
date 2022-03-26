package com.wiliamhill.sports.scoreboard.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.With;
import org.springframework.data.annotation.Id;

@Getter
@AllArgsConstructor
@Builder
public class Scoreboard {
    @Id
    private final Integer id;
    @With
    private final String event;
    @With
    private final String score;
}
