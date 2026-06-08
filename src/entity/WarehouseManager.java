package entity;

public class WarehouseManager extends User {
    private String warehouseId;

    public WarehouseManager(String userId, String name, String email, String password, boolean active, String warehouseId) {
        super(userId, name, email, password, active);
        this.warehouseId = warehouseId;
    }

    @Override
    public Role getRole() {
        return Role.WAREHOUSE_MANAGER;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public boolean managesWarehouse(String candidateWarehouseId) {
        return warehouseId != null
                && candidateWarehouseId != null
                && warehouseId.equalsIgnoreCase(candidateWarehouseId);
    }

    public void assertCanAccessWarehouse(String candidateWarehouseId, String action) {
        if (!managesWarehouse(candidateWarehouseId)) {
            throw new IllegalArgumentException("You are not allowed to " + action + " this warehouse.");
        }
    }
}
