package dev.lydtech.tracking.handler;

import dev.lydtech.dispatch.message.DispatchCompleted;
import dev.lydtech.dispatch.message.DispatchPreparing;
import dev.lydtech.tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@KafkaListener(
        id = "dispatchTrackingConsumerClient",
        topics = "dispatch.tracking",
        groupId = "tracking.dispatch.tracking.consumer",
        containerFactory = "kafkaListenerContainerFactory"
)
public class DispatchTrackingHandler {

    private final TrackingService trackingService;


    @KafkaHandler
    public void listen (DispatchPreparing payload) {
        log.info("Received Tracking Payload: {}",payload);

        try {
            trackingService.process(payload);
        } catch (Exception e) {
            log.error("Tracking Process failure: ",e);
            /* Clean up whatever needs to be handled before interrupting  */
            Thread.currentThread().interrupt();

        }
    }

    @KafkaHandler
    public void listen (DispatchCompleted payload) {
        log.info("Received Tracking Payload: {}",payload);

        try {
            trackingService.process(payload);
        } catch (Exception e) {
            log.error("Tracking Process failure: ",e);
            /* Clean up whatever needs to be handled before interrupting  */
            Thread.currentThread().interrupt();

        }
    }
}
