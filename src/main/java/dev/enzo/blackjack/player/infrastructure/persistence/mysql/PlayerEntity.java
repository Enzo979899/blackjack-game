package dev.enzo.blackjack.player.infrastructure.persistence.mysql;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("players")
public class PlayerEntity {

    @Id
    private String id;
    private String name;
    private int wins;
    private int losses;
    private int draws;

    public PlayerEntity(String id, String name, int wins, int losses, int draws) {
        this.id = id;
        this.name = name;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getDraws() {
        return draws;
    }
}
