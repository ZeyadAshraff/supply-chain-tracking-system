package pattern.observer;

public class RetailerObserver implements ShipmentObserver {
    @Override
    public void updateShipmentStatus(String shipmentId, String status) {
        System.out.println("[RetailerObserver] Shipment " + shipmentId + " status changed to " + status);
    }
}
