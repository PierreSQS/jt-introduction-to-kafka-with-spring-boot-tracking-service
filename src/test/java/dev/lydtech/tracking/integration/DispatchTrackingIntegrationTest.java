package dev.lydtech.tracking.integration;

import dev.lydtech.configuration.TrackingConfiguration;
import dev.lydtech.dispatch.message.DispatchPreparing;
import dev.lydtech.dispatch.message.TrackingStatusUpdated;
import dev.lydtech.tracking.util.TestEventData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@SpringBootTest(classes = {TrackingConfiguration.class})
@ActiveProfiles("test")
@EmbeddedKafka(controlledShutdown = true)
class DispatchTrackingIntegrationTest {

    private final static String DISPATCH_TRACKING_TOPIC = "dispatch.tracking";

    private final static String TRACKING_STATUS_TOPIC = "tracking.status";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

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

    @BeforeEach
    void setUp() {
        // initialize trackingStatusCounter
        kafkaTestListener.trackingStatusCounter.set(0);
    }

    @Test
    void testDispatchTrackingFlow() throws Exception{
        DispatchPreparing dispatchPreparing = TestEventData.buildDispatchPreparingEvent(UUID.randomUUID());
        send(DISPATCH_TRACKING_TOPIC,dispatchPreparing);

        // WAIT FOR KAFKA LISTENER COUNTER TO BE INCREASED TO 1
        await().atMost(3, TimeUnit.SECONDS).pollDelay(100, TimeUnit.MILLISECONDS)
                .until(kafkaTestListener.trackingStatusCounter::get, equalTo(1));
    }

    private void send(String topic, Object payloadData) throws Exception{
        kafkaTemplate.send(MessageBuilder
                .withPayload(payloadData)
                .setHeader(KafkaHeaders.TOPIC,topic)
                .build()).get();
    }
}
