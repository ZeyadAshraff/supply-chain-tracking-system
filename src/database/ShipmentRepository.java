package database;

import entity.Item;
import entity.Role;
import entity.Shipment;
import entity.Shipper;
import entity.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShipmentRepository {
    private final DBConnection dbConnection;

    public ShipmentRepository(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void ensureShipmentSchema() {
        ensureColumn("source_warehouse_id", "VARCHAR(40) NULL");
        ensureColumn("destination_type", "VARCHAR(20) NOT NULL DEFAULT 'TEXT'");
        ensureColumn("destination_warehouse_id", "VARCHAR(40) NULL");
        ensureColumn("destination_retailer_id", "VARCHAR(40) NULL");
    }

    private void ensureColumn(String columnName, String sqlType) {
        String sql = "ALTER TABLE shipments ADD COLUMN " + columnName + " " + sqlType;
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            // Column already exists.
        }
    }

    public void createShipment(Shipment shipment) {
        String sql = """
                INSERT INTO shipments(
                    shipment_id, pickup_location, destination, shipper_id,
                    source_warehouse_id, destination_type, destination_warehouse_id, destination_retailer_id
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            ps.setString(1, shipment.getShipmentId());
            ps.setString(2, shipment.getPickupLocation());
            ps.setString(3, shipment.getDestination());
            if (shipment.getAssignedShipper() != null) {
                ps.setString(4, shipment.getAssignedShipper().getUserId());
            } else {
                ps.setNull(4, java.sql.Types.VARCHAR);
            }
            ps.setString(5, shipment.getSourceWarehouseId());
            ps.setString(6, shipment.getDestinationType());
            ps.setString(7, shipment.getDestinationWarehouseId());
            ps.setString(8, shipment.getDestinationRetailerId());
            ps.executeUpdate();

            for (Item item : shipment.getItems()) {
                if (itemExists(conn, item.getItemId())) {
                    addShipmentItem(conn, shipment.getShipmentId(), item.getItemId());
                }
            }
            conn.commit();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to create shipment: " + ex.getMessage(), ex);
        }
    }

    private boolean itemExists(Connection conn, String itemId) throws SQLException {
        String sql = "SELECT 1 FROM items WHERE item_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void addShipmentItem(Connection conn, String shipmentId, String itemId) throws SQLException {
        String sql = "INSERT INTO shipment_items(shipment_id, item_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, shipmentId);
            ps.setString(2, itemId);
            ps.executeUpdate();
        }
    }

    public void assignShipper(String shipmentId, Shipper shipper) {
        String sql = "UPDATE shipments SET shipper_id = ? WHERE shipment_id = ?";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, shipper.getUserId());
            ps.setString(2, shipmentId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to assign shipper", ex);
        }
    }

    public Shipment findById(String shipmentId) {
        String sql = """
                SELECT shipment_id, pickup_location, destination, shipper_id,
                       source_warehouse_id, destination_type, destination_warehouse_id, destination_retailer_id
                FROM shipments WHERE shipment_id = ?
                """;
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, shipmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapShipment(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to fetch shipment", ex);
        }
    }

    public List<Shipment> findAllShipments() {
        String sql = """
                SELECT shipment_id, pickup_location, destination, shipper_id,
                       source_warehouse_id, destination_type, destination_warehouse_id, destination_retailer_id
                FROM shipments ORDER BY shipment_id
                """;
        List<Shipment> shipments = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                shipments.add(mapShipment(rs));
            }
            return shipments;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to list shipments", ex);
        }
    }

    public List<Shipment> findVisibleShipments(User actor) {
        if (actor.getRole() == Role.ADMIN) {
            return findAllShipments();
        }

        String sql = switch (actor.getRole()) {
            case SHIPPER -> """
                    SELECT shipment_id, pickup_location, destination, shipper_id,
                           source_warehouse_id, destination_type, destination_warehouse_id, destination_retailer_id
                    FROM shipments
                    WHERE shipper_id = ?
                    ORDER BY shipment_id
                    """;
            case MANUFACTURER -> """
                    SELECT DISTINCT s.shipment_id, s.pickup_location, s.destination, s.shipper_id,
                                    s.source_warehouse_id, s.destination_type, s.destination_warehouse_id, s.destination_retailer_id
                    FROM shipments s
                    LEFT JOIN shipment_items si ON s.shipment_id = si.shipment_id
                    LEFT JOIN items i ON si.item_id = i.item_id
                    LEFT JOIN goods g ON i.good_id = g.good_id
                    LEFT JOIN manufacturer_warehouses mw ON mw.manufacturer_id = ?
                    WHERE g.manufacturer_id = ?
                       OR s.source_warehouse_id = mw.warehouse_id
                    ORDER BY s.shipment_id
                    """;
            case WAREHOUSE_MANAGER -> """
                    SELECT s.shipment_id, s.pickup_location, s.destination, s.shipper_id,
                           s.source_warehouse_id, s.destination_type, s.destination_warehouse_id, s.destination_retailer_id
                    FROM shipments s
                    WHERE s.source_warehouse_id = ?
                       OR s.destination_warehouse_id = ?
                       OR s.pickup_location = (SELECT w.location FROM warehouses w WHERE w.warehouse_id = ?)
                       OR s.destination = (SELECT w.location FROM warehouses w WHERE w.warehouse_id = ?)
                    ORDER BY s.shipment_id
                    """;
            case RETAILER -> """
                    SELECT s.shipment_id, s.pickup_location, s.destination, s.shipper_id,
                           s.source_warehouse_id, s.destination_type, s.destination_warehouse_id, s.destination_retailer_id
                    FROM shipments s
                    JOIN retailers r ON r.user_id = ?
                    WHERE s.destination_retailer_id = r.user_id
                       OR (s.destination_type = 'TEXT' AND s.destination = r.location)
                    ORDER BY s.shipment_id
                    """;
            default -> throw new IllegalArgumentException("Unsupported role for shipment view: " + actor.getRole());
        };

        List<Shipment> shipments = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            switch (actor.getRole()) {
                case SHIPPER, RETAILER -> ps.setString(1, actor.getUserId());
                case MANUFACTURER -> {
                    ps.setString(1, actor.getUserId());
                    ps.setString(2, actor.getUserId());
                }
                case WAREHOUSE_MANAGER -> {
                    String warehouseId = findWarehouseIdForManager(conn, actor.getUserId());
                    if (warehouseId == null || warehouseId.isBlank()) {
                        return shipments;
                    }
                    ps.setString(1, warehouseId);
                    ps.setString(2, warehouseId);
                    ps.setString(3, warehouseId);
                    ps.setString(4, warehouseId);
                }
                default -> {
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    shipments.add(mapShipment(rs));
                }
            }
            return shipments;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to list visible shipments", ex);
        }
    }

    public boolean canUserViewShipment(User actor, String shipmentId) {
        for (Shipment shipment : findVisibleShipments(actor)) {
            if (shipment.getShipmentId().equalsIgnoreCase(shipmentId)) {
                return true;
            }
        }
        return false;
    }

    private Shipment mapShipment(ResultSet rs) throws SQLException {
        Shipment shipment = new Shipment(
                rs.getString("shipment_id"),
                rs.getString("source_warehouse_id"),
                rs.getString("destination_type"),
                rs.getString("destination_warehouse_id"),
                rs.getString("destination_retailer_id"),
                rs.getString("pickup_location"),
                rs.getString("destination"),
                new ArrayList<>());

        String shipperId = rs.getString("shipper_id");
        if (shipperId != null && !shipperId.isBlank()) {
            shipment.assignShipper(new Shipper(shipperId, shipperId, "", "", true, "", "", true));
        }
        return shipment;
    }

    private String findWarehouseIdForManager(Connection conn, String managerId) throws SQLException {
        String sql = "SELECT warehouse_id FROM warehouse_managers WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("warehouse_id") : null;
            }
        }
    }
}
