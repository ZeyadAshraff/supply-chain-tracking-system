package pattern.readonly;

public interface ReadOnlyShipment {
    String getShipmentId();
    String getStatus();
    String getPickupLocation();
    String getDestination();
}
