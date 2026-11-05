package dev.enzo.blackjack;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.r2dbc.autoconfigure.R2dbcAutoConfiguration," +
                "org.springframework.boot.mongodb.autoconfigure.MongoReactiveAutoConfiguration," +
                "org.springframework.boot.data.mongodb.autoconfigure.DataMongoReactiveAutoConfiguration"
})

class BlackjackGameApplicationTests {

    @Test
    void contextLoads() {
    }

}
