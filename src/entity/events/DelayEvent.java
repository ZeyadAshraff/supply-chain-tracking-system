package entity.events;

import java.time.LocalDateTime;

public class DelayEvent extends Event {
    private final String delayReason;

    public DelayEvent(String eventId, LocalDateTime timestamp, String description, String delayReason) {
        super(eventId, timestamp, description);
        this.delayReason = delayReason;
    }

    @Override
    public String getEventType() {
        return "DELAY";
    }

    @Override
    public String getEventDetails() {
        return "delayReason=" + delayReason;
    }
}
