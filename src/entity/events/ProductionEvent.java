package entity.events;

import java.time.LocalDateTime;

public class ProductionEvent extends Event {
    private final String productionLocation;

    public ProductionEvent(String eventId, LocalDateTime timestamp, String description, String productionLocation) {
        super(eventId, timestamp, description);
        this.productionLocation = productionLocation;
    }

    public String getProductionLocation() {
        return productionLocation;
    }

    @Override
    public String getEventType() {
        return "PRODUCTION";
    }

    @Override
    public String getEventDetails() {
        return "location=" + productionLocation;
    }
}
