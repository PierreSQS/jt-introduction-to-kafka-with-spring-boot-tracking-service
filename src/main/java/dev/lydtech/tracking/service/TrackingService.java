package dev.lydtech.tracking.service;

import dev.lydtech.dispatch.message.DispatchPreparing;
import dev.lydtech.dispatch.message.Status;
import dev.lydtech.dispatch.message.TrackingStatusUpdated;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackingService {

    public static final String TRACKING_STATUS_TOPIC = "tracking.status";

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void process(DispatchPreparing dispatchPreparing) {
        TrackingStatusUpdated trackingStatusUpdated =
                TrackingStatusUpdated
                        .builder()
                        .orderId(dispatchPreparing.getOrderId())
                        .status(Status.PREPARING)
                        .build();

        kafkaTemplate.send(TRACKING_STATUS_TOPIC, trackingStatusUpdated);

    }


}
