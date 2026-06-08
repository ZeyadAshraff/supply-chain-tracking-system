package core;

import control.AdminControl;
import control.AuthenticationControl;
import control.EventControl;
import control.GoodControl;
import control.InventoryControl;
import control.ShipmentControl;
import control.VehicleControl;
import database.DBConnection;
import database.EventRepository;
import database.GoodRepository;
import database.ItemRepository;
import database.InventoryRepository;
import database.ShipmentRepository;
import database.UserRepository;
import database.VehicleRepository;
import database.WarehouseRepository;

public class AppContext {
    public final UserRepository userRepository;
    public final ShipmentRepository shipmentRepository;
    public final EventRepository eventRepository;
    public final GoodRepository goodRepository;
    public final ItemRepository itemRepository;
    public final InventoryRepository inventoryRepository;
    public final VehicleRepository vehicleRepository;
    public final WarehouseRepository warehouseRepository;

    public final AuthenticationControl authenticationControl;
    public final ShipmentControl shipmentControl;
    public final EventControl eventControl;
    public final GoodControl goodControl;
    public final VehicleControl vehicleControl;
    public final InventoryControl inventoryControl;
    public final AdminControl adminControl;

    public AppContext() {
        DBConnection db = DBConnection.getInstance();
        this.userRepository = new UserRepository(db);
        this.shipmentRepository = new ShipmentRepository(db);
        this.eventRepository = new EventRepository(db);
        this.goodRepository = new GoodRepository(db);
        this.itemRepository = new ItemRepository(db);
        this.inventoryRepository = new InventoryRepository(db);
        this.vehicleRepository = new VehicleRepository(db);
        this.warehouseRepository = new WarehouseRepository(db);
        this.shipmentRepository.ensureShipmentSchema();

        this.authenticationControl = new AuthenticationControl(userRepository);
        this.shipmentControl = new ShipmentControl(shipmentRepository, eventRepository, warehouseRepository, userRepository);
        this.eventControl = new EventControl(shipmentControl);
        this.goodControl = new GoodControl(goodRepository, userRepository, inventoryRepository, warehouseRepository);
        this.vehicleControl = new VehicleControl(vehicleRepository);
        this.inventoryControl = new InventoryControl(inventoryRepository, warehouseRepository, goodRepository, userRepository);
        this.adminControl = new AdminControl(userRepository, shipmentRepository, eventRepository);
    }
}
