package entity.events;

import java.time.LocalDateTime;

public class FailedDeliveryEvent extends Event {
    private final String reason;
    private final String evidencePath;

    public FailedDeliveryEvent(String eventId, LocalDateTime timestamp, String description, String reason, String evidencePath) {
        super(eventId, timestamp, description);
        this.reason = reason;
        this.evidencePath = evidencePath;
    }

    @Override
    public String getEventType() {
        return "FAILED_DELIVERY";
    }

    @Override
    public String getEventDetails() {
        return "reason=" + reason + ";evidencePath=" + evidencePath;
    }
}
