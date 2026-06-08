package control;

import entity.Shipper;
import entity.User;
import entity.Vehicle;
import database.VehicleRepository;
import java.util.List;

public class VehicleControl {
    private final VehicleRepository vehicleRepository;

    public VehicleControl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public void addVehicle(User actor, Shipper shipper, Vehicle vehicle) {
        shipper.assertOwnedBy(actor);
        vehicleRepository.addVehicle(shipper.getUserId(), vehicle);
    }

    public void removeVehicle(User actor, Shipper shipper, Vehicle vehicle) {
        shipper.assertOwnedBy(actor);
        vehicleRepository.removeVehicle(shipper.getUserId(), vehicle.getVehicleId());
    }

    public void updateVehicleStatus(User actor, String shipperId, Vehicle vehicle, String status) {
        Shipper.assertActorOwnsShipperId(actor, shipperId);
        vehicle.setStatus(status);
        vehicleRepository.updateVehicleStatus(vehicle.getVehicleId(), status);
    }

    public List<Vehicle> retrieveAvailableVehicles(User actor, Shipper shipper) {
        shipper.assertOwnedBy(actor);
        return vehicleRepository.findAvailableVehicles(shipper.getUserId());
    }

    public List<Vehicle> retrieveAllVehicles(User actor, String shipperId) {
        Shipper.assertActorOwnsShipperId(actor, shipperId);
        return vehicleRepository.findVehiclesByShipper(shipperId);
    }
}
