package database;

import entity.DeliveryEvidence;
import entity.events.DeliveryConfirmationEvent;
import entity.events.DeliveryEvent;
import entity.events.DelayEvent;
import entity.events.DispatchEvent;
import entity.events.Event;
import entity.events.FailedDeliveryEvent;
import entity.events.InspectionEvent;
import entity.events.IssueReportedEvent;
import entity.events.ProductionEvent;
import entity.events.ReceiptEvent;
import entity.events.ReturnDispatchedEvent;
import entity.events.ReturnRequestedEvent;
import entity.events.ReturnedEvent;
import entity.events.ShipmentAcceptanceEvent;
import entity.events.ShipmentCreatedEvent;
import entity.events.StorageEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventRepository {
    private final DBConnection dbConnection;

    public EventRepository(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void addEvent(String shipmentId, Event event) {
        String sql = "INSERT INTO events(event_id, shipment_id, event_type, timestamp_value, description, details_json) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getEventId());
            ps.setString(2, shipmentId);
            ps.setString(3, event.getEventType());
            ps.setTimestamp(4, Timestamp.valueOf(event.getTimestamp()));
            ps.setString(5, event.getDescription());
            ps.setString(6, event.getEventDetails());
            ps.executeUpdate();

            if (event instanceof DeliveryEvent deliveryEvent) {
                DeliveryEvidence evidence = deliveryEvent.getDeliveryEvidence();
                String evSql = "INSERT INTO delivery_evidence(evidence_id, event_id, file_path, timestamp_value, verification_status) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement evPs = conn.prepareStatement(evSql)) {
                    evPs.setString(1, evidence.getEvidenceId());
                    evPs.setString(2, event.getEventId());
                    evPs.setString(3, evidence.getFilePath());
                    evPs.setTimestamp(4, Timestamp.valueOf(evidence.getTimestamp()));
                    evPs.setString(5, evidence.getVerificationStatus());
                    evPs.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to add event", ex);
        }
    }

    public List<Event> getEventsByShipment(String shipmentId) {
        String sql = "SELECT event_id, event_type, timestamp_value, description, details_json FROM events WHERE shipment_id = ? ORDER BY timestamp_value, event_id";
        List<Event> events = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, shipmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    events.add(mapEvent(conn,
                            rs.getString("event_id"),
                            rs.getString("event_type"),
                            rs.getTimestamp("timestamp_value").toLocalDateTime(),
                            rs.getString("description"),
                            rs.getString("details_json")));
                }
            }
            return events;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to read events", ex);
        }
    }

    private Event mapEvent(Connection conn, String eventId, String type, LocalDateTime time, String description, String details) throws SQLException {
        String safeDetails = details == null ? "" : details;
        return switch (type) {
            case "PRODUCTION" -> new ProductionEvent(eventId, time, description, extractValue(safeDetails, "location"));
            case "SHIPMENT_CREATED" -> new ShipmentCreatedEvent(eventId, time, description, extractValue(safeDetails, "createdBy"));
            case "SHIPMENT_ACCEPTED" -> new ShipmentAcceptanceEvent(eventId, time, description, extractValue(safeDetails, "acceptedBy"));
            case "DISPATCH" -> new DispatchEvent(eventId, time, description, extractValue(safeDetails, "vehicleId"));
            case "RECEIPT" -> new ReceiptEvent(eventId, time, description, extractValue(safeDetails, "receivedCondition"));
            case "INSPECTION" -> new InspectionEvent(eventId, time, description,
                    extractValue(safeDetails, "inspectionStatus"), extractValue(safeDetails, "notes"));
            case "STORAGE" -> new StorageEvent(eventId, time, description, extractValue(safeDetails, "storageLocation"));
            case "DELIVERY" -> new DeliveryEvent(eventId, time, description, loadEvidenceForEvent(conn, eventId));
            case "DELIVERY_CONFIRMATION" -> new DeliveryConfirmationEvent(eventId, time, description, extractValue(safeDetails, "confirmation"));
            case "ISSUE_REPORTED" -> new FailedDeliveryEvent(eventId, time, description,
                    extractValue(safeDetails, "issueDescription"), extractValue(safeDetails, "evidencePath"));
            case "FAILED_DELIVERY" -> new FailedDeliveryEvent(eventId, time, description,
                    extractValue(safeDetails, "reason"), extractValue(safeDetails, "evidencePath"));
            case "DELAY" -> new DelayEvent(eventId, time, description, extractValue(safeDetails, "delayReason"));
            case "RETURN_REQUESTED" -> new ReturnRequestedEvent(eventId, time, description,
                    extractValue(safeDetails, "returnReason"),
                    extractValue(safeDetails, "evidencePath"),
                    extractValue(safeDetails, "returnWarehouseId"));
            case "RETURN_DISPATCHED" -> new ReturnDispatchedEvent(eventId, time, description, extractValue(safeDetails, "vehicleId"));
            case "RETURNED" -> new ReturnedEvent(eventId, time, description, extractValue(safeDetails, "warehouseId"));
            default -> new ShipmentCreatedEvent(eventId, time, description, "SYSTEM");
        };
    }

    private DeliveryEvidence loadEvidenceForEvent(Connection conn, String eventId) throws SQLException {
        String sql = "SELECT evidence_id, file_path, timestamp_value, verification_status FROM delivery_evidence WHERE event_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DeliveryEvidence(
                            rs.getString("evidence_id"),
                            rs.getString("file_path"),
                            rs.getTimestamp("timestamp_value").toLocalDateTime(),
                            rs.getString("verification_status"));
                }
            }
        }
        return new DeliveryEvidence(UUID.randomUUID().toString(), "N/A", LocalDateTime.now(), "UNVERIFIED");
    }

    private String extractValue(String details, String key) {
        String[] segments = details.split(";");
        for (String segment : segments) {
            if (segment.startsWith(key + "=")) {
                return segment.substring((key + "=").length());
            }
        }
        return "";
    }
}
