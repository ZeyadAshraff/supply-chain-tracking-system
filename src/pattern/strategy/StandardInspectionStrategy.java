package pattern.strategy;

import entity.Shipment;

public class StandardInspectionStrategy implements InspectionStrategy {
    @Override
    public String inspect(Shipment shipment) {
        return "INSPECTED_APPROVED";
    }
}
