package dev.enzo.blackjack.player.infrastructure.fake;

import dev.enzo.blackjack.game.domain.model.RoundResult;
import dev.enzo.blackjack.player.application.port.out.PlayerRepository;
import dev.enzo.blackjack.player.domain.model.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryRoundResultRepositoryTest {
    private PlayerRepository playerRepository;
    private InMemoryRoundResultRepository resultRepository;
    private UUID playerId;
    private Player player;

    @BeforeEach
    void setUp() {
        playerRepository = new InMemoryPlayerRepository();
        resultRepository = new InMemoryRoundResultRepository(playerRepository);
        playerId = UUID.randomUUID();
        player = new Player(playerId, "Enzo");

        playerRepository.save(player);
    }

    @Test
    void shouldRecordPlayerWin() {
        UUID roundId = UUID.randomUUID();
        boolean recorded = resultRepository.recordIfAbsent(roundId, playerId, RoundResult.PLAYER_WIN);

        assertTrue(recorded);
        assertEquals(1, player.getWins());
        assertEquals(0, player.getLosses());
        assertEquals(0, player.getDraws());
    }

    @Test
    void shouldRecordPlayerLoss() {
        UUID roundId = UUID.randomUUID();
        boolean recorded = resultRepository.recordIfAbsent(roundId, playerId, RoundResult.DEALER_WIN);

        assertTrue(recorded);
        assertEquals(0, player.getWins());
        assertEquals(1, player.getLosses());
        assertEquals(0, player.getDraws());
    }

    @Test
    void shouldRecordPlayerDraw() {
        UUID roundId = UUID.randomUUID();
        boolean recorded = resultRepository.recordIfAbsent(roundId, playerId, RoundResult.PUSH);

        assertTrue(recorded);
        assertEquals(0, player.getWins());
        assertEquals(0, player.getLosses());
        assertEquals(1, player.getDraws());
    }

    @Test
    void shouldNotRecordSameRoundTwice() {
        UUID roundId = UUID.randomUUID();
        boolean firstResult = resultRepository.recordIfAbsent(roundId, playerId, RoundResult.PLAYER_WIN);
        boolean secondResult = resultRepository.recordIfAbsent(roundId, playerId, RoundResult.PLAYER_WIN);

        assertTrue(firstResult);
        assertFalse(secondResult);
        assertEquals(1, player.getWins());
        assertEquals(0, player.getLosses());
        assertEquals(0, player.getDraws());
    }

    @Test
    void shouldAccumulateResultsFromDifferentRounds() {
        resultRepository.recordIfAbsent(UUID.randomUUID(), playerId, RoundResult.PLAYER_WIN);
        resultRepository.recordIfAbsent(UUID.randomUUID(), playerId, RoundResult.PLAYER_WIN);
        resultRepository.recordIfAbsent(UUID.randomUUID(), playerId, RoundResult.DEALER_WIN);
        resultRepository.recordIfAbsent(UUID.randomUUID(), playerId, RoundResult.PUSH);

        assertEquals(2, player.getWins());
        assertEquals(1, player.getLosses());
        assertEquals(1, player.getDraws());
    }

    @Test
    void shouldRejectNonExistingPlayer() {
        UUID roundId = UUID.randomUUID();
        UUID unknownPlayerId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class,
                () -> resultRepository.recordIfAbsent(roundId, unknownPlayerId, RoundResult.PLAYER_WIN));
        assertEquals(0, player.getWins());
    }

    @Test
    void shouldRejectNullArguments() {
        UUID roundId = UUID.randomUUID();

        assertThrows(NullPointerException.class,
                () -> resultRepository.recordIfAbsent(null, playerId, RoundResult.PLAYER_WIN));
        assertThrows(NullPointerException.class,
                () -> resultRepository.recordIfAbsent(roundId, null, RoundResult.PLAYER_WIN));
        assertThrows(NullPointerException.class,
                () -> resultRepository.recordIfAbsent(roundId, playerId, null));
    }

}
