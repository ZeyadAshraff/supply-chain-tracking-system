package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Warehouse {
    private final String warehouseId;
    private String name;
    private String location;
    private int capacity;
    private final List<Item> items;

    public Warehouse(String warehouseId, String name, String location, int capacity) {
        this.warehouseId = warehouseId;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.items = new ArrayList<>();
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void addInventoryItem(Item item) {
        items.add(item);
    }

    public void removeInventoryItem(Item item) {
        items.remove(item);
    }

    public List<Item> getInventory() {
        return Collections.unmodifiableList(items);
    }

    public int getAvailableCapacity() {
        int used = 0;
        for (Item item : items) {
            used += item.getQuantity();
        }
        return Math.max(0, capacity - used);
    }
}
