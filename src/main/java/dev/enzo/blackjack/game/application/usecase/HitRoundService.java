package dev.enzo.blackjack.game.application.usecase;

import dev.enzo.blackjack.game.application.port.in.HitRoundUseCase;
import dev.enzo.blackjack.game.application.port.out.RoundRepository;
import dev.enzo.blackjack.game.domain.model.Round;

import java.util.Objects;
import java.util.UUID;

public class HitRoundService implements HitRoundUseCase {

    private final RoundRepository roundRepository;

    public HitRoundService(RoundRepository roundRepository) {
        this.roundRepository = Objects.requireNonNull(roundRepository);
    }

    @Override
    public void hit(UUID roundId) {
        Objects.requireNonNull(roundId, "Round ID cannot be null");

        Round round = roundRepository.findById(roundId).orElseThrow(() ->
                new IllegalArgumentException("Round not found: " + roundId));

        round.hit();

        roundRepository.save(round);
    }
}

