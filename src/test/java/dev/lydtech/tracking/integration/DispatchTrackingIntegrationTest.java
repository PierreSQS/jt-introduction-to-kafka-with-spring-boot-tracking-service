package dev.lydtech.tracking.integration;

import dev.lydtech.configuration.TrackingConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest(classes = {TrackingConfiguration.class})
@ActiveProfiles("test")
@EmbeddedKafka(controlledShutdown = true)
class DispatchTrackingIntegrationTest {

    private final static String DISPATCH_TRACKING_TOPIC = "dispatch.tracking";

    private final static String TRACKING_STATUS_TOPIC = "tracking.status";

    @Test
    void testDispatchTrackingFlow() {
    }
}
