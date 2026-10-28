package dev.enzo.blackjack.game.infrastructure.fake;

import dev.enzo.blackjack.game.domain.model.Game;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryGameRepositoryTest {

    @Test
    void shouldSaveAndFindGameById() {
        InMemoryGameRepository repository = new InMemoryGameRepository();
        UUID gameId = UUID.randomUUID();
        Game game = new Game();

        repository.save(gameId, game);
        Optional<Game> result = repository.findById(gameId);

        assertTrue(result.isPresent());
        assertSame(game, result.get());
    }

    @Test
    void shouldReturnEmptyWhenGameDoesNotExist() {
        InMemoryGameRepository repository = new InMemoryGameRepository();
        UUID gameId = UUID.randomUUID();

        Optional<Game> result = repository.findById(gameId);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldStoreMultipleGamesIndependently() {
        InMemoryGameRepository repository = new InMemoryGameRepository();
        UUID firstGameId = UUID.randomUUID();
        UUID secondGameId = UUID.randomUUID();
        Game firstGame = new Game();
        Game secondGame = new Game();

        repository.save(firstGameId, firstGame);
        repository.save(secondGameId, secondGame);

        assertSame(firstGame, repository.findById(firstGameId).orElseThrow());
        assertSame(secondGame, repository.findById(secondGameId).orElseThrow());
    }
}
