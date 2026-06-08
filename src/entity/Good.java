package entity;

import java.util.List;

public class Good {
    private final String goodId;
    private String label;
    private String description;
    private final Manufacturer manufacturer;
    private double weight;
    private double volume;

    public Good(String goodId, String label, String description, Manufacturer manufacturer, double weight, double volume) {
        this.goodId = goodId;
        this.label = label;
        this.description = description;
        this.manufacturer = manufacturer;
        this.weight = weight;
        this.volume = volume;
    }

    public String getGoodId() {
        return goodId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public double getWeight() {
        return weight;
    }

    public double getVolume() {
        return volume;
    }

    public void assertOwnedByManufacturer(String manufacturerId) {
        if (manufacturerId != null && manufacturer.getUserId().equalsIgnoreCase(manufacturerId)) {
            return;
        }
        throw new IllegalArgumentException("Good does not belong to selected manufacturer.");
    }

    // Good is the abstract definition; stock lives in item occurrences.
    public int totalStockFromOccurrences(List<Item> occurrences) {
        if (occurrences == null || occurrences.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (Item item : occurrences) {
            if (item != null && item.belongsToGood(goodId)) {
                total += item.getOnHandQuantity();
            }
        }
        return total;
    }
}
