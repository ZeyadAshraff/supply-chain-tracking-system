package entity.events;

import java.time.LocalDateTime;

public class StorageEvent extends Event {
    private final String storageLocation;

    public StorageEvent(String eventId, LocalDateTime timestamp, String description, String storageLocation) {
        super(eventId, timestamp, description);
        this.storageLocation = storageLocation;
    }

    @Override
    public String getEventType() {
        return "STORAGE";
    }

    @Override
    public String getEventDetails() {
        return "storageLocation=" + storageLocation;
    }
}
