package database;

import entity.Admin;
import entity.Manufacturer;
import entity.Retailer;
import entity.Role;
import entity.Shipper;
import entity.User;
import entity.WarehouseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final DBConnection dbConnection;

    public UserRepository(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void addUser(User user) {
        String sql = "INSERT INTO users(user_id, name, email, password, role, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUserId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole().name());
            ps.setBoolean(6, user.isActive());
            ps.executeUpdate();
            addRoleSpecificRecord(conn, user);
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to add user", ex);
        }
    }

    private void addRoleSpecificRecord(Connection conn, User user) throws SQLException {
        switch (user.getRole()) {
            case SHIPPER -> {
                Shipper shipper = (Shipper) user;
                String sql = "INSERT INTO shippers(user_id, company_name, contact_number, is_available) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, shipper.getUserId());
                    ps.setString(2, shipper.getCompanyName());
                    ps.setString(3, shipper.getContactNumber());
                    ps.setBoolean(4, shipper.isAvailable());
                    ps.executeUpdate();
                }
            }
            case MANUFACTURER -> {
                Manufacturer manufacturer = (Manufacturer) user;
                String sql = "INSERT INTO manufacturers(user_id, company_name) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, manufacturer.getUserId());
                    ps.setString(2, manufacturer.getCompanyName());
                    ps.executeUpdate();
                }
            }
            case RETAILER -> {
                Retailer retailer = (Retailer) user;
                String sql = "INSERT INTO retailers(user_id, organization_name, location) VALUES (?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, retailer.getUserId());
                    ps.setString(2, retailer.getOrganizationName());
                    ps.setString(3, retailer.getLocation());
                    ps.executeUpdate();
                }
            }
            case WAREHOUSE_MANAGER -> {
                WarehouseManager manager = (WarehouseManager) user;
                String sql = "INSERT INTO warehouse_managers(user_id, warehouse_id) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, manager.getUserId());
                    ps.setString(2, manager.getWarehouseId());
                    ps.executeUpdate();
                }
            }
            default -> {
            }
        }
    }

    public User findByEmailAndPassword(String email, String password) {
        String sql = "SELECT user_id, name, email, password, role, is_active FROM users WHERE email = ? AND password = ?";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                String roleStr = rs.getString("role");
                Role role = Role.valueOf(roleStr);
                return mapUserFromBase(conn, rs.getString("user_id"), rs.getString("name"), rs.getString("email"), rs.getString("password"), rs.getBoolean("is_active"), role);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to find user", ex);
        }
    }

    private User mapUserFromBase(Connection conn, String userId, String name, String email, String password, boolean active, Role role) throws SQLException {
        return switch (role) {
            case ADMIN -> new Admin(userId, name, email, password, active, "SYSTEM");
            case MANUFACTURER -> {
                String company = fetchSingleString(conn, "SELECT company_name FROM manufacturers WHERE user_id = ?", userId);
                yield new Manufacturer(userId, name, email, password, active, company == null ? "N/A" : company);
            }
            case SHIPPER -> {
                String company = fetchSingleString(conn, "SELECT company_name FROM shippers WHERE user_id = ?", userId);
                String contact = fetchSingleString(conn, "SELECT contact_number FROM shippers WHERE user_id = ?", userId);
                Boolean available = fetchSingleBoolean(conn, "SELECT is_available FROM shippers WHERE user_id = ?", userId);
                yield new Shipper(userId, name, email, password, active,
                        company == null ? "N/A" : company,
                        contact == null ? "N/A" : contact,
                        available == null || available);
            }
            case WAREHOUSE_MANAGER -> {
                String warehouseId = fetchSingleString(conn, "SELECT warehouse_id FROM warehouse_managers WHERE user_id = ?", userId);
                yield new WarehouseManager(userId, name, email, password, active, warehouseId == null ? "" : warehouseId);
            }
            case RETAILER -> {
                String org = fetchSingleString(conn, "SELECT organization_name FROM retailers WHERE user_id = ?", userId);
                String location = fetchSingleString(conn, "SELECT location FROM retailers WHERE user_id = ?", userId);
                yield new Retailer(userId, name, email, password, active,
                        org == null ? "N/A" : org,
                        location == null ? "N/A" : location);
            }
        };
    }

    private String fetchSingleString(Connection conn, String sql, String userId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    private Boolean fetchSingleBoolean(Connection conn, String sql, String userId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getBoolean(1) : null;
            }
        }
    }

    public List<User> findAllUsers() {
        String sql = "SELECT user_id, name, email, password, role, is_active FROM users ORDER BY user_id";
        List<User> users = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapUserFromBase(conn,
                        rs.getString("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("is_active"),
                        Role.valueOf(rs.getString("role"))));
            }
            return users;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to list users", ex);
        }
    }

    public List<Retailer> findAllRetailers() {
        List<Retailer> retailers = new ArrayList<>();
        for (User user : findAllUsers()) {
            if (user.getRole() == Role.RETAILER) {
                retailers.add((Retailer) user);
            }
        }
        return retailers;
    }

    public Retailer findRetailerById(String retailerId) {
        for (Retailer retailer : findAllRetailers()) {
            if (retailer.getUserId().equalsIgnoreCase(retailerId)) {
                return retailer;
            }
        }
        return null;
    }

    public void deleteUser(String userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to delete user", ex);
        }
    }
}
