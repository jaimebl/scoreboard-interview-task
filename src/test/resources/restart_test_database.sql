DROP TABLE scoreboard;
CREATE TABLE scoreboard (id SERIAL PRIMARY KEY, event VARCHAR(255), score VARCHAR(255));
INSERT INTO scoreboard (event, score) VALUES ('Team 1 vs Team 2', '0-1');
INSERT INTO scoreboard (event, score) VALUES ('Team 3 vs Team 4', '3-3');
