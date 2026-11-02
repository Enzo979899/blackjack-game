package dev.enzo.blackjack.player.domain;

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

}