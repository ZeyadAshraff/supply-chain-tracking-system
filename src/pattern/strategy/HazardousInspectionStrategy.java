package pattern.strategy;

import entity.Shipment;

public class HazardousInspectionStrategy implements InspectionStrategy {
    @Override
    public String inspect(Shipment shipment) {
        return "INSPECTED_APPROVED_HAZMAT_PROTOCOL";
    }
}
