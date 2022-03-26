package com.wiliamhill.sports.scoreboard.controller;

import com.wiliamhill.sports.scoreboard.domain.ScoreboardEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.MediaType;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@RestController
@RequestMapping("/scoreboard/events")
public class ScoreboardEventsController implements ApplicationListener<ScoreboardEvent> {

    private final SubscribableChannel subscribableChannel = MessageChannels.publishSubscribe("scoreboard-events").get();

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ScoreboardEvent.Event> getScoreboardEvents() {
        return Flux.create(sink -> {
            MessageHandler messageHandler = message -> sink.next(((ScoreboardEvent) message.getPayload()).getEvent());
            sink.onCancel(() -> subscribableChannel.unsubscribe(messageHandler));
            subscribableChannel.subscribe(messageHandler);
        }, FluxSink.OverflowStrategy.LATEST);
    }
    
    @Override
    public void onApplicationEvent(ScoreboardEvent scoreboardEvent) {
        subscribableChannel.send(new GenericMessage<>(scoreboardEvent));
    }
}
