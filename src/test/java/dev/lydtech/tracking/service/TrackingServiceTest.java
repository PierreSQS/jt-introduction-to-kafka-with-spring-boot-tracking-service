package dev.lydtech.tracking.service;

import dev.lydtech.dispatch.message.DispatchCompleted;
import dev.lydtech.dispatch.message.DispatchPreparing;
import dev.lydtech.dispatch.message.TrackingStatusUpdated;
import dev.lydtech.tracking.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class TrackingServiceTest {

    public static final String TRACKING_PROCESS_FAILURE = "Tracking Process failure";
    KafkaTemplate<String,Object> kafkaTemplateMock;
    TrackingService trackingService;

    @BeforeEach
    void setUp() {
        kafkaTemplateMock = mock();
        trackingService = new TrackingService(kafkaTemplateMock);
    }

    @Test
    void process_DispatchPreparing_Success() throws ExecutionException, InterruptedException {
        given(kafkaTemplateMock.send(anyString(),any(TrackingStatusUpdated.class))).willReturn(mock());

        DispatchPreparing dispatchPreparing = TestEventData.buildDispatchPreparingEvent(UUID.randomUUID());

        trackingService.process(dispatchPreparing);
        verify(kafkaTemplateMock).send(eq(TrackingService.TRACKING_STATUS_TOPIC),any());
    }

    @Test
    void process_DispatchPreparing_ThrowsException() {
        // Given
        DispatchPreparing dispatchPreparing = TestEventData.buildDispatchPreparingEvent(UUID.randomUUID());

        // When
        doThrow(new RuntimeException(TRACKING_PROCESS_FAILURE))
                .when(kafkaTemplateMock).send(anyString(),any(TrackingStatusUpdated.class));


        // Then
        // Junit5 Exception Assertion
        Exception exception = assertThrows(RuntimeException.class, () ->
                trackingService.process(dispatchPreparing));

        // Junit5 Exception Message Assertion
        assertThat(exception.getMessage()).isEqualTo(TRACKING_PROCESS_FAILURE);


        // AssertJ Exception Assertion
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> trackingService.process(dispatchPreparing))
                .withMessage(TRACKING_PROCESS_FAILURE);

        // Verify 2 Calls because of the calls above
        verify(kafkaTemplateMock, times(2)).send(eq(TrackingService.TRACKING_STATUS_TOPIC),any());
    }

    @Test
    void process_DispatchCompleted_Success() throws ExecutionException, InterruptedException {
        DispatchCompleted dispatchCompleted = TestEventData.buildDispatchCompletedEvent(UUID.randomUUID());

        given(kafkaTemplateMock.send(eq(TrackingService.TRACKING_STATUS_TOPIC), any(TrackingStatusUpdated.class)))
                .willReturn(mock());

        trackingService.process(dispatchCompleted);

        verify(kafkaTemplateMock).send(eq(TrackingService.TRACKING_STATUS_TOPIC), any(TrackingStatusUpdated.class));
    }

    @Test
    void process_DispatchCompleted_ThrowsException() {
        DispatchCompleted dispatchCompleted = TestEventData.buildDispatchCompletedEvent(UUID.randomUUID());

        doThrow(new RuntimeException(TRACKING_PROCESS_FAILURE)).when(kafkaTemplateMock)
                .send(eq(TrackingService.TRACKING_STATUS_TOPIC), any(TrackingStatusUpdated.class));

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> trackingService.process(dispatchCompleted))
                .withMessage(TRACKING_PROCESS_FAILURE);

        verify(kafkaTemplateMock).send(eq(TrackingService.TRACKING_STATUS_TOPIC), any(TrackingStatusUpdated.class));
    }
}