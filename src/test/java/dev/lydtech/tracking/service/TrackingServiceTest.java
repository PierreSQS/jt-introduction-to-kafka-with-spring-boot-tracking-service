package dev.lydtech.tracking.service;

import dev.lydtech.dispatch.message.DispatchPreparing;
import dev.lydtech.tracking.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
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
    void process_Success() throws ExecutionException, InterruptedException {
        given(kafkaTemplateMock.send(anyString(),any(DispatchPreparing.class))).willReturn(mock());

        DispatchPreparing dispatchPreparing = TestEventData.buildDispatchPreparingEvent(UUID.randomUUID());

        trackingService.process(dispatchPreparing);
        verify(kafkaTemplateMock).send(eq(TrackingService.TRACKING_STATUS_TOPIC),any());
    }

    @Disabled
    @Test
    void process_ThrowsException() throws ExecutionException, InterruptedException {
        DispatchPreparing dispatchPreparing = TestEventData.buildDispatchPreparingEvent(UUID.randomUUID());
        trackingService.process(dispatchPreparing);
        verify(kafkaTemplateMock).send(eq(TrackingService.TRACKING_STATUS_TOPIC),any());
    }
}