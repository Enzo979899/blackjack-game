package dev.enzo.blackjack.game.application.dto;

import dev.enzo.blackjack.game.domain.model.GameStatus;

import java.util.UUID;

public record StartGameResult(UUID gameId, GameStatus status) {

}
