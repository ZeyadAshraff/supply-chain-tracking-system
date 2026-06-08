package pattern.observer;

public class WarehouseObserver implements ShipmentObserver {
    @Override
    public void updateShipmentStatus(String shipmentId, String status) {
        System.out.println("[WarehouseObserver] Shipment " + shipmentId + " status changed to " + status);
    }
}
