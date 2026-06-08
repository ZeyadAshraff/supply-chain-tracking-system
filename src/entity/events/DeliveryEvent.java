package entity.events;

import entity.DeliveryEvidence;
import java.time.LocalDateTime;

public class DeliveryEvent extends Event {
    private final DeliveryEvidence deliveryEvidence;

    public DeliveryEvent(String eventId, LocalDateTime timestamp, String description, DeliveryEvidence deliveryEvidence) {
        super(eventId, timestamp, description);
        this.deliveryEvidence = deliveryEvidence;
    }

    public DeliveryEvidence getDeliveryEvidence() {
        return deliveryEvidence;
    }

    @Override
    public String getEventType() {
        return "DELIVERY";
    }

    @Override
    public String getEventDetails() {
        return "evidenceId=" + deliveryEvidence.getEvidenceId() + ";path=" + deliveryEvidence.getFilePath();
    }
}
