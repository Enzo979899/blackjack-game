package dev.enzo.blackjack.game.application.usecase;

import dev.enzo.blackjack.game.application.port.in.StandRoundUseCase;
import dev.enzo.blackjack.game.application.port.out.RoundRepository;
import dev.enzo.blackjack.game.domain.model.Round;

import java.util.Objects;
import java.util.UUID;

public class StandRoundService implements StandRoundUseCase {

    private final RoundRepository roundRepository;

    public StandRoundService(RoundRepository roundRepository) {
        this.roundRepository = Objects.requireNonNull(roundRepository);
    }

    @Override
    public void stand(UUID roundId) {
        Objects.requireNonNull(roundId, "Round ID cannot be null");

        Round round = roundRepository.findById(roundId).orElseThrow(() ->
                new IllegalArgumentException("Round not found: " + roundId));

        round.stand();

        roundRepository.save(round);
    }

}
