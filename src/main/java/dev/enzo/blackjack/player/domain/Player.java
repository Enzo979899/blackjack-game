package dev.enzo.blackjack.player.domain;

import java.util.Objects;
import java.util.UUID;

public class Player {
    private final UUID id;
    private final String name;

    public Player(UUID id, String name) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.name = Objects.requireNonNull(name, "Player name cannot be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Player name cannot be blank");
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
