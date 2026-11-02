package dev.enzo.blackjack.game.application.usecase;

import dev.enzo.blackjack.game.application.dto.StartGameResult;
import dev.enzo.blackjack.game.application.port.in.StartGameUseCase;
import dev.enzo.blackjack.game.application.port.out.GameRepository;
import dev.enzo.blackjack.game.domain.model.Deck;
import dev.enzo.blackjack.game.domain.model.Game;
import dev.enzo.blackjack.game.domain.model.Round;

import java.util.Objects;
import java.util.UUID;

public class StartGameService implements StartGameUseCase {
    private final GameRepository gameRepository;

    public StartGameService(GameRepository gameRepository) {
        this.gameRepository = Objects.requireNonNull(gameRepository);
    }

    @Override
    public StartGameResult startGame() {
        Deck deck = new Deck();
        deck.shuffle();

        UUID gameId = UUID.randomUUID();
        Round game = new Round(deck);

        game.start();

        // comprobar si ha hagut blackjack natural

        gameRepository.save(gameId, game); // repo correcto cuando este corregido
        // enviar un evento a algo que aumenti el ranking

        return new StartGameResult(gameId, game.getStatus());
    }
}
