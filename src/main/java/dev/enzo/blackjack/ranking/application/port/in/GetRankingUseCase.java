package dev.enzo.blackjack.ranking.application.port.in;

import dev.enzo.blackjack.ranking.application.dto.RankingEntry;

import java.util.List;

public interface GetRankingUseCase {

    List<RankingEntry> getRanking();

}
