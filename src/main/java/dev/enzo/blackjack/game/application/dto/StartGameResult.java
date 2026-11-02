package dev.enzo.blackjack.game.application.dto;

import dev.enzo.blackjack.game.domain.model.Deck;
import dev.enzo.blackjack.game.domain.model.GameStatus;
import dev.enzo.blackjack.game.domain.model.Hand;

import java.util.UUID;

public record StartGameResult(UUID gameId, GameStatus status, Hand player, Hand dealer) {

}
