package control;

import entity.DeliveryEvidence;
import entity.Role;
import entity.Shipment;
import entity.User;
import entity.Vehicle;
import entity.events.DeliveryEvent;
import entity.events.DelayEvent;
import entity.events.DispatchEvent;
import entity.events.Event;
import entity.events.InspectionEvent;
import entity.events.ReceiptEvent;
import entity.events.ReturnDispatchedEvent;
import entity.events.ReturnedEvent;
import entity.events.ShipmentAcceptanceEvent;
import entity.events.StorageEvent;
import java.time.LocalDateTime;
import java.util.UUID;
import pattern.strategy.InspectionStrategy;
import pattern.strategy.StandardInspectionStrategy;

public class EventControl {
    private InspectionStrategy strategy;
    private final ShipmentControl shipmentControl;

    public EventControl(ShipmentControl shipmentControl) {
        this.shipmentControl = shipmentControl;
        this.strategy = new StandardInspectionStrategy();
    }

    public void recordDispatchEvent(User actor, String shipmentId, Vehicle vehicle) {
        DispatchEvent event = new DispatchEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                "Shipment dispatched",
                vehicle.getVehicleId());
        shipmentControl.addEvent(actor, shipmentId, event);
    }

    public void recordShipmentAcceptanceEvent(User actor, String shipmentId) {
        ShipmentAcceptanceEvent event = new ShipmentAcceptanceEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                "Shipment accepted by shipper",
                actor.getUserId());
        shipmentControl.addEvent(actor, shipmentId, event);
    }

    public void recordReceiptEvent(User actor, String shipmentId, String condition) {
        ReceiptEvent event = new ReceiptEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                "Shipment received",
                condition);
        shipmentControl.addEvent(actor, shipmentId, event);
    }

    public void recordInspectionEvent(User actor, String shipmentId, String notes) {
        Shipment shipment = shipmentControl.findShipmentById(actor, shipmentId);
        if (shipment == null) {
            throw new IllegalArgumentException("Shipment not found.");
        }
        String inspectionStatus = strategy.inspect(shipment);
        InspectionEvent event = new InspectionEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                "Inspection recorded",
                inspectionStatus,
                notes);
        shipmentControl.addEvent(actor, shipmentId, event);
    }

    public void recordStorageEvent(User actor, String shipmentId, String location) {
        StorageEvent event = new StorageEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                "Storage recorded",
                location);
        shipmentControl.addEvent(actor, shipmentId, event);
    }

    public void recordDeliveryEvent(User actor, String shipmentId, DeliveryEvidence evidence) {
        DeliveryEvent event = new DeliveryEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                "Delivery recorded",
                evidence);
        shipmentControl.addEvent(actor, shipmentId, event);
    }

    public void recordDelayEvent(User actor, String shipmentId, String reason) {
        DelayEvent event = new DelayEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                "Delay recorded",
                reason);
        shipmentControl.addEvent(actor, shipmentId, event);
    }

    public void recordReturnDispatchEvent(User actor, String shipmentId, String vehicleId) {
        ReturnDispatchedEvent event = new ReturnDispatchedEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                "Return shipment dispatched",
                vehicleId);
        shipmentControl.addEvent(actor, shipmentId, event);
    }

    public void recordReturnedEvent(User actor, String shipmentId, String warehouseId) {
        ReturnedEvent event = new ReturnedEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                "Shipment returned to origin",
                warehouseId);
        shipmentControl.addEvent(actor, shipmentId, event);
    }

    public String[] allowedEventTypesForRole(Role role) {
        return Event.candidateEventsForRole(role).toArray(new String[0]);
    }

    public boolean isInspectionEvent(String eventType) {
        return "INSPECTION".equalsIgnoreCase(eventType);
    }

    public boolean requiresEvidencePath(String eventType) {
        return Event.isEvidenceRequired(eventType);
    }

    public void setStrategy(InspectionStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Inspection strategy is required.");
        }
        this.strategy = strategy;
    }
}
