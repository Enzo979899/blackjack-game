package dev.enzo.blackjack.game.application.port.out;

import dev.enzo.blackjack.game.domain.model.Game;

import java.util.Optional;
import java.util.UUID;

public interface GameRepository {

    void save(UUID gameId, Game game);

    Optional<Game> findById(UUID gameId);

}
