package dev.lydtech.tracking.handler;

import dev.lydtech.dispatch.message.DispatchPreparing;
import dev.lydtech.tracking.service.TrackingService;
import dev.lydtech.tracking.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

class DispatchTrackingHandlerTest {

    TrackingService trackingServMock;

    DispatchTrackingHandler dispatchTrackingHandler;

    @BeforeEach
    void setUp() {
        trackingServMock = mock();
        dispatchTrackingHandler = new DispatchTrackingHandler(trackingServMock);
    }

    @Test
    void listen_success() throws ExecutionException, InterruptedException {
        DispatchPreparing dispatchPreparing = TestEventData.buildDispatchPreparingEvent(UUID.randomUUID());
        dispatchTrackingHandler.listen(dispatchPreparing);
        verify(trackingServMock).process(dispatchPreparing);
    }
    @Test
    void listen_throwsException() throws ExecutionException, InterruptedException {
        // Given
        DispatchPreparing dispatchPreparing = TestEventData.buildDispatchPreparingEvent(UUID.randomUUID());

        // When
        doThrow(RuntimeException.class).when(trackingServMock).process(any(DispatchPreparing.class));

        // Then
        dispatchTrackingHandler.listen(dispatchPreparing);
        verify(trackingServMock).process(dispatchPreparing);
    }
}