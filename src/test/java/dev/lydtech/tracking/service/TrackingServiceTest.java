package dev.lydtech.tracking.service;

import dev.lydtech.dispatch.message.DispatchPreparing;
import dev.lydtech.tracking.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TrackingServiceTest {

    KafkaTemplate<String,Object> kafkaTemplateMock;
    TrackingService trackingService;

    @BeforeEach
    void setUp() {
        kafkaTemplateMock = mock();
        trackingService = new TrackingService(kafkaTemplateMock);
    }

    @Test
    void process() {
        DispatchPreparing dispatchPreparing = TestEventData.buildDispatchPreparingEvent(UUID.randomUUID());
        trackingService.process(dispatchPreparing);
        verify(kafkaTemplateMock).send(eq(TrackingService.TRACKING_STATUS_TOPIC),any());
    }
}