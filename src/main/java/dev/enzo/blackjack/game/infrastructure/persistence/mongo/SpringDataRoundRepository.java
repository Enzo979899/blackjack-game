package dev.enzo.blackjack.game.infrastructure.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

interface SpringDataRoundRepository extends MongoRepository<RoundDocument, String> {

}
