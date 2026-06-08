package entity.events;

import java.time.LocalDateTime;

public class ShipmentCreatedEvent extends Event {
    private final String createdBy;

    public ShipmentCreatedEvent(String eventId, LocalDateTime timestamp, String description, String createdBy) {
        super(eventId, timestamp, description);
        this.createdBy = createdBy;
    }

    @Override
    public String getEventType() {
        return "SHIPMENT_CREATED";
    }

    @Override
    public String getEventDetails() {
        return "createdBy=" + createdBy;
    }
}
