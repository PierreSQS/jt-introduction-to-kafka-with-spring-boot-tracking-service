package dev.lydtech.tracking.handler;

import dev.lydtech.dispatch.message.DispatchCompleted;
import dev.lydtech.dispatch.message.DispatchPreparing;
import dev.lydtech.tracking.service.TrackingService;
import dev.lydtech.tracking.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

class DispatchTrackingHandlerTest {

    private static final String TRACKING_FAILURE = "Tracking Process failure: ";

    TrackingService trackingServMock;

    DispatchTrackingHandler dispatchTrackingHandler;

    DispatchPreparing dispatchPreparing;

    DispatchCompleted dispatchCompleted;

    @BeforeEach
    void setUp() {
        trackingServMock = mock();
        dispatchTrackingHandler = new DispatchTrackingHandler(trackingServMock);
        dispatchPreparing = TestEventData.buildDispatchPreparingEvent(UUID.randomUUID());
        dispatchCompleted = TestEventData.buildDispatchCompletedEvent(UUID.randomUUID());
    }

    @Test
    void listen_DispatchPreparing_success() throws ExecutionException, InterruptedException {
        dispatchTrackingHandler.listen(dispatchPreparing);
        verify(trackingServMock).process(dispatchPreparing);
    }
    @Test
    void listen_DispatchPreparing_throwsException() throws ExecutionException, InterruptedException {
        // When
        doThrow(RuntimeException.class).when(trackingServMock).process(any(DispatchPreparing.class));

        // Then
        dispatchTrackingHandler.listen(dispatchPreparing);
        verify(trackingServMock).process(dispatchPreparing);
    }

    @Test
    void listen_DispatchCompleted_success() throws ExecutionException, InterruptedException {
        dispatchTrackingHandler.listen(dispatchCompleted);
        verify(trackingServMock).process(dispatchCompleted);
    }

    @Test
    void listen_DispatchCompleted_throwsException() throws ExecutionException, InterruptedException {
        doThrow(new RuntimeException(TRACKING_FAILURE)).when(trackingServMock).process(dispatchCompleted);

        dispatchTrackingHandler.listen(dispatchCompleted);
        verify(trackingServMock).process(dispatchCompleted);
    }

}