package entity.events;

import java.time.LocalDateTime;

public class DeliveryConfirmationEvent extends Event {
    private final String confirmationDetails;

    public DeliveryConfirmationEvent(String eventId, LocalDateTime timestamp, String description, String confirmationDetails) {
        super(eventId, timestamp, description);
        this.confirmationDetails = confirmationDetails;
    }

    @Override
    public String getEventType() {
        return "DELIVERY_CONFIRMATION";
    }

    @Override
    public String getEventDetails() {
        return "confirmation=" + confirmationDetails;
    }
}
