package dev.lydtech.tracking.integration;

import dev.lydtech.configuration.TrackingConfiguration;
import dev.lydtech.dispatch.message.TrackingStatusUpdated;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@SpringBootTest(classes = {TrackingConfiguration.class})
@ActiveProfiles("test")
@EmbeddedKafka(controlledShutdown = true)
class DispatchTrackingIntegrationTest {

    private final static String DISPATCH_TRACKING_TOPIC = "dispatch.tracking";

    private final static String TRACKING_STATUS_TOPIC = "tracking.status";

    @Autowired
    private KafkaTestListener kafkaTestListener;

    @Configuration
    static class TestConfig {
        @Bean
        public KafkaTestListener kafkaTestListener() {
            return new KafkaTestListener();
        }
    }

    public static class KafkaTestListener {
        AtomicInteger trackingStatusCounter = new AtomicInteger(0);

        @KafkaListener(groupId = "KafkaIntegrationTest",topics = {TRACKING_STATUS_TOPIC})
        void receiveTrackingStatus(@Payload TrackingStatusUpdated trackingStatusUpdated) {
            log.info("Receive TrackingStatusUpdated Payload: {}",trackingStatusUpdated);
            trackingStatusCounter.incrementAndGet();
        }
    }

    @Test
    void testDispatchTrackingFlow() {
    }
}
