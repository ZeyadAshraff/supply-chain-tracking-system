package entity.events;

import java.time.LocalDateTime;

public class ReturnDispatchedEvent extends Event {
    private final String vehicleId;

    public ReturnDispatchedEvent(String eventId, LocalDateTime timestamp, String description, String vehicleId) {
        super(eventId, timestamp, description);
        this.vehicleId = vehicleId;
    }

    @Override
    public String getEventType() {
        return "RETURN_DISPATCHED";
    }

    @Override
    public String getEventDetails() {
        return "vehicleId=" + vehicleId;
    }
}
