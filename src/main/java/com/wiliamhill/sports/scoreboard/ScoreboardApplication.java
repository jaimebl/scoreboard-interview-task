package com.wiliamhill.sports.scoreboard;

import com.wiliamhill.sports.scoreboard.domain.Scoreboard;
import com.wiliamhill.sports.scoreboard.repository.ScoreboardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ScoreboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScoreboardApplication.class, args);
	}

	@Bean
	CommandLineRunner initData(ScoreboardRepository repository) {
		return args -> Flux.just(
						Scoreboard.builder().event("Real Zaragoza vs Amorebieta").score("1-1").build(),
						Scoreboard.builder().event("Spain vs Albania").score("2-1").build())
				.flatMap(repository::save)
				.subscribe();
	}
}
