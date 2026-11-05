package dev.enzo.blackjack.ranking.application.port.out;

import dev.enzo.blackjack.ranking.application.dto.RankingEntry;

import java.util.List;

public interface RankingRepository {

    List<RankingEntry> findAll();

}
