package database;

import entity.Good;
import entity.Manufacturer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GoodRepository {
    private final DBConnection dbConnection;

    public GoodRepository(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void addGood(Good good) {
        String sql = "INSERT INTO goods(good_id, label, description, manufacturer_id, weight, volume) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, good.getGoodId());
            ps.setString(2, good.getLabel());
            ps.setString(3, good.getDescription());
            ps.setString(4, good.getManufacturer().getUserId());
            ps.setDouble(5, good.getWeight());
            ps.setDouble(6, good.getVolume());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to add good", ex);
        }
    }

    public Good getGood(String goodId, Manufacturer manufacturer) {
        String sql = "SELECT good_id, label, description, weight, volume FROM goods WHERE good_id = ?";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, goodId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Good(rs.getString("good_id"), rs.getString("label"), rs.getString("description"),
                        manufacturer, rs.getDouble("weight"), rs.getDouble("volume"));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to get good", ex);
        }
    }

    public void removeGood(String goodId) {
        String sql = "DELETE FROM goods WHERE good_id = ?";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, goodId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to remove good", ex);
        }
    }

    public List<Good> findByManufacturer(Manufacturer manufacturer) {
        String sql = "SELECT good_id, label, description, weight, volume FROM goods WHERE manufacturer_id = ? ORDER BY label";
        List<Good> goods = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manufacturer.getUserId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    goods.add(new Good(
                            rs.getString("good_id"),
                            rs.getString("label"),
                            rs.getString("description"),
                            manufacturer,
                            rs.getDouble("weight"),
                            rs.getDouble("volume")));
                }
            }
            return goods;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to list goods by manufacturer", ex);
        }
    }

    public List<Good> findAllGoods(List<Manufacturer> manufacturers) {
        String sql = "SELECT good_id, label, description, manufacturer_id, weight, volume FROM goods ORDER BY label";
        List<Good> goods = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String manufacturerId = rs.getString("manufacturer_id");
                Manufacturer manufacturer = findManufacturerById(manufacturers, manufacturerId);
                if (manufacturer == null) {
                    continue;
                }
                goods.add(new Good(
                        rs.getString("good_id"),
                        rs.getString("label"),
                        rs.getString("description"),
                        manufacturer,
                        rs.getDouble("weight"),
                        rs.getDouble("volume")));
            }
            return goods;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to list goods", ex);
        }
    }

    public String findManufacturerIdByGoodId(String goodId) {
        String sql = "SELECT manufacturer_id FROM goods WHERE good_id = ?";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, goodId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return rs.getString("manufacturer_id");
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to fetch good owner", ex);
        }
    }

    public boolean existsGood(String goodId) {
        String sql = "SELECT 1 FROM goods WHERE good_id = ?";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, goodId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to check good", ex);
        }
    }

    public Good findById(String goodId, List<Manufacturer> manufacturers) {
        String sql = "SELECT good_id, label, description, manufacturer_id, weight, volume FROM goods WHERE good_id = ?";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, goodId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Manufacturer manufacturer = findManufacturerById(manufacturers, rs.getString("manufacturer_id"));
                if (manufacturer == null) {
                    return null;
                }
                return new Good(
                        rs.getString("good_id"),
                        rs.getString("label"),
                        rs.getString("description"),
                        manufacturer,
                        rs.getDouble("weight"),
                        rs.getDouble("volume"));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load good by id", ex);
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
