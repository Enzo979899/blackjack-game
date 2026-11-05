package dev.enzo.blackjack.ranking.infrastructure.fake;

import dev.enzo.blackjack.player.domain.model.Player;
import dev.enzo.blackjack.player.infrastructure.fake.InMemoryPlayerRepository;
import dev.enzo.blackjack.ranking.application.dto.RankingEntry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryRankingRepositoryTest {
    private InMemoryPlayerRepository playerRepository;
    private InMemoryRankingRepository rankingRepository;

    @BeforeEach
    void setUp() {
        playerRepository = new InMemoryPlayerRepository();
        rankingRepository = new InMemoryRankingRepository(playerRepository);
    }

    @Test
    void shouldReturnEmptyListWhenNoPlayersExist() {
        List<RankingEntry> entries = rankingRepository.findAll();

        assertTrue(entries.isEmpty());
    }

    @Test
    void shouldConvertPlayerToRankingEntry() {
        UUID playerId = UUID.randomUUID();
        Player player = new Player(playerId, "Enzo");

        playerRepository.save(player);

        List<RankingEntry> entries = rankingRepository.findAll();

        assertEquals(1, entries.size());

        RankingEntry entry = entries.getFirst();

        assertAll(() -> assertEquals(playerId, entry.playerId()),
                () -> assertEquals("Enzo", entry.playerName()), () -> assertEquals(0, entry.wins()),
                () -> assertEquals(0, entry.losses()), () -> assertEquals(0, entry.draws()));
    }

    @Test
    void shouldReturnAllPlayersAsRankingEntries() {
        Player enzo = new Player(UUID.randomUUID(), "Enzo");
        Player carla = new Player(UUID.randomUUID(), "Carla");

        playerRepository.save(enzo);
        playerRepository.save(carla);

        List<RankingEntry> entries = rankingRepository.findAll();

        assertEquals(2, entries.size());
        assertTrue(entries.stream().anyMatch(entry -> entry.playerId().equals(enzo.getId())));
        assertTrue(entries.stream().anyMatch(entry -> entry.playerId().equals(carla.getId())));
    }

    @Test
    void shouldReflectUpdatedPlayerStatistics() {
        Player player = new Player(UUID.randomUUID(), "Enzo");

        playerRepository.save(player);

        player.recordWin();
        player.recordWin();
        player.recordLoss();
        player.recordDraw();

        playerRepository.save(player);

        RankingEntry entry = rankingRepository.findAll().getFirst();

        assertAll(() -> assertEquals(2, entry.wins()), () -> assertEquals(1, entry.losses()),
                () -> assertEquals(1, entry.draws()));
    }

    @Test
    void shouldReturnSnapshotOfPlayerStatistics() {
        Player player = new Player(UUID.randomUUID(), "Enzo");

        playerRepository.save(player);

        RankingEntry originalEntry = rankingRepository.findAll().getFirst();

        player.recordWin();
        playerRepository.save(player);

        RankingEntry updatedEntry = rankingRepository.findAll().getFirst();

        assertEquals(0, originalEntry.wins());
        assertEquals(1, updatedEntry.wins());
    }

    @Test
    void shouldRejectNullPlayerRepository() {
        assertThrows(NullPointerException.class, () -> new InMemoryRankingRepository(null));
    }

}
