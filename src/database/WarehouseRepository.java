package database;

import entity.Warehouse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WarehouseRepository {
    private final DBConnection dbConnection;

    public WarehouseRepository(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void ensureWarehouse(String warehouseId, String name, String location, int capacity) {
        String sql = "INSERT INTO warehouses(warehouse_id, name, location, capacity) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name), location=VALUES(location), capacity=VALUES(capacity)";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, warehouseId);
            ps.setString(2, name);
            ps.setString(3, location);
            ps.setInt(4, capacity);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to ensure warehouse: " + ex.getMessage(), ex);
        }
    }

    public String findWarehouseIdForManager(String managerUserId) {
        String sql = "SELECT warehouse_id FROM warehouse_managers WHERE user_id = ?";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, managerUserId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("warehouse_id") : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to fetch warehouse manager mapping", ex);
        }
    }

    public List<String> findAllWarehouseIds() {
        String sql = "SELECT warehouse_id FROM warehouses ORDER BY warehouse_id";
        List<String> ids = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getString("warehouse_id"));
            }
            return ids;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to list warehouses", ex);
        }
    }

    public List<Warehouse> findAllWarehouses() {
        String sql = "SELECT warehouse_id, name, location, capacity FROM warehouses ORDER BY warehouse_id";
        List<Warehouse> warehouses = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                warehouses.add(new Warehouse(
                        rs.getString("warehouse_id"),
                        rs.getString("name"),
                        rs.getString("location"),
                        rs.getInt("capacity")));
            }
            return warehouses;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to list warehouses", ex);
        }
    }

    public String findWarehouseLocationById(String warehouseId) {
        String sql = "SELECT location FROM warehouses WHERE warehouse_id = ?";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, warehouseId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("location") : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load warehouse location", ex);
        }
    }

    public String findWarehouseNameById(String warehouseId) {
        String sql = "SELECT name FROM warehouses WHERE warehouse_id = ?";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, warehouseId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("name") : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load warehouse name", ex);
        }
    }

    public void assignManufacturerWarehouse(String manufacturerId, String warehouseId) {
        String sql = """
                INSERT INTO manufacturer_warehouses(manufacturer_id, warehouse_id)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE warehouse_id = VALUES(warehouse_id)
                """;
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manufacturerId);
            ps.setString(2, warehouseId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to assign manufacturer warehouse", ex);
        }
    }

    public String findWarehouseIdForManufacturer(String manufacturerId) {
        String sql = "SELECT warehouse_id FROM manufacturer_warehouses WHERE manufacturer_id = ?";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manufacturerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("warehouse_id") : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to fetch manufacturer warehouse mapping", ex);
        }
    }
}
