package entity.events;

import java.time.LocalDateTime;

public class ReturnedEvent extends Event {
    private final String warehouseId;

    public ReturnedEvent(String eventId, LocalDateTime timestamp, String description, String warehouseId) {
        super(eventId, timestamp, description);
        this.warehouseId = warehouseId;
    }

    @Override
    public String getEventType() {
        return "RETURNED";
    }

    @Override
    public String getEventDetails() {
        return "warehouseId=" + warehouseId;
    }
}
