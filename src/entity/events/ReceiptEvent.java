package entity.events;

import java.time.LocalDateTime;

public class ReceiptEvent extends Event {
    private final String receivedCondition;

    public ReceiptEvent(String eventId, LocalDateTime timestamp, String description, String receivedCondition) {
        super(eventId, timestamp, description);
        this.receivedCondition = receivedCondition;
    }

    @Override
    public String getEventType() {
        return "RECEIPT";
    }

    @Override
    public String getEventDetails() {
        return "receivedCondition=" + receivedCondition;
    }
}
