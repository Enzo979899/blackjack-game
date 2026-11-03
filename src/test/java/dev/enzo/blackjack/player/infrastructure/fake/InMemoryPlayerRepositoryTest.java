package dev.enzo.blackjack.player.infrastructure.fake;

import dev.enzo.blackjack.player.domain.model.Player;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryPlayerRepositoryTest {

    @Test
    void shouldSaveAndFindPlayerById() {
        InMemoryPlayerRepository repository = new InMemoryPlayerRepository();
        UUID playerId = UUID.randomUUID();
        Player player = new Player(playerId, "Carla");

        repository.save(player);
        Optional<Player> result = repository.findById(playerId);

        assertTrue(result.isPresent());
        assertSame(player, result.get());
    }

    @Test
    void shouldReturnEmptyWhenPlayerDoesNotExist() {
        InMemoryPlayerRepository repository = new InMemoryPlayerRepository();
        UUID playerId = UUID.randomUUID();
        Optional<Player> result = repository.findById(playerId);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldStoreMultiplePlayersIndependently() {
        InMemoryPlayerRepository repository = new InMemoryPlayerRepository();

        UUID firstPlayerId = UUID.randomUUID();
        Player firstPlayer = new Player(firstPlayerId, "Enzo");
        UUID secondPlayerId = UUID.randomUUID();
        Player secondPlayer = new Player(secondPlayerId, "Carla");

        repository.save(firstPlayer);
        repository.save(secondPlayer);

        assertSame(firstPlayer, repository.findById(firstPlayerId).orElseThrow());
        assertSame(secondPlayer, repository.findById(secondPlayerId).orElseThrow());
    }
}
