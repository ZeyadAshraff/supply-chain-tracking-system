package database;

import entity.Vehicle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehicleRepository {
    private final DBConnection dbConnection;

    public VehicleRepository(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void addVehicle(String shipperId, Vehicle vehicle) {
        String sql = "INSERT INTO vehicles(vehicle_id, shipper_id, vehicle_type, license_plate, capacity, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vehicle.getVehicleId());
            ps.setString(2, shipperId);
            ps.setString(3, vehicle.getVehicleType());
            ps.setString(4, vehicle.getLicensePlate());
            ps.setInt(5, vehicle.getCapacity());
            ps.setString(6, vehicle.getStatus());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to add vehicle", ex);
        }
    }

    public void removeVehicle(String shipperId, String vehicleId) {
        String sql = "DELETE FROM vehicles WHERE shipper_id = ? AND vehicle_id = ?";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, shipperId);
            ps.setString(2, vehicleId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to remove vehicle", ex);
        }
    }

    public void updateVehicleStatus(String vehicleId, String status) {
        String sql = "UPDATE vehicles SET status = ? WHERE vehicle_id = ?";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, vehicleId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to update vehicle status", ex);
        }
    }

    public List<Vehicle> findAvailableVehicles(String shipperId) {
        String sql = "SELECT vehicle_id, vehicle_type, license_plate, capacity, status FROM vehicles WHERE shipper_id = ? AND status = 'AVAILABLE'";
        return findVehiclesByQuery(sql, shipperId);
    }

    public List<Vehicle> findVehiclesByShipper(String shipperId) {
        String sql = "SELECT vehicle_id, vehicle_type, license_plate, capacity, status FROM vehicles WHERE shipper_id = ? ORDER BY vehicle_id";
        return findVehiclesByQuery(sql, shipperId);
    }

    private List<Vehicle> findVehiclesByQuery(String sql, String shipperId) {
        List<Vehicle> vehicles = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, shipperId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(new Vehicle(
                            rs.getString("vehicle_id"),
                            rs.getString("vehicle_type"),
                            rs.getString("license_plate"),
                            rs.getInt("capacity"),
                            rs.getString("status")));
                }
            }
            return vehicles;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to retrieve vehicles", ex);
        }
    }
}
