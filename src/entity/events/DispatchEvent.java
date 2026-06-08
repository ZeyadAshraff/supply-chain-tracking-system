package entity.events;

import java.time.LocalDateTime;

public class DispatchEvent extends Event {
    private final String vehicleId;

    public DispatchEvent(String eventId, LocalDateTime timestamp, String description, String vehicleId) {
        super(eventId, timestamp, description);
        this.vehicleId = vehicleId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    @Override
    public String getEventType() {
        return "DISPATCH";
    }

    @Override
    public String getEventDetails() {
        return "vehicleId=" + vehicleId;
    }
}
