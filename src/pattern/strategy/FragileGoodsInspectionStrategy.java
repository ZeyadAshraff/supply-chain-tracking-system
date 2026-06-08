package pattern.strategy;

import entity.Shipment;

public class FragileGoodsInspectionStrategy implements InspectionStrategy {
    @Override
    public String inspect(Shipment shipment) {
        return "INSPECTED_APPROVED_FRAGILE_HANDLING";
    }
}
