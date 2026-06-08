package entity.events;

import entity.Role;
import java.util.List;
import java.time.LocalDateTime;

public abstract class Event {
    private final String eventId;
    private final LocalDateTime timestamp;
    private final String description;

    protected Event(String eventId, LocalDateTime timestamp, String description) {
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.description = description;
    }

    public String getEventId() {
        return eventId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public abstract String getEventType();

    public abstract String getEventDetails();

    public static boolean isEvidenceRequired(String eventType) {
        return "DELIVERY".equalsIgnoreCase(eventType)
                || "FAILED_DELIVERY".equalsIgnoreCase(eventType)
                || "RETURN_REQUESTED".equalsIgnoreCase(eventType);
    }

    public static List<String> candidateEventsForRole(Role role) {
        return switch (role) {
            case SHIPPER -> List.of("SHIPMENT_ACCEPTED", "DISPATCH", "DELIVERY", "DELAY", "FAILED_DELIVERY", "RETURN_DISPATCHED");
            case WAREHOUSE_MANAGER -> List.of("RECEIPT", "INSPECTION", "STORAGE", "RETURNED");
            case RETAILER -> List.of("DELIVERY_CONFIRMATION", "RETURN_REQUESTED");
            default -> List.of();
        };
    }

    public static void assertCanRecordEvent(Role role, String eventType, String currentStatus) {
        boolean allowedByRole = switch (eventType) {
            case "SHIPMENT_ACCEPTED", "DISPATCH", "DELIVERY", "DELAY", "RETURN_DISPATCHED", "FAILED_DELIVERY" -> role == Role.SHIPPER;
            case "RECEIPT", "INSPECTION", "STORAGE", "RETURNED" -> role == Role.WAREHOUSE_MANAGER;
            case "DELIVERY_CONFIRMATION", "RETURN_REQUESTED" -> role == Role.RETAILER;
            default -> false;
        };
        if (!allowedByRole) {
            throw new IllegalArgumentException("Role " + role + " is not allowed to record event " + eventType + ".");
        }
        assertTransitionAllowed(eventType, currentStatus);
    }

    public static void assertTransitionAllowed(String eventType, String status) {
        boolean allowed = switch (eventType) {
            case "SHIPMENT_ACCEPTED" -> "CREATED".equals(status);
            case "DISPATCH" -> "CREATED".equals(status)
                    || "ACCEPTED_BY_SHIPPER".equals(status)
                    || "STORED".equals(status)
                    || "INSPECTED".equals(status)
                    || "RETURN_REQUESTED".equals(status)
                    || "DELAYED".equals(status);
            case "RECEIPT" -> "IN_TRANSIT".equals(status);
            case "INSPECTION" -> "RECEIVED_AT_WAREHOUSE".equals(status);
            case "STORAGE" -> "RECEIVED_AT_WAREHOUSE".equals(status) || "INSPECTED".equals(status);
            case "DELIVERY" -> "IN_TRANSIT".equals(status) || "STORED".equals(status) || "DELAYED".equals(status);
            case "DELIVERY_CONFIRMATION" -> "DELIVERED".equals(status);
            case "FAILED_DELIVERY" -> "DELIVERED".equals(status);
            case "RETURN_REQUESTED" -> "DELIVERY_FAILED".equals(status) || "DELIVERED".equals(status);
            case "RETURN_DISPATCHED" -> "RETURN_REQUESTED".equals(status);
            case "RETURNED" -> "RETURN_IN_TRANSIT".equals(status);
            case "DELAY" -> !"DELIVERY_CONFIRMED".equals(status) && !"RETURNED_TO_ORIGIN".equals(status);
            default -> false;
        };
        if (!allowed) {
            throw new IllegalArgumentException("Event " + eventType + " is not valid while shipment status is " + status + ".");
        }
    }
}
