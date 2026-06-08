package core;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class MockDataSeeder {
    private MockDataSeeder() {
    }

    public static void seed(AppContext context) {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            clearAllData(conn);
            seedUsers(conn);
            seedWarehouses(conn);
            seedManufacturerWarehouseLinks(conn);
            seedVehicles(conn);
            seedGoodsItemsInventory(conn);
            seedShipments(conn);
            seedShipmentItems(conn);
            seedEvents(conn);
            conn.commit();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to seed mock data", ex);
        }
    }

    private static void clearAllData(Connection conn) throws SQLException {
        // Remove dependent rows first to keep FK integrity.
        execute(conn, "DELETE FROM delivery_evidence");
        execute(conn, "DELETE FROM events");
        execute(conn, "DELETE FROM shipment_items");
        execute(conn, "DELETE FROM shipments");
        execute(conn, "DELETE FROM warehouse_inventory");
        execute(conn, "DELETE FROM items");
        execute(conn, "DELETE FROM goods");
        execute(conn, "DELETE FROM manufacturer_warehouses");
        execute(conn, "DELETE FROM vehicles");
        execute(conn, "DELETE FROM warehouse_managers");
        execute(conn, "DELETE FROM retailers");
        execute(conn, "DELETE FROM shippers");
        execute(conn, "DELETE FROM manufacturers");
        execute(conn, "DELETE FROM warehouses");
        execute(conn, "DELETE FROM users");
    }

    private static void seedUsers(Connection conn) throws SQLException {
        // 4 Admin
        insertUser(conn, "A1", "Admin One", "admin1@scts.com", "admin123", "ADMIN");
        insertUser(conn, "A2", "Admin Two", "admin2@scts.com", "admin123", "ADMIN");
        insertUser(conn, "A3", "Admin Three", "admin3@scts.com", "admin123", "ADMIN");
        insertUser(conn, "A4", "Admin Four", "admin4@scts.com", "admin123", "ADMIN");

        // 4 Manufacturers
        insertUser(conn, "M1", "Manufacturer One", "manufacturer1@scts.com", "man123", "MANUFACTURER");
        insertUser(conn, "M2", "Manufacturer Two", "manufacturer2@scts.com", "man123", "MANUFACTURER");
        insertUser(conn, "M3", "Manufacturer Three", "manufacturer3@scts.com", "man123", "MANUFACTURER");
        insertUser(conn, "M4", "Manufacturer Four", "manufacturer4@scts.com", "man123", "MANUFACTURER");

        execute(conn, "INSERT INTO manufacturers(user_id, company_name) VALUES ('M1', 'Acme Manufacturing')");
        execute(conn, "INSERT INTO manufacturers(user_id, company_name) VALUES ('M2', 'Nova Manufacturing')");
        execute(conn, "INSERT INTO manufacturers(user_id, company_name) VALUES ('M3', 'Atlas Manufacturing')");
        execute(conn, "INSERT INTO manufacturers(user_id, company_name) VALUES ('M4', 'Prime Manufacturing')");

        // 4 Shippers
        insertUser(conn, "S1", "Shipper One", "shipper1@scts.com", "ship123", "SHIPPER");
        insertUser(conn, "S2", "Shipper Two", "shipper2@scts.com", "ship123", "SHIPPER");
        insertUser(conn, "S3", "Shipper Three", "shipper3@scts.com", "ship123", "SHIPPER");
        insertUser(conn, "S4", "Shipper Four", "shipper4@scts.com", "ship123", "SHIPPER");

        execute(conn, "INSERT INTO shippers(user_id, company_name, contact_number, is_available) VALUES ('S1', 'Rapid Logistics', '01000000001', TRUE)");
        execute(conn, "INSERT INTO shippers(user_id, company_name, contact_number, is_available) VALUES ('S2', 'Swift Movers', '01000000002', TRUE)");
        execute(conn, "INSERT INTO shippers(user_id, company_name, contact_number, is_available) VALUES ('S3', 'Transit Pro', '01000000003', TRUE)");
        execute(conn, "INSERT INTO shippers(user_id, company_name, contact_number, is_available) VALUES ('S4', 'Cargo Link', '01000000004', TRUE)");

        // 4 Warehouse Managers
        insertUser(conn, "W1", "Warehouse Manager One", "wm1@scts.com", "ware123", "WAREHOUSE_MANAGER");
        insertUser(conn, "W2", "Warehouse Manager Two", "wm2@scts.com", "ware123", "WAREHOUSE_MANAGER");
        insertUser(conn, "W3", "Warehouse Manager Three", "wm3@scts.com", "ware123", "WAREHOUSE_MANAGER");
        insertUser(conn, "W4", "Warehouse Manager Four", "wm4@scts.com", "ware123", "WAREHOUSE_MANAGER");

        // 4 Retailers
        insertUser(conn, "R1", "Retailer One", "retailer1@scts.com", "ret123", "RETAILER");
        insertUser(conn, "R2", "Retailer Two", "retailer2@scts.com", "ret123", "RETAILER");
        insertUser(conn, "R3", "Retailer Three", "retailer3@scts.com", "ret123", "RETAILER");
        insertUser(conn, "R4", "Retailer Four", "retailer4@scts.com", "ret123", "RETAILER");

        execute(conn, "INSERT INTO retailers(user_id, organization_name, location) VALUES ('R1', 'Retail Cairo', 'Cairo')");
        execute(conn, "INSERT INTO retailers(user_id, organization_name, location) VALUES ('R2', 'Retail Alex', 'Alexandria')");
        execute(conn, "INSERT INTO retailers(user_id, organization_name, location) VALUES ('R3', 'Retail Giza', 'Giza')");
        execute(conn, "INSERT INTO retailers(user_id, organization_name, location) VALUES ('R4', 'Retail Mansoura', 'Mansoura')");
    }

    private static void seedWarehouses(Connection conn) throws SQLException {
        // 4 Warehouses
        execute(conn, "INSERT INTO warehouses(warehouse_id, name, location, capacity) VALUES ('WH1', 'Warehouse Cairo', 'Cairo', 1000)");
        execute(conn, "INSERT INTO warehouses(warehouse_id, name, location, capacity) VALUES ('WH2', 'Warehouse Alexandria', 'Alexandria', 1200)");
        execute(conn, "INSERT INTO warehouses(warehouse_id, name, location, capacity) VALUES ('WH3', 'Warehouse Giza', 'Giza', 900)");
        execute(conn, "INSERT INTO warehouses(warehouse_id, name, location, capacity) VALUES ('WH4', 'Warehouse Mansoura', 'Mansoura', 1100)");

        execute(conn, "INSERT INTO warehouse_managers(user_id, warehouse_id) VALUES ('W1', 'WH1')");
        execute(conn, "INSERT INTO warehouse_managers(user_id, warehouse_id) VALUES ('W2', 'WH2')");
        execute(conn, "INSERT INTO warehouse_managers(user_id, warehouse_id) VALUES ('W3', 'WH3')");
        execute(conn, "INSERT INTO warehouse_managers(user_id, warehouse_id) VALUES ('W4', 'WH4')");
    }

    private static void seedManufacturerWarehouseLinks(Connection conn) throws SQLException {
        execute(conn, "INSERT INTO manufacturer_warehouses(manufacturer_id, warehouse_id) VALUES ('M1', 'WH1')");
        execute(conn, "INSERT INTO manufacturer_warehouses(manufacturer_id, warehouse_id) VALUES ('M2', 'WH2')");
        execute(conn, "INSERT INTO manufacturer_warehouses(manufacturer_id, warehouse_id) VALUES ('M3', 'WH3')");
        execute(conn, "INSERT INTO manufacturer_warehouses(manufacturer_id, warehouse_id) VALUES ('M4', 'WH4')");
    }

    private static void seedVehicles(Connection conn) throws SQLException {
        // 8 Vehicles (2 per shipper)
        execute(conn, "INSERT INTO vehicles(vehicle_id, shipper_id, vehicle_type, license_plate, capacity, status) VALUES ('V1', 'S1', 'TRUCK', 'EG-1111', 200, 'AVAILABLE')");
        execute(conn, "INSERT INTO vehicles(vehicle_id, shipper_id, vehicle_type, license_plate, capacity, status) VALUES ('V2', 'S1', 'VAN', 'EG-1112', 120, 'AVAILABLE')");
        execute(conn, "INSERT INTO vehicles(vehicle_id, shipper_id, vehicle_type, license_plate, capacity, status) VALUES ('V3', 'S2', 'TRUCK', 'EG-2221', 250, 'AVAILABLE')");
        execute(conn, "INSERT INTO vehicles(vehicle_id, shipper_id, vehicle_type, license_plate, capacity, status) VALUES ('V4', 'S2', 'TRUCK', 'EG-2222', 220, 'AVAILABLE')");
        execute(conn, "INSERT INTO vehicles(vehicle_id, shipper_id, vehicle_type, license_plate, capacity, status) VALUES ('V5', 'S3', 'VAN', 'EG-3331', 140, 'AVAILABLE')");
        execute(conn, "INSERT INTO vehicles(vehicle_id, shipper_id, vehicle_type, license_plate, capacity, status) VALUES ('V6', 'S3', 'TRUCK', 'EG-3332', 280, 'AVAILABLE')");
        execute(conn, "INSERT INTO vehicles(vehicle_id, shipper_id, vehicle_type, license_plate, capacity, status) VALUES ('V7', 'S4', 'TRUCK', 'EG-4441', 300, 'AVAILABLE')");
        execute(conn, "INSERT INTO vehicles(vehicle_id, shipper_id, vehicle_type, license_plate, capacity, status) VALUES ('V8', 'S4', 'VAN', 'EG-4442', 110, 'AVAILABLE')");
    }

    private static void seedGoodsItemsInventory(Connection conn) throws SQLException {
        // 12 Goods (3 per manufacturer)
        execute(conn, "INSERT INTO goods(good_id, label, description, manufacturer_id, weight, volume) VALUES ('G1', 'Canned Food', 'Sealed food packs', 'M1', 2.5, 1.0)");
        execute(conn, "INSERT INTO goods(good_id, label, description, manufacturer_id, weight, volume) VALUES ('G2', 'Bottled Water', 'Mineral water bottles', 'M1', 1.0, 0.8)");
        execute(conn, "INSERT INTO goods(good_id, label, description, manufacturer_id, weight, volume) VALUES ('G3', 'Dry Beans', 'Packaged legumes', 'M1', 1.8, 0.9)");

        execute(conn, "INSERT INTO goods(good_id, label, description, manufacturer_id, weight, volume) VALUES ('G4', 'Medical Kits', 'Emergency kits', 'M2', 1.2, 0.7)");
        execute(conn, "INSERT INTO goods(good_id, label, description, manufacturer_id, weight, volume) VALUES ('G5', 'Sanitizers', 'Alcohol hand sanitizers', 'M2', 0.9, 0.6)");
        execute(conn, "INSERT INTO goods(good_id, label, description, manufacturer_id, weight, volume) VALUES ('G6', 'Face Masks', 'Protective masks', 'M2', 0.4, 0.4)");

        execute(conn, "INSERT INTO goods(good_id, label, description, manufacturer_id, weight, volume) VALUES ('G7', 'Electronics', 'Fragile electronics', 'M3', 4.0, 1.8)");
        execute(conn, "INSERT INTO goods(good_id, label, description, manufacturer_id, weight, volume) VALUES ('G8', 'Phone Accessories', 'Chargers and cables', 'M3', 0.8, 0.5)");
        execute(conn, "INSERT INTO goods(good_id, label, description, manufacturer_id, weight, volume) VALUES ('G9', 'Small Appliances', 'Kitchen appliances', 'M3', 5.2, 2.2)");

        execute(conn, "INSERT INTO goods(good_id, label, description, manufacturer_id, weight, volume) VALUES ('G10', 'Textiles', 'Packaged fabrics', 'M4', 3.1, 1.5)");
        execute(conn, "INSERT INTO goods(good_id, label, description, manufacturer_id, weight, volume) VALUES ('G11', 'Sportswear', 'Athletic clothing', 'M4', 2.0, 1.1)");
        execute(conn, "INSERT INTO goods(good_id, label, description, manufacturer_id, weight, volume) VALUES ('G12', 'Blankets', 'Household blankets', 'M4', 2.9, 1.4)");

        // 12 Items
        LocalDate d = LocalDate.now().minusDays(5);
        insertItem(conn, "I1", "G1", 100, d);
        insertItem(conn, "I2", "G2", 150, d);
        insertItem(conn, "I3", "G3", 130, d);
        insertItem(conn, "I4", "G4", 80, d);
        insertItem(conn, "I5", "G5", 120, d);
        insertItem(conn, "I6", "G6", 200, d);
        insertItem(conn, "I7", "G7", 60, d);
        insertItem(conn, "I8", "G8", 170, d);
        insertItem(conn, "I9", "G9", 55, d);
        insertItem(conn, "I10", "G10", 90, d);
        insertItem(conn, "I11", "G11", 140, d);
        insertItem(conn, "I12", "G12", 75, d);

        // Inventory rows distributed across all warehouses
        execute(conn, "INSERT INTO warehouse_inventory(warehouse_id, item_id, quantity) VALUES ('WH1', 'I1', 100)");
        execute(conn, "INSERT INTO warehouse_inventory(warehouse_id, item_id, quantity) VALUES ('WH1', 'I2', 150)");
        execute(conn, "INSERT INTO warehouse_inventory(warehouse_id, item_id, quantity) VALUES ('WH1', 'I3', 130)");

        execute(conn, "INSERT INTO warehouse_inventory(warehouse_id, item_id, quantity) VALUES ('WH2', 'I4', 80)");
        execute(conn, "INSERT INTO warehouse_inventory(warehouse_id, item_id, quantity) VALUES ('WH2', 'I5', 120)");
        execute(conn, "INSERT INTO warehouse_inventory(warehouse_id, item_id, quantity) VALUES ('WH2', 'I6', 200)");

        execute(conn, "INSERT INTO warehouse_inventory(warehouse_id, item_id, quantity) VALUES ('WH3', 'I7', 60)");
        execute(conn, "INSERT INTO warehouse_inventory(warehouse_id, item_id, quantity) VALUES ('WH3', 'I8', 170)");
        execute(conn, "INSERT INTO warehouse_inventory(warehouse_id, item_id, quantity) VALUES ('WH3', 'I9', 55)");

        execute(conn, "INSERT INTO warehouse_inventory(warehouse_id, item_id, quantity) VALUES ('WH4', 'I10', 90)");
        execute(conn, "INSERT INTO warehouse_inventory(warehouse_id, item_id, quantity) VALUES ('WH4', 'I11', 140)");
        execute(conn, "INSERT INTO warehouse_inventory(warehouse_id, item_id, quantity) VALUES ('WH4', 'I12', 75)");
    }

    private static void seedShipments(Connection conn) throws SQLException {
        // 12 shipments with mixed routes and states
        insertShipment(conn, "SHP1", "Cairo", "Alexandria", "S1", "WH1", "WAREHOUSE", "WH2", null);
        insertShipment(conn, "SHP2", "Alexandria", "Cairo", "S2", "WH2", "RETAILER", null, "R1");
        insertShipment(conn, "SHP3", "Giza", "Alexandria", "S3", "WH3", "RETAILER", null, "R2");
        insertShipment(conn, "SHP4", "Mansoura", "Giza", "S4", "WH4", "RETAILER", null, "R3");
        insertShipment(conn, "SHP5", "Cairo", "Mansoura", "S1", "WH1", "RETAILER", null, "R4");
        insertShipment(conn, "SHP6", "Alexandria", "Giza", "S2", "WH2", "WAREHOUSE", "WH3", null);
        insertShipment(conn, "SHP7", "Giza", "Cairo", "S3", "WH3", "RETAILER", null, "R1");
        insertShipment(conn, "SHP8", "Mansoura", "Alexandria", "S4", "WH4", "RETAILER", null, "R2");
        insertShipment(conn, "SHP9", "Cairo", "Giza", "S1", "WH1", "RETAILER", null, "R3");
        insertShipment(conn, "SHP10", "Alexandria", "Mansoura", "S2", "WH2", "RETAILER", null, "R4");
        insertShipment(conn, "SHP11", "Giza", "Mansoura", "S3", "WH3", "WAREHOUSE", "WH4", null);
        insertShipment(conn, "SHP12", "Mansoura", "Cairo", "S4", "WH4", "RETAILER", null, "R1");
    }

    private static void seedShipmentItems(Connection conn) throws SQLException {
        linkShipmentItem(conn, "SHP1", "I1");
        linkShipmentItem(conn, "SHP2", "I4");
        linkShipmentItem(conn, "SHP3", "I7");
        linkShipmentItem(conn, "SHP4", "I10");
        linkShipmentItem(conn, "SHP5", "I2");
        linkShipmentItem(conn, "SHP6", "I5");
        linkShipmentItem(conn, "SHP7", "I8");
        linkShipmentItem(conn, "SHP8", "I11");
        linkShipmentItem(conn, "SHP9", "I3");
        linkShipmentItem(conn, "SHP10", "I6");
        linkShipmentItem(conn, "SHP11", "I9");
        linkShipmentItem(conn, "SHP12", "I12");
    }

    private static void seedEvents(Connection conn) throws SQLException {
        // Scenario coverage: created, accepted, in-transit, delay, receipt, inspection, storage,
        // delivered, confirmed, failed-delivery, return-requested, return-dispatched, returned.

        // SHP1 warehouse-to-warehouse, fully processed to storage
        seedEvent(conn, "E1", "SHP1", "SHIPMENT_CREATED", "Shipment created", "createdBy=M1", LocalDateTime.now().minusDays(10));
        seedEvent(conn, "E2", "SHP1", "SHIPMENT_ACCEPTED", "Accepted by shipper", "acceptedBy=S1", LocalDateTime.now().minusDays(10).plusHours(2));
        seedEvent(conn, "E3", "SHP1", "DISPATCH", "Dispatched", "vehicleId=V1", LocalDateTime.now().minusDays(10).plusHours(5));
        seedEvent(conn, "E4", "SHP1", "RECEIPT", "Received at WH2", "receivedCondition=GOOD", LocalDateTime.now().minusDays(9));
        seedEvent(conn, "E5", "SHP1", "INSPECTION", "Inspection", "inspectionStatus=INSPECTED_APPROVED;notes=OK", LocalDateTime.now().minusDays(9).plusHours(2));
        seedEvent(conn, "E6", "SHP1", "STORAGE", "Stored", "storageLocation=Zone-A2", LocalDateTime.now().minusDays(9).plusHours(4));

        // SHP2 delivered + confirmed
        seedEvent(conn, "E7", "SHP2", "SHIPMENT_CREATED", "Shipment created", "createdBy=M2", LocalDateTime.now().minusDays(8));
        seedEvent(conn, "E8", "SHP2", "SHIPMENT_ACCEPTED", "Accepted by shipper", "acceptedBy=S2", LocalDateTime.now().minusDays(8).plusHours(1));
        seedEvent(conn, "E9", "SHP2", "DISPATCH", "Dispatched", "vehicleId=V3", LocalDateTime.now().minusDays(8).plusHours(3));
        seedEvent(conn, "E10", "SHP2", "DELIVERY", "Delivered", "evidenceId=EV2;path=evidence/s2-delivery.jpg", LocalDateTime.now().minusDays(7));
        insertDeliveryEvidence(conn, "EV2", "E10", "evidence/s2-delivery.jpg", LocalDateTime.now().minusDays(7), "UNVERIFIED");
        seedEvent(conn, "E11", "SHP2", "DELIVERY_CONFIRMATION", "Confirmed by retailer", "confirmation=Received in good condition", LocalDateTime.now().minusDays(7).plusHours(1));

        // SHP3 delayed in-transit
        seedEvent(conn, "E12", "SHP3", "SHIPMENT_CREATED", "Shipment created", "createdBy=M3", LocalDateTime.now().minusDays(6));
        seedEvent(conn, "E13", "SHP3", "SHIPMENT_ACCEPTED", "Accepted by shipper", "acceptedBy=S3", LocalDateTime.now().minusDays(6).plusHours(1));
        seedEvent(conn, "E14", "SHP3", "DISPATCH", "Dispatched", "vehicleId=V5", LocalDateTime.now().minusDays(6).plusHours(3));
        seedEvent(conn, "E15", "SHP3", "DELAY", "Delayed", "delayReason=Road closure", LocalDateTime.now().minusDays(5).plusHours(5));

        // SHP4 failed delivery then return flow complete
        seedEvent(conn, "E16", "SHP4", "SHIPMENT_CREATED", "Shipment created", "createdBy=M4", LocalDateTime.now().minusDays(5));
        seedEvent(conn, "E17", "SHP4", "SHIPMENT_ACCEPTED", "Accepted by shipper", "acceptedBy=S4", LocalDateTime.now().minusDays(5).plusHours(1));
        seedEvent(conn, "E18", "SHP4", "DISPATCH", "Dispatched", "vehicleId=V7", LocalDateTime.now().minusDays(5).plusHours(3));
        seedEvent(conn, "E19", "SHP4", "DELIVERY", "Delivered attempt", "evidenceId=EV4;path=evidence/s4-attempt.jpg", LocalDateTime.now().minusDays(4));
        insertDeliveryEvidence(conn, "EV4", "E19", "evidence/s4-attempt.jpg", LocalDateTime.now().minusDays(4), "UNVERIFIED");
        seedEvent(conn, "E20", "SHP4", "FAILED_DELIVERY", "Failed at destination", "reason=Damaged on arrival;evidencePath=evidence/s4-failed.jpg", LocalDateTime.now().minusDays(4).plusHours(1));
        seedEvent(conn, "E21", "SHP4", "RETURN_REQUESTED", "Return requested", "returnReason=Damage confirmed;evidencePath=evidence/s4-failed.jpg;returnWarehouseId=WH4", LocalDateTime.now().minusDays(4).plusHours(2));
        seedEvent(conn, "E22", "SHP4", "RETURN_DISPATCHED", "Return dispatched", "vehicleId=V8", LocalDateTime.now().minusDays(4).plusHours(3));
        seedEvent(conn, "E23", "SHP4", "RETURNED", "Returned to origin", "warehouseId=WH4", LocalDateTime.now().minusDays(3));

        // SHP5 return-requested pending dispatch
        seedEvent(conn, "E24", "SHP5", "SHIPMENT_CREATED", "Shipment created", "createdBy=M1", LocalDateTime.now().minusDays(3));
        seedEvent(conn, "E25", "SHP5", "SHIPMENT_ACCEPTED", "Accepted by shipper", "acceptedBy=S1", LocalDateTime.now().minusDays(3).plusHours(1));
        seedEvent(conn, "E26", "SHP5", "DISPATCH", "Dispatched", "vehicleId=V2", LocalDateTime.now().minusDays(3).plusHours(2));
        seedEvent(conn, "E27", "SHP5", "DELIVERY", "Delivered attempt", "evidenceId=EV5;path=evidence/s5-attempt.jpg", LocalDateTime.now().minusDays(2));
        insertDeliveryEvidence(conn, "EV5", "E27", "evidence/s5-attempt.jpg", LocalDateTime.now().minusDays(2), "UNVERIFIED");
        seedEvent(conn, "E28", "SHP5", "FAILED_DELIVERY", "Failed at destination", "reason=Wrong package;evidencePath=evidence/s5-failed.jpg", LocalDateTime.now().minusDays(2).plusHours(1));
        seedEvent(conn, "E29", "SHP5", "RETURN_REQUESTED", "Return requested", "returnReason=Incorrect order;evidencePath=evidence/s5-failed.jpg;returnWarehouseId=WH1", LocalDateTime.now().minusDays(2).plusHours(2));

        // SHP6 received but not inspected yet
        seedEvent(conn, "E30", "SHP6", "SHIPMENT_CREATED", "Shipment created", "createdBy=M2", LocalDateTime.now().minusDays(3));
        seedEvent(conn, "E31", "SHP6", "SHIPMENT_ACCEPTED", "Accepted by shipper", "acceptedBy=S2", LocalDateTime.now().minusDays(3).plusHours(1));
        seedEvent(conn, "E32", "SHP6", "DISPATCH", "Dispatched", "vehicleId=V4", LocalDateTime.now().minusDays(3).plusHours(3));
        seedEvent(conn, "E33", "SHP6", "RECEIPT", "Received at WH3", "receivedCondition=GOOD", LocalDateTime.now().minusDays(2));

        // SHP7 only created
        seedEvent(conn, "E34", "SHP7", "SHIPMENT_CREATED", "Shipment created", "createdBy=M3", LocalDateTime.now().minusDays(2));

        // SHP8 created + accepted
        seedEvent(conn, "E35", "SHP8", "SHIPMENT_CREATED", "Shipment created", "createdBy=M4", LocalDateTime.now().minusDays(2));
        seedEvent(conn, "E36", "SHP8", "SHIPMENT_ACCEPTED", "Accepted by shipper", "acceptedBy=S4", LocalDateTime.now().minusDays(2).plusHours(2));

        // SHP9 in transit
        seedEvent(conn, "E37", "SHP9", "SHIPMENT_CREATED", "Shipment created", "createdBy=M1", LocalDateTime.now().minusDays(2));
        seedEvent(conn, "E38", "SHP9", "SHIPMENT_ACCEPTED", "Accepted by shipper", "acceptedBy=S1", LocalDateTime.now().minusDays(2).plusHours(2));
        seedEvent(conn, "E39", "SHP9", "DISPATCH", "Dispatched", "vehicleId=V1", LocalDateTime.now().minusDays(2).plusHours(4));

        // SHP10 delivered (awaiting confirmation)
        seedEvent(conn, "E40", "SHP10", "SHIPMENT_CREATED", "Shipment created", "createdBy=M2", LocalDateTime.now().minusDays(2));
        seedEvent(conn, "E41", "SHP10", "SHIPMENT_ACCEPTED", "Accepted by shipper", "acceptedBy=S2", LocalDateTime.now().minusDays(2).plusHours(1));
        seedEvent(conn, "E42", "SHP10", "DISPATCH", "Dispatched", "vehicleId=V3", LocalDateTime.now().minusDays(2).plusHours(3));
        seedEvent(conn, "E43", "SHP10", "DELIVERY", "Delivered", "evidenceId=EV10;path=evidence/s10-delivery.jpg", LocalDateTime.now().minusDays(1));
        insertDeliveryEvidence(conn, "EV10", "E43", "evidence/s10-delivery.jpg", LocalDateTime.now().minusDays(1), "UNVERIFIED");

        // SHP11 warehouse route with delay
        seedEvent(conn, "E44", "SHP11", "SHIPMENT_CREATED", "Shipment created", "createdBy=M3", LocalDateTime.now().minusDays(2));
        seedEvent(conn, "E45", "SHP11", "SHIPMENT_ACCEPTED", "Accepted by shipper", "acceptedBy=S3", LocalDateTime.now().minusDays(2).plusHours(1));
        seedEvent(conn, "E46", "SHP11", "DISPATCH", "Dispatched", "vehicleId=V6", LocalDateTime.now().minusDays(2).plusHours(3));
        seedEvent(conn, "E47", "SHP11", "DELAY", "Delayed", "delayReason=Mechanical issue", LocalDateTime.now().minusDays(1).plusHours(4));

        // SHP12 delivered then failed and return requested (for shipper actions)
        seedEvent(conn, "E48", "SHP12", "SHIPMENT_CREATED", "Shipment created", "createdBy=M4", LocalDateTime.now().minusDays(1));
        seedEvent(conn, "E49", "SHP12", "SHIPMENT_ACCEPTED", "Accepted by shipper", "acceptedBy=S4", LocalDateTime.now().minusDays(1).plusHours(1));
        seedEvent(conn, "E50", "SHP12", "DISPATCH", "Dispatched", "vehicleId=V7", LocalDateTime.now().minusDays(1).plusHours(3));
        seedEvent(conn, "E51", "SHP12", "DELIVERY", "Delivered attempt", "evidenceId=EV12;path=evidence/s12-delivery.jpg", LocalDateTime.now().minusDays(1).plusHours(8));
        insertDeliveryEvidence(conn, "EV12", "E51", "evidence/s12-delivery.jpg", LocalDateTime.now().minusDays(1).plusHours(8), "UNVERIFIED");
        seedEvent(conn, "E52", "SHP12", "FAILED_DELIVERY", "Failed after handoff", "reason=Quality issue on unpacking;evidencePath=evidence/s12-failed.jpg", LocalDateTime.now().minusHours(8));
        seedEvent(conn, "E53", "SHP12", "RETURN_REQUESTED", "Return requested", "returnReason=Quality rejected;evidencePath=evidence/s12-failed.jpg;returnWarehouseId=WH4", LocalDateTime.now().minusHours(7));
    }

    private static void insertShipment(Connection conn, String shipmentId, String pickup, String destination, String shipperId,
                                       String sourceWarehouseId, String destinationType, String destinationWarehouseId,
                                       String destinationRetailerId) throws SQLException {
        String sql = """
                INSERT INTO shipments(
                    shipment_id, pickup_location, destination, shipper_id,
                    source_warehouse_id, destination_type, destination_warehouse_id, destination_retailer_id
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, shipmentId);
            ps.setString(2, pickup);
            ps.setString(3, destination);
            ps.setString(4, shipperId);
            ps.setString(5, sourceWarehouseId);
            ps.setString(6, destinationType);
            ps.setString(7, destinationWarehouseId);
            ps.setString(8, destinationRetailerId);
            ps.executeUpdate();
        }
    }

    private static void linkShipmentItem(Connection conn, String shipmentId, String itemId) throws SQLException {
        String sql = "INSERT INTO shipment_items(shipment_id, item_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, shipmentId);
            ps.setString(2, itemId);
            ps.executeUpdate();
        }
    }

    private static void seedEvent(Connection conn, String eventId, String shipmentId, String eventType, String description, String details, LocalDateTime time) throws SQLException {
        String sql = "INSERT INTO events(event_id, shipment_id, event_type, timestamp_value, description, details_json) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventId);
            ps.setString(2, shipmentId);
            ps.setString(3, eventType);
            ps.setTimestamp(4, Timestamp.valueOf(time));
            ps.setString(5, description);
            ps.setString(6, details);
            ps.executeUpdate();
        }
    }

    private static void insertUser(Connection conn, String userId, String name, String email, String password, String role) throws SQLException {
        String sql = "INSERT INTO users(user_id, name, email, password, role, is_active) VALUES (?, ?, ?, ?, ?, TRUE)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setString(5, role);
            ps.executeUpdate();
        }
    }

    private static void insertItem(Connection conn, String itemId, String goodId, int quantity, LocalDate date) throws SQLException {
        String sql = "INSERT INTO items(item_id, good_id, quantity, manufacture_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, itemId);
            ps.setString(2, goodId);
            ps.setInt(3, quantity);
            ps.setDate(4, java.sql.Date.valueOf(date));
            ps.executeUpdate();
        }
    }

    private static void insertDeliveryEvidence(Connection conn, String evidenceId, String eventId, String filePath,
                                               LocalDateTime timestamp, String status) throws SQLException {
        String sql = "INSERT INTO delivery_evidence(evidence_id, event_id, file_path, timestamp_value, verification_status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, evidenceId);
            ps.setString(2, eventId);
            ps.setString(3, filePath);
            ps.setTimestamp(4, Timestamp.valueOf(timestamp));
            ps.setString(5, status);
            ps.executeUpdate();
        }
    }

    private static void execute(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
}
