package dev.enzo.blackjack.player.application.port.out;

import dev.enzo.blackjack.player.domain.model.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository {

    void save(Player player);

    Optional<Player> findById(UUID playerId);

    List<Player> findAll();
}
