package dev.enzo.blackjack.game.application.usecase;

import dev.enzo.blackjack.game.application.dto.StartRoundResult;
import dev.enzo.blackjack.game.application.port.in.StartRoundUseCase;
import dev.enzo.blackjack.game.application.port.out.GameRepository;
import dev.enzo.blackjack.game.domain.model.Game;
import dev.enzo.blackjack.game.domain.model.Round;
import dev.enzo.blackjack.game.domain.model.RoundStatus;

import java.util.Objects;
import java.util.UUID;

public class StartRoundService implements StartRoundUseCase {

    private final GameRepository gameRepository;

    public StartRoundService(GameRepository gameRepository) {
        this.gameRepository = Objects.requireNonNull(gameRepository);
    }

    @Override
    public StartRoundResult startRound(UUID gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new IllegalArgumentException("Game not found!"));
        game.startRound();

        Round round = game.getCurrentRound();
        round.start();

        if (round.getStatus() == RoundStatus.FINISHED) {
            game.recordRoundResult();
        }


        gameRepository.save(gameId, game);

        return new StartRoundResult(gameId, round.getStatus());
    }
}
