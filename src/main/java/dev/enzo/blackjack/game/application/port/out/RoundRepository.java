package dev.enzo.blackjack.game.application.port.out;

import dev.enzo.blackjack.game.domain.model.Round;

import java.util.Optional;
import java.util.UUID;

public interface RoundRepository {

    void save(Round round);

    Optional<Round> findById(UUID roundId);

}
