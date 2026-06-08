package pattern.observer;

public interface ShipmentObserver {
    void updateShipmentStatus(String shipmentId, String status);
}
