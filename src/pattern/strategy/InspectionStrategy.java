package pattern.strategy;

import entity.Shipment;

public interface InspectionStrategy {
    String inspect(Shipment shipment);
}
