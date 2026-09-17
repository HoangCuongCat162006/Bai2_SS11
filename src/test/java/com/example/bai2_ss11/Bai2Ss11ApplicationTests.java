package com.example.bai2_ss11;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(partitions = 5, topics = {"storex-order-events"})
class Bai2Ss11ApplicationTests {

    @Test
    void contextLoads() {
    }

}
