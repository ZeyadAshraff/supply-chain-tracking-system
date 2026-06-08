package pattern.observer;

public class AdminObserver implements ShipmentObserver {
    @Override
    public void updateShipmentStatus(String shipmentId, String status) {
        System.out.println("[AdminObserver] Shipment " + shipmentId + " status changed to " + status);
    }
}
