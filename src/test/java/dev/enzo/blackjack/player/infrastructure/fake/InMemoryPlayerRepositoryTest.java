package dev.enzo.blackjack.player.infrastructure.fake;

import dev.enzo.blackjack.player.domain.model.Player;
import org.junit.jupiter.api.Test;

import java.util.List;
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

    @Test
    void shouldReturnEmptyListWhenNoPlayersExist() {
        InMemoryPlayerRepository repository = new InMemoryPlayerRepository();

        List<Player> players = repository.findAll();

        assertTrue(players.isEmpty());
    }

    @Test
    void shouldReturnAllSavedPlayers() {
        InMemoryPlayerRepository repository = new InMemoryPlayerRepository();
        Player enzo = new Player(UUID.randomUUID(), "Enzo");
        Player carla = new Player(UUID.randomUUID(), "Carla");

        repository.save(enzo);
        repository.save(carla);

        List<Player> players = repository.findAll();

        assertEquals(2, players.size());
        assertTrue(players.contains(enzo));
        assertTrue(players.contains(carla));
    }

    @Test
    void shouldReturnUnmodifiableList() {
        InMemoryPlayerRepository repository = new InMemoryPlayerRepository();
        Player enzo = new Player(UUID.randomUUID(), "Enzo");

        repository.save(enzo);

        List<Player> players = repository.findAll();

        assertThrows(UnsupportedOperationException.class,
                () -> players.add(new Player(UUID.randomUUID(), "Carla")));
    }
}
