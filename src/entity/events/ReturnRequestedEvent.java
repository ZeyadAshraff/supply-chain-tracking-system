package entity.events;

import java.time.LocalDateTime;

public class ReturnRequestedEvent extends Event {
    private final String returnReason;
    private final String evidencePath;
    private final String returnWarehouseId;

    public ReturnRequestedEvent(String eventId, LocalDateTime timestamp, String description,
                                String returnReason, String evidencePath, String returnWarehouseId) {
        super(eventId, timestamp, description);
        this.returnReason = returnReason;
        this.evidencePath = evidencePath;
        this.returnWarehouseId = returnWarehouseId;
    }

    @Override
    public String getEventType() {
        return "RETURN_REQUESTED";
    }

    @Override
    public String getEventDetails() {
        return "returnReason=" + returnReason + ";evidencePath=" + evidencePath + ";returnWarehouseId=" + returnWarehouseId;
    }
}
