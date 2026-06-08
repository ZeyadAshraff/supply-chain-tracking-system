package database;

import entity.Good;
import entity.Item;
import entity.Manufacturer;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InventoryRepository {
    private final DBConnection dbConnection;

    public InventoryRepository(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void upsertInventoryItem(String warehouseId, String itemId, String goodId, int quantity, LocalDate manufactureDate) {
        Item.assertPositiveQuantity(quantity);
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            ensureItemExists(conn, itemId, goodId, quantity, manufactureDate);
            String sql = """
                    INSERT INTO warehouse_inventory(warehouse_id, item_id, quantity)
                    VALUES (?, ?, ?)
                    ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, warehouseId);
                ps.setString(2, itemId);
                ps.setInt(3, quantity);
                ps.executeUpdate();
            }
            updateItemQuantity(conn, itemId, quantity);
            conn.commit();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to store inventory item", ex);
        }
    }

    public void decreaseInventoryItem(String warehouseId, String itemId, int quantity) {
        Item.assertPositiveQuantity(quantity);
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);

            int current = getCurrentInventoryQuantity(conn, warehouseId, itemId);
            if (current < quantity) {
                throw new IllegalArgumentException("Requested quantity exceeds inventory stock.");
            }

            String updateSql = "UPDATE warehouse_inventory SET quantity = quantity - ? WHERE warehouse_id = ? AND item_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, quantity);
                ps.setString(2, warehouseId);
                ps.setString(3, itemId);
                ps.executeUpdate();
            }

            String cleanupSql = "DELETE FROM warehouse_inventory WHERE warehouse_id = ? AND item_id = ? AND quantity <= 0";
            try (PreparedStatement ps = conn.prepareStatement(cleanupSql)) {
                ps.setString(1, warehouseId);
                ps.setString(2, itemId);
                ps.executeUpdate();
            }

            updateItemQuantity(conn, itemId, -quantity);
            conn.commit();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to decrease inventory item", ex);
        }
    }

    public int getGoodStockInWarehouse(String warehouseId, String goodId) {
        String sql = """
                SELECT COALESCE(SUM(wi.quantity), 0) AS total_qty
                FROM warehouse_inventory wi
                JOIN items i ON i.item_id = wi.item_id
                WHERE wi.warehouse_id = ? AND i.good_id = ?
                """;
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, warehouseId);
            ps.setString(2, goodId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("total_qty") : 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to get stock by good", ex);
        }
    }

    public String findItemIdForWarehouseGood(String warehouseId, String goodId) {
        String sql = """
                SELECT wi.item_id
                FROM warehouse_inventory wi
                JOIN items i ON i.item_id = wi.item_id
                WHERE wi.warehouse_id = ? AND i.good_id = ?
                ORDER BY wi.item_id
                LIMIT 1
                """;
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, warehouseId);
            ps.setString(2, goodId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("item_id") : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load inventory item by good", ex);
        }
    }

    public List<Item> findInventoryByWarehouseAndManufacturer(String warehouseId, String manufacturerId, List<Manufacturer> manufacturers) {
        String sql = """
                SELECT i.item_id, i.quantity, i.manufacture_date,
                       g.good_id, g.label, g.description, g.weight, g.volume, g.manufacturer_id
                FROM warehouse_inventory wi
                JOIN items i ON wi.item_id = i.item_id
                JOIN goods g ON i.good_id = g.good_id
                WHERE wi.warehouse_id = ? AND g.manufacturer_id = ?
                ORDER BY i.item_id
                """;
        List<Item> items = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, warehouseId);
            ps.setString(2, manufacturerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Manufacturer manufacturer = findManufacturerById(manufacturers, rs.getString("manufacturer_id"));
                    if (manufacturer == null) {
                        continue;
                    }
                    Good good = new Good(
                            rs.getString("good_id"),
                            rs.getString("label"),
                            rs.getString("description"),
                            manufacturer,
                            rs.getDouble("weight"),
                            rs.getDouble("volume"));
                    items.add(new Item(
                            rs.getString("item_id"),
                            rs.getInt("quantity"),
                            rs.getDate("manufacture_date").toLocalDate(),
                            good));
                }
            }
            return items;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to list manufacturer warehouse inventory", ex);
        }
    }

    public List<Item> findInventoryByWarehouse(String warehouseId, List<Manufacturer> manufacturers) {
        String sql = """
                SELECT i.item_id, i.quantity, i.manufacture_date,
                       g.good_id, g.label, g.description, g.weight, g.volume, g.manufacturer_id
                FROM warehouse_inventory wi
                JOIN items i ON wi.item_id = i.item_id
                JOIN goods g ON i.good_id = g.good_id
                WHERE wi.warehouse_id = ?
                ORDER BY i.item_id
                """;
        List<Item> items = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, warehouseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Manufacturer manufacturer = findManufacturerById(manufacturers, rs.getString("manufacturer_id"));
                    if (manufacturer == null) {
                        continue;
                    }
                    Good good = new Good(
                            rs.getString("good_id"),
                            rs.getString("label"),
                            rs.getString("description"),
                            manufacturer,
                            rs.getDouble("weight"),
                            rs.getDouble("volume"));
                    items.add(new Item(
                            rs.getString("item_id"),
                            rs.getInt("quantity"),
                            rs.getDate("manufacture_date").toLocalDate(),
                            good));
                }
            }
            return items;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to list warehouse inventory", ex);
        }
    }

    private void ensureItemExists(Connection conn, String itemId, String goodId, int quantity, LocalDate manufactureDate) throws SQLException {
        String checkSql = "SELECT 1 FROM items WHERE item_id = ?";
        try (PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setString(1, itemId);
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) {
                    return;
                }
            }
        }

        String insertSql = "INSERT INTO items(item_id, good_id, quantity, manufacture_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
            insert.setString(1, itemId);
            insert.setString(2, goodId);
            insert.setInt(3, 0);
            insert.setDate(4, Date.valueOf(manufactureDate));
            insert.executeUpdate();
        }
    }

    private int getCurrentInventoryQuantity(Connection conn, String warehouseId, String itemId) throws SQLException {
        String sql = "SELECT quantity FROM warehouse_inventory WHERE warehouse_id = ? AND item_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, warehouseId);
            ps.setString(2, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("Item not found in selected warehouse inventory.");
                }
                return rs.getInt("quantity");
            }
        }
    }

    private void updateItemQuantity(Connection conn, String itemId, int delta) throws SQLException {
        String sql = "UPDATE items SET quantity = GREATEST(quantity + ?, 0) WHERE item_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setString(2, itemId);
            ps.executeUpdate();
        }
    }

    private Manufacturer findManufacturerById(List<Manufacturer> manufacturers, String manufacturerId) {
        for (Manufacturer manufacturer : manufacturers) {
            if (manufacturer.getUserId().equalsIgnoreCase(manufacturerId)) {
                return manufacturer;
            }
        }
        return null;
    }
}
