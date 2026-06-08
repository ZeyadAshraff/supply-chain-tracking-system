package control;

import database.GoodRepository;
import database.InventoryRepository;
import database.UserRepository;
import database.WarehouseRepository;
import entity.Item;
import entity.Role;
import entity.User;
import entity.WarehouseManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InventoryControl {
    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final GoodRepository goodRepository;
    private final UserRepository userRepository;

    public InventoryControl(InventoryRepository inventoryRepository,
                            WarehouseRepository warehouseRepository,
                            GoodRepository goodRepository,
                            UserRepository userRepository) {
        this.inventoryRepository = inventoryRepository;
        this.warehouseRepository = warehouseRepository;
        this.goodRepository = goodRepository;
        this.userRepository = userRepository;
    }

    public void storeItem(User actor, String warehouseId, String itemId, String goodId, int quantity) {
        ensureCanManageWarehouse(actor, warehouseId);
        ensureWarehouseExists(warehouseId);
        Item.assertPositiveQuantity(quantity);
        if (!goodRepository.existsGood(goodId)) {
            throw new IllegalArgumentException("Good ID does not exist.");
        }
        inventoryRepository.upsertInventoryItem(warehouseId, itemId, goodId, quantity, LocalDate.now());
    }

    public void increaseStock(User actor, String warehouseId, String itemId, String goodId, int quantity) {
        storeItem(actor, warehouseId, itemId, goodId, quantity);
    }

    public void decreaseStock(User actor, String warehouseId, String itemId, int quantity) {
        ensureCanManageWarehouse(actor, warehouseId);
        ensureWarehouseExists(warehouseId);
        inventoryRepository.decreaseInventoryItem(warehouseId, itemId, quantity);
    }

    public List<Item> viewInventory(User actor, String warehouseId) {
        ensureCanViewWarehouse(actor, warehouseId);
        ensureWarehouseExists(warehouseId);
        return inventoryRepository.findInventoryByWarehouse(warehouseId, allManufacturers());
    }

    public List<String> listVisibleWarehouses(User actor) {
        if (actor.getRole() == Role.WAREHOUSE_MANAGER) {
            String warehouseId = ((WarehouseManager) actor).getWarehouseId();
            if (warehouseId == null || warehouseId.isBlank()) {
                warehouseId = warehouseRepository.findWarehouseIdForManager(actor.getUserId());
            }
            List<String> single = new ArrayList<>();
            if (warehouseId != null && !warehouseId.isBlank()) {
                single.add(warehouseId);
            }
            return single;
        }
        throw new IllegalArgumentException("You are not allowed to view warehouse inventory.");
    }

    private void ensureCanManageWarehouse(User actor, String warehouseId) {
        if (actor.getRole() != Role.WAREHOUSE_MANAGER) {
            throw new IllegalArgumentException("You are not allowed to manage this warehouse.");
        }
        resolveManager(actor).assertCanAccessWarehouse(warehouseId, "manage");
    }

    private void ensureCanViewWarehouse(User actor, String warehouseId) {
        if (actor.getRole() != Role.WAREHOUSE_MANAGER) {
            throw new IllegalArgumentException("You are not allowed to view this warehouse.");
        }
        resolveManager(actor).assertCanAccessWarehouse(warehouseId, "view");
    }

    private WarehouseManager resolveManager(User actor) {
        WarehouseManager manager = (WarehouseManager) actor;
        String managerWarehouse = manager.getWarehouseId();
        if (managerWarehouse == null || managerWarehouse.isBlank()) {
            managerWarehouse = warehouseRepository.findWarehouseIdForManager(actor.getUserId());
            manager.setWarehouseId(managerWarehouse);
        }
        return manager;
    }

    private void ensureWarehouseExists(String warehouseId) {
        if (!warehouseRepository.findAllWarehouseIds().contains(warehouseId)) {
            throw new IllegalArgumentException("Warehouse not found: " + warehouseId);
        }
    }

    private List<entity.Manufacturer> allManufacturers() {
        List<entity.Manufacturer> manufacturers = new ArrayList<>();
        for (User user : userRepository.findAllUsers()) {
            if (user.getRole() == Role.MANUFACTURER) {
                manufacturers.add((entity.Manufacturer) user);
            }
        }
        return manufacturers;
    }
}
