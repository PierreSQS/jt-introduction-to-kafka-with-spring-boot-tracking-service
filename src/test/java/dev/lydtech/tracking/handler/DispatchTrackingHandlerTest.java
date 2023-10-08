package dev.lydtech.tracking.handler;

import dev.lydtech.dispatch.message.DispatchPreparing;
import dev.lydtech.tracking.service.TrackingService;
import dev.lydtech.tracking.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DispatchTrackingHandlerTest {

    TrackingService trackingServMock;

    DispatchTrackingHandler dispatchTrackingHandler;

    @BeforeEach
    void setUp() {
        trackingServMock = mock();
        dispatchTrackingHandler = new DispatchTrackingHandler(trackingServMock);
    }

    @Test
    void listen() throws ExecutionException, InterruptedException {
        DispatchPreparing dispatchPreparing = TestEventData.buildDispatchPreparingEvent(UUID.randomUUID());
        dispatchTrackingHandler.listen(dispatchPreparing);
        verify(trackingServMock).process(dispatchPreparing);
    }
}