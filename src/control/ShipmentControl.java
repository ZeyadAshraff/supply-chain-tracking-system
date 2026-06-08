package control;

import database.EventRepository;
import database.ShipmentRepository;
import database.UserRepository;
import database.WarehouseRepository;
import entity.Item;
import entity.Retailer;
import entity.Role;
import entity.Shipment;
import entity.Shipper;
import entity.User;
import entity.Warehouse;
import entity.events.DeliveryConfirmationEvent;
import entity.events.Event;
import entity.events.FailedDeliveryEvent;
import entity.events.ReturnRequestedEvent;
import entity.events.ShipmentCreatedEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShipmentControl {
    private final ShipmentRepository shipmentRepository;
    private final EventRepository eventRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;

    public ShipmentControl(ShipmentRepository shipmentRepository,
                           EventRepository eventRepository,
                           WarehouseRepository warehouseRepository,
                           UserRepository userRepository) {
        this.shipmentRepository = shipmentRepository;
        this.eventRepository = eventRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
    }

    public Shipment createShipment(User actor,
                                   String sourceWarehouseId,
                                   String destinationType,
                                   String destinationId,
                                   List<Item> items,
                                   Shipper assignedShipper) {
        Shipment.assertCanCreateShipment(actor.getRole());
        List<String> warehouseIds = warehouseRepository.findAllWarehouseIds();
        Shipment.assertValidRoute(
                sourceWarehouseId,
                destinationType,
                destinationId,
                warehouseIds.contains(sourceWarehouseId),
                warehouseIds.contains(destinationId),
                userRepository.findRetailerById(destinationId) != null);

        String pickupLocation = warehouseRepository.findWarehouseLocationById(sourceWarehouseId);
        if (pickupLocation == null || pickupLocation.isBlank()) {
            throw new IllegalArgumentException("Source warehouse location is not defined.");
        }

        String destinationLabel;
        String destinationWarehouseId = null;
        String destinationRetailerId = null;
        if ("WAREHOUSE".equalsIgnoreCase(destinationType)) {
            destinationWarehouseId = destinationId;
            String destinationLocation = warehouseRepository.findWarehouseLocationById(destinationId);
            if (destinationLocation == null || destinationLocation.isBlank()) {
                throw new IllegalArgumentException("Destination warehouse location is not defined.");
            }
            destinationLabel = destinationLocation;
        } else if ("RETAILER".equalsIgnoreCase(destinationType)) {
            destinationRetailerId = destinationId;
            Retailer retailer = userRepository.findRetailerById(destinationId);
            if (retailer == null) {
                throw new IllegalArgumentException("Destination retailer not found.");
            }
            destinationLabel = retailer.getLocation();
        } else {
            throw new IllegalArgumentException("Unsupported destination type.");
        }

        Shipment shipment = new Shipment(
                UUID.randomUUID().toString(),
                sourceWarehouseId,
                destinationType.toUpperCase(),
                destinationWarehouseId,
                destinationRetailerId,
                pickupLocation,
                destinationLabel,
                items);
        shipment.assignShipper(assignedShipper);
        shipmentRepository.createShipment(shipment);
        eventRepository.addEvent(shipment.getShipmentId(), new ShipmentCreatedEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                "Shipment created",
                actor.getUserId()));
        return shipment;
    }

    public Shipment createShipment(User actor, String pickupLocation, String destination, List<Item> items, Shipper assignedShipper) {
        throw new IllegalArgumentException("Use warehouse and retailer selection for shipment creation.");
    }

    public void assignShipper(String shipmentId, Shipper shipper) {
        shipmentRepository.assignShipper(shipmentId, shipper);
    }

    public Shipment findShipmentById(String shipmentId) {
        return shipmentRepository.findById(shipmentId);
    }

    public Shipment findShipmentById(User actor, String shipmentId) {
        ensureCanViewShipment(actor, shipmentId);
        return shipmentRepository.findById(shipmentId);
    }

    public void addEvent(User actor, String shipmentId, Event event) {
        ensureCanViewShipment(actor, shipmentId);
        Shipment shipment = shipmentRepository.findById(shipmentId);
        if (shipment == null) {
            throw new IllegalArgumentException("Shipment not found.");
        }
        // Rebuild lifecycle state from persisted timeline before validating next event.
        for (Event persistedEvent : eventRepository.getEventsByShipment(shipmentId)) {
            shipment.addEvent(persistedEvent);
        }
        Event.assertCanRecordEvent(actor.getRole(), event.getEventType(), shipment.getStatus());
        eventRepository.addEvent(shipmentId, event);
    }

    public String viewShipmentStatus(User actor, String shipmentId) {
        ensureCanViewShipment(actor, shipmentId);
        List<Event> events = eventRepository.getEventsByShipment(shipmentId);
        if (events.isEmpty()) {
            return "CREATED";
        }
        Shipment temp = shipmentRepository.findById(shipmentId);
        if (temp == null) {
            return "NOT_FOUND";
        }
        for (Event event : events) {
            temp.addEvent(event);
        }
        return temp.getStatus();
    }

    public void recordDeliveryConfirmation(User actor, String shipmentId, String details) {
        addEvent(actor, shipmentId, new DeliveryConfirmationEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                "Retailer confirmed delivery",
                details));
    }

    public void requestShipmentReturn(User actor, String shipmentId, String reason, String evidencePath) {
        Shipment shipment = findShipmentById(actor, shipmentId);
        String returnWarehouseId = shipment.getSourceWarehouseId();
        addEvent(actor, shipmentId, new ReturnRequestedEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                "Return requested by retailer",
                reason,
                evidencePath,
                returnWarehouseId == null ? "" : returnWarehouseId));
    }

    public void reportFailedDelivery(User actor, String shipmentId, String reason, String evidencePath) {
        addEvent(actor, shipmentId, new FailedDeliveryEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                "Delivery failed at retailer",
                reason,
                evidencePath));
    }

    public List<Event> getShipmentTimeline(User actor, String shipmentId) {
        ensureCanViewShipment(actor, shipmentId);
        return eventRepository.getEventsByShipment(shipmentId);
    }

    public List<Shipment> getVisibleShipments(User actor) {
        return shipmentRepository.findVisibleShipments(actor);
    }

    public List<String> getAllowedEventsForShipment(User actor, String shipmentId) {
        ensureCanViewShipment(actor, shipmentId);
        Shipment shipment = shipmentRepository.findById(shipmentId);
        if (shipment == null) {
            throw new IllegalArgumentException("Shipment not found.");
        }

        for (Event e : eventRepository.getEventsByShipment(shipmentId)) {
            shipment.addEvent(e);
        }

        List<String> allowed = new ArrayList<>();
        for (String candidate : Event.candidateEventsForRole(actor.getRole())) {
            try {
                Event.assertCanRecordEvent(actor.getRole(), candidate, shipment.getStatus());
                allowed.add(candidate);
            } catch (IllegalArgumentException ignored) {
                // Not valid in current lifecycle state.
            }
        }
        return allowed;
    }

    public List<Warehouse> listShipmentWarehouses() {
        return warehouseRepository.findAllWarehouses();
    }

    public List<Retailer> listShipmentRetailers() {
        return userRepository.findAllRetailers();
    }

    public String suggestDefaultSourceWarehouse(User actor) {
        if (actor.getRole() == Role.WAREHOUSE_MANAGER) {
            String warehouseId = warehouseRepository.findWarehouseIdForManager(actor.getUserId());
            if (warehouseId != null && !warehouseId.isBlank()) {
                return warehouseId;
            }
        }
        if (actor.getRole() == Role.MANUFACTURER) {
            String warehouseId = warehouseRepository.findWarehouseIdForManufacturer(actor.getUserId());
            if (warehouseId != null && !warehouseId.isBlank()) {
                return warehouseId;
            }
        }
        return null;
    }

    private void ensureCanViewShipment(User actor, String shipmentId) {
        if (!shipmentRepository.canUserViewShipment(actor, shipmentId)) {
            throw new IllegalArgumentException("You are not allowed to view this shipment.");
        }
    }

}
