package com.wiliamhill.sports.scoreboard.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class ScoreboardEvent extends ApplicationEvent {
    
    private final transient Event event;
    
    public ScoreboardEvent(Object source, Scoreboard scoreboard) {
        super(source);
        this.event = new Event(UUID.randomUUID(), scoreboard);
    }

    @Getter
    @AllArgsConstructor
    public static class Event {
        private final UUID id;
        private final Scoreboard scoreboard;
    }
}
