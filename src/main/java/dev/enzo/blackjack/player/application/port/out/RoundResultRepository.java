package dev.enzo.blackjack.player.application.port.out;

import dev.enzo.blackjack.game.domain.model.RoundResult;

import java.util.UUID;

public interface RoundResultRepository {

    boolean recordIfAbsent(UUID roundId, UUID playerId, RoundResult result);

}
