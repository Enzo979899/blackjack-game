package dev.enzo.blackjack.game.application.dto;

import dev.enzo.blackjack.game.domain.model.RoundStatus;

import java.util.UUID;

public record StartRoundResult(UUID gameId, RoundStatus status) {

}
