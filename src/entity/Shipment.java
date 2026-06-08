package entity;

import entity.events.Event;
import pattern.observer.ShipmentObserver;
import pattern.readonly.ReadOnlyShipment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Shipment implements ReadOnlyShipment {
    private final String shipmentId;
    private final String sourceWarehouseId;
    private final String destinationType;
    private final String destinationWarehouseId;
    private final String destinationRetailerId;
    private final String pickupLocation;
    private final String destination;
    private final List<Item> items;
    private Shipper assignedShipper;
    private final List<Event> events;
    private final List<ShipmentObserver> observers;

    public Shipment(String shipmentId, String pickupLocation, String destination, List<Item> items) {
        this(shipmentId, null, "TEXT", null, null, pickupLocation, destination, items);
    }

    public Shipment(String shipmentId,
                    String sourceWarehouseId,
                    String destinationType,
                    String destinationWarehouseId,
                    String destinationRetailerId,
                    String pickupLocation,
                    String destination,
                    List<Item> items) {
        this.shipmentId = shipmentId;
        this.sourceWarehouseId = sourceWarehouseId;
        this.destinationType = destinationType == null ? "TEXT" : destinationType;
        this.destinationWarehouseId = destinationWarehouseId;
        this.destinationRetailerId = destinationRetailerId;
        this.pickupLocation = pickupLocation;
        this.destination = destination;
        this.items = new ArrayList<>(items);
        this.events = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    @Override
    public String getShipmentId() {
        return shipmentId;
    }

    @Override
    public String getPickupLocation() {
        return pickupLocation;
    }

    @Override
    public String getDestination() {
        return destination;
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public String getSourceWarehouseId() {
        return sourceWarehouseId;
    }

    public String getDestinationType() {
        return destinationType;
    }

    public String getDestinationWarehouseId() {
        return destinationWarehouseId;
    }

    public String getDestinationRetailerId() {
        return destinationRetailerId;
    }

    public Shipper getAssignedShipper() {
        return assignedShipper;
    }

    public void assignShipper(Shipper shipper) {
        this.assignedShipper = shipper;
    }

    public static void assertCanCreateShipment(Role role) {
        boolean allowed = role == Role.MANUFACTURER || role == Role.WAREHOUSE_MANAGER;
        if (!allowed) {
            throw new IllegalArgumentException("Only Manufacturer or Warehouse Manager can create shipments.");
        }
    }

    public static void assertValidRoute(String sourceWarehouseId,
                                        String destinationType,
                                        String destinationId,
                                        boolean sourceWarehouseExists,
                                        boolean destinationWarehouseExists,
                                        boolean destinationRetailerExists) {
        if (sourceWarehouseId == null || sourceWarehouseId.isBlank()) {
            throw new IllegalArgumentException("Source warehouse is required.");
        }
        if (!sourceWarehouseExists) {
            throw new IllegalArgumentException("Source warehouse does not exist.");
        }
        if (destinationType == null || destinationType.isBlank()) {
            throw new IllegalArgumentException("Destination type is required.");
        }
        if (destinationId == null || destinationId.isBlank()) {
            throw new IllegalArgumentException("Destination is required.");
        }
        if ("WAREHOUSE".equalsIgnoreCase(destinationType)) {
            if (!destinationWarehouseExists) {
                throw new IllegalArgumentException("Destination warehouse does not exist.");
            }
            if (sourceWarehouseId.equalsIgnoreCase(destinationId)) {
                throw new IllegalArgumentException("Source and destination warehouse cannot be the same.");
            }
            return;
        }
        if ("RETAILER".equalsIgnoreCase(destinationType)) {
            if (!destinationRetailerExists) {
                throw new IllegalArgumentException("Destination retailer does not exist.");
            }
            return;
        }
        throw new IllegalArgumentException("Destination type must be WAREHOUSE or RETAILER.");
    }

    public boolean isDestinationRetailer() {
        return "RETAILER".equalsIgnoreCase(destinationType);
    }

    public boolean isDestinationWarehouse() {
        return "WAREHOUSE".equalsIgnoreCase(destinationType);
    }

    public void addEvent(Event event) {
        events.add(event);
        notifyObservers(getStatus());
    }

    public List<Event> getEventList() {
        return Collections.unmodifiableList(events);
    }

    public void attachObserver(ShipmentObserver observer) {
        observers.add(observer);
    }

    public void detachObserver(ShipmentObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(String status) {
        for (ShipmentObserver observer : observers) {
            observer.updateShipmentStatus(shipmentId, status);
        }
    }

    @Override
    public String getStatus() {
        if (events.isEmpty()) {
            return "CREATED";
        }
        String type = events.get(events.size() - 1).getEventType();
        return switch (type) {
            case "SHIPMENT_CREATED" -> "CREATED";
            case "SHIPMENT_ACCEPTED" -> "ACCEPTED_BY_SHIPPER";
            case "DISPATCH" -> "IN_TRANSIT";
            case "RECEIPT" -> "RECEIVED_AT_WAREHOUSE";
            case "INSPECTION" -> "INSPECTED";
            case "STORAGE" -> "STORED";
            case "DELIVERY" -> "DELIVERED";
            case "DELIVERY_CONFIRMATION" -> "DELIVERY_CONFIRMED";
            case "FAILED_DELIVERY" -> "DELIVERY_FAILED";
            case "DELAY" -> "DELAYED";
            case "RETURN_REQUESTED" -> "RETURN_REQUESTED";
            case "RETURN_DISPATCHED" -> "RETURN_IN_TRANSIT";
            case "RETURNED" -> "RETURNED_TO_ORIGIN";
            default -> "UNKNOWN";
        };
    }
}
