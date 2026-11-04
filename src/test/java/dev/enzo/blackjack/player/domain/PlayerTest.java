package dev.enzo.blackjack.player.domain;

import dev.enzo.blackjack.player.domain.model.Player;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void shouldCreatePlayerWithIdAndName() {
        UUID playerId = UUID.randomUUID();
        String name = "Enzo";
        Player player = new Player(playerId, name);

        assertEquals(playerId, player.getId());
        assertEquals(name, player.getName());
    }

    @Test
    void shouldRejectNullIdAndName() {
        UUID playerId = UUID.randomUUID();

        assertThrows(NullPointerException.class, () -> new Player(null, "Carlos"));
        assertThrows(NullPointerException.class, () -> new Player(playerId, null));
    }

    @Test
    void shouldRejectBlankPlayerName() {
        UUID playerId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () -> new Player(playerId, ""));
        assertThrows(IllegalArgumentException.class, () -> new Player(playerId, "   "));
    }

    @Test
    void shouldRecordWin() {
        Player player = new Player(UUID.randomUUID(), "Enzo");

        player.recordWin();

        assertEquals(1, player.getWins());
        assertEquals(0, player.getLosses());
        assertEquals(0, player.getDraws());
    }

    @Test
    void shouldRecordLoss() {
        Player player = new Player(UUID.randomUUID(), "Enzo");

        player.recordLoss();

        assertEquals(0, player.getWins());
        assertEquals(1, player.getLosses());
        assertEquals(0, player.getDraws());
    }

    @Test
    void shouldRecordDraw() {
        Player player = new Player(UUID.randomUUID(), "Enzo");

        player.recordDraw();

        assertEquals(0, player.getWins());
        assertEquals(0, player.getLosses());
        assertEquals(1, player.getDraws());
    }

    @Test
    void shouldAccumulateRoundResults() {
        Player player = new Player(UUID.randomUUID(), "Enzo");

        player.recordWin();
        player.recordWin();
        player.recordLoss();
        player.recordDraw();

        assertEquals(2, player.getWins());
        assertEquals(1, player.getLosses());
        assertEquals(1, player.getDraws());
    }

}