package control;

import database.GoodRepository;
import database.InventoryRepository;
import database.UserRepository;
import database.WarehouseRepository;
import entity.Good;
import entity.Manufacturer;
import entity.Role;
import entity.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GoodControl {
    private final GoodRepository goodRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;

    public GoodControl(GoodRepository goodRepository,
                       UserRepository userRepository,
                       InventoryRepository inventoryRepository,
                       WarehouseRepository warehouseRepository) {
        this.goodRepository = goodRepository;
        this.userRepository = userRepository;
        this.inventoryRepository = inventoryRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public Good registerGood(User actor, Good good) {
        good.getManufacturer().assertCanManageGoodsAs(actor);
        goodRepository.addGood(good);
        return good;
    }

    public void removeGood(User actor, String goodId) {
        String ownerManufacturerId = goodRepository.findManufacturerIdByGoodId(goodId);
        if (ownerManufacturerId == null) {
            throw new IllegalArgumentException("Good not found.");
        }
        Manufacturer owner = findManufacturerById(ownerManufacturerId);
        if (owner == null) {
            throw new IllegalArgumentException("Manufacturer not found.");
        }
        owner.assertCanManageGoodsAs(actor);

        String warehouseId = resolveManufacturerWarehouse(ownerManufacturerId);
        if (warehouseId != null && inventoryRepository.getGoodStockInWarehouse(warehouseId, goodId) > 0) {
            throw new IllegalArgumentException("Cannot remove a good while stock exists in manufacturer warehouse.");
        }
        goodRepository.removeGood(goodId);
    }

    public List<Good> listVisibleGoods(User actor) {
        return switch (actor.getRole()) {
            case MANUFACTURER -> goodRepository.findByManufacturer((Manufacturer) actor);
            case WAREHOUSE_MANAGER, SHIPPER, RETAILER -> goodRepository.findAllGoods(allManufacturers());
            default -> throw new IllegalArgumentException("You are not allowed to view goods.");
        };
    }

    public List<Good> listGoodsByManufacturer(User actor, String manufacturerId) {
        Manufacturer manufacturer = findManufacturerById(manufacturerId);
        if (manufacturer == null) {
            throw new IllegalArgumentException("Manufacturer not found.");
        }
        manufacturer.assertCanManageGoodsAs(actor);
        return goodRepository.findByManufacturer(manufacturer);
    }

    public List<Manufacturer> listSelectableManufacturers(User actor) {
        if (actor.getRole() == Role.MANUFACTURER) {
            List<Manufacturer> one = new ArrayList<>();
            one.add((Manufacturer) actor);
            return one;
        }
        throw new IllegalArgumentException("You are not allowed to manage goods.");
    }

    public String resolveManagedManufacturerId(User actor, String requestedManufacturerId) {
        if (actor.getRole() == Role.MANUFACTURER) {
            return actor.getUserId();
        }
        throw new IllegalArgumentException("You are not allowed to manage goods.");
    }

    public int getGoodStock(User actor, String manufacturerId, String goodId) {
        Manufacturer manufacturer = findManufacturerById(manufacturerId);
        if (manufacturer == null) {
            throw new IllegalArgumentException("Manufacturer not found.");
        }
        manufacturer.assertCanManageGoodsAs(actor);
        String warehouseId = resolveManufacturerWarehouse(manufacturerId);
        if (warehouseId == null) {
            return 0;
        }
        return inventoryRepository.getGoodStockInWarehouse(warehouseId, goodId);
    }

    public int adjustGoodQuantity(User actor, String manufacturerId, String goodId, int deltaQuantity) {
        if (deltaQuantity == 0) {
            throw new IllegalArgumentException("Quantity change cannot be zero.");
        }
        Manufacturer manufacturer = findManufacturerById(manufacturerId);
        if (manufacturer == null) {
            throw new IllegalArgumentException("Manufacturer not found.");
        }
        manufacturer.assertCanManageGoodsAs(actor);
        String ownerManufacturerId = goodRepository.findManufacturerIdByGoodId(goodId);
        if (ownerManufacturerId == null) {
            throw new IllegalArgumentException("Good not found.");
        }
        Good domainGood = findGoodByIdForManufacturer(manufacturer, goodId);
        domainGood.assertOwnedByManufacturer(manufacturerId);

        String warehouseId = resolveManufacturerWarehouse(manufacturerId);
        if (warehouseId == null) {
            throw new IllegalArgumentException("Manufacturer warehouse mapping is missing.");
        }

        String itemId = inventoryRepository.findItemIdForWarehouseGood(warehouseId, goodId);
        if (deltaQuantity > 0) {
            if (itemId == null) {
                itemId = "I-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            }
            inventoryRepository.upsertInventoryItem(warehouseId, itemId, goodId, deltaQuantity, LocalDate.now());
        } else {
            if (itemId == null) {
                throw new IllegalArgumentException("No stock exists for this good in manufacturer warehouse.");
            }
            inventoryRepository.decreaseInventoryItem(warehouseId, itemId, Math.abs(deltaQuantity));
        }
        return inventoryRepository.getGoodStockInWarehouse(warehouseId, goodId);
    }

    public String resolveManufacturerWarehouse(String manufacturerId) {
        String warehouseId = warehouseRepository.findWarehouseIdForManufacturer(manufacturerId);
        if (warehouseId != null && !warehouseId.isBlank()) {
            return warehouseId;
        }
        List<String> allWarehouseIds = warehouseRepository.findAllWarehouseIds();
        if (allWarehouseIds.isEmpty()) {
            return null;
        }
        warehouseId = allWarehouseIds.get(0);
        warehouseRepository.assignManufacturerWarehouse(manufacturerId, warehouseId);
        return warehouseId;
    }

    private Manufacturer findManufacturerById(String manufacturerId) {
        for (User user : userRepository.findAllUsers()) {
            if (user.getRole() == Role.MANUFACTURER && user.getUserId().equalsIgnoreCase(manufacturerId)) {
                return (Manufacturer) user;
            }
        }
        return null;
    }

    private List<Manufacturer> allManufacturers() {
        List<Manufacturer> manufacturers = new ArrayList<>();
        for (User user : userRepository.findAllUsers()) {
            if (user.getRole() == Role.MANUFACTURER) {
                manufacturers.add((Manufacturer) user);
            }
        }
        return manufacturers;
    }

    private Good findGoodByIdForManufacturer(Manufacturer manufacturer, String goodId) {
        for (Good good : goodRepository.findByManufacturer(manufacturer)) {
            if (good.getGoodId().equalsIgnoreCase(goodId)) {
                return good;
            }
        }
        throw new IllegalArgumentException("Good not found.");
    }
}
