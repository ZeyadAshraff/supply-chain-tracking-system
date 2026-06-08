package entity.events;

import java.time.LocalDateTime;

public class ShipmentAcceptanceEvent extends Event {
    private final String acceptedBy;

    public ShipmentAcceptanceEvent(String eventId, LocalDateTime timestamp, String description, String acceptedBy) {
        super(eventId, timestamp, description);
        this.acceptedBy = acceptedBy;
    }

    @Override
    public String getEventType() {
        return "SHIPMENT_ACCEPTED";
    }

    @Override
    public String getEventDetails() {
        return "acceptedBy=" + acceptedBy;
    }
}
