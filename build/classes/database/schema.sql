CREATE DATABASE IF NOT EXISTS scts_phase3;
USE scts_phase3;

CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(40) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL,
    role VARCHAR(40) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS manufacturers (
    user_id VARCHAR(40) PRIMARY KEY,
    company_name VARCHAR(120) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS retailers (
    user_id VARCHAR(40) PRIMARY KEY,
    organization_name VARCHAR(120) NOT NULL,
    location VARCHAR(120) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS warehouses (
    warehouse_id VARCHAR(40) PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    location VARCHAR(120) NOT NULL,
    capacity INT NOT NULL
);

CREATE TABLE IF NOT EXISTS warehouse_managers (
    user_id VARCHAR(40) PRIMARY KEY,
    warehouse_id VARCHAR(40) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(warehouse_id)
);

CREATE TABLE IF NOT EXISTS shippers (
    user_id VARCHAR(40) PRIMARY KEY,
    company_name VARCHAR(120) NOT NULL,
    contact_number VARCHAR(40) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS vehicles (
    vehicle_id VARCHAR(40) PRIMARY KEY,
    shipper_id VARCHAR(40) NOT NULL,
    vehicle_type VARCHAR(60) NOT NULL,
    license_plate VARCHAR(30) NOT NULL,
    capacity INT NOT NULL,
    status VARCHAR(40) NOT NULL,
    FOREIGN KEY (shipper_id) REFERENCES shippers(user_id)
);

CREATE TABLE IF NOT EXISTS goods (
    good_id VARCHAR(40) PRIMARY KEY,
    label VARCHAR(120) NOT NULL,
    description VARCHAR(255) NOT NULL,
    manufacturer_id VARCHAR(40) NOT NULL,
    weight DOUBLE NOT NULL,
    volume DOUBLE NOT NULL,
    FOREIGN KEY (manufacturer_id) REFERENCES manufacturers(user_id)
);

CREATE TABLE IF NOT EXISTS items (
    item_id VARCHAR(40) PRIMARY KEY,
    good_id VARCHAR(40) NOT NULL,
    quantity INT NOT NULL,
    manufacture_date DATE NOT NULL,
    FOREIGN KEY (good_id) REFERENCES goods(good_id)
);

CREATE TABLE IF NOT EXISTS shipments (
    shipment_id VARCHAR(40) PRIMARY KEY,
    pickup_location VARCHAR(120) NOT NULL,
    destination VARCHAR(120) NOT NULL,
    shipper_id VARCHAR(40),
    source_warehouse_id VARCHAR(40),
    destination_type VARCHAR(20) NOT NULL DEFAULT 'TEXT',
    destination_warehouse_id VARCHAR(40),
    destination_retailer_id VARCHAR(40),
    FOREIGN KEY (shipper_id) REFERENCES shippers(user_id)
);

CREATE TABLE IF NOT EXISTS shipment_items (
    shipment_id VARCHAR(40) NOT NULL,
    item_id VARCHAR(40) NOT NULL,
    PRIMARY KEY (shipment_id, item_id),
    FOREIGN KEY (shipment_id) REFERENCES shipments(shipment_id),
    FOREIGN KEY (item_id) REFERENCES items(item_id)
);

CREATE TABLE IF NOT EXISTS warehouse_inventory (
    warehouse_id VARCHAR(40) NOT NULL,
    item_id VARCHAR(40) NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY (warehouse_id, item_id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(warehouse_id),
    FOREIGN KEY (item_id) REFERENCES items(item_id)
);

CREATE TABLE IF NOT EXISTS manufacturer_warehouses (
    manufacturer_id VARCHAR(40) PRIMARY KEY,
    warehouse_id VARCHAR(40) NOT NULL,
    FOREIGN KEY (manufacturer_id) REFERENCES manufacturers(user_id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(warehouse_id)
);

CREATE TABLE IF NOT EXISTS events (
    event_id VARCHAR(40) PRIMARY KEY,
    shipment_id VARCHAR(40) NOT NULL,
    event_type VARCHAR(60) NOT NULL,
    timestamp_value DATETIME NOT NULL,
    description VARCHAR(255) NOT NULL,
    details_json TEXT,
    FOREIGN KEY (shipment_id) REFERENCES shipments(shipment_id)
);

CREATE TABLE IF NOT EXISTS delivery_evidence (
    evidence_id VARCHAR(40) PRIMARY KEY,
    event_id VARCHAR(40) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    timestamp_value DATETIME NOT NULL,
    verification_status VARCHAR(40) NOT NULL,
    FOREIGN KEY (event_id) REFERENCES events(event_id)
);
