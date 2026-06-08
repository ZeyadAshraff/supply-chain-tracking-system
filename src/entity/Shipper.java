package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Shipper extends User {
    private String companyName;
    private String contactNumber;
    private boolean available;
    private final List<Vehicle> vehicles;

    public Shipper(String userId, String name, String email, String password, boolean active,
                   String companyName, String contactNumber, boolean available) {
        super(userId, name, email, password, active);
        this.companyName = companyName;
        this.contactNumber = contactNumber;
        this.available = available;
        this.vehicles = new ArrayList<>();
    }

    @Override
    public Role getRole() {
        return Role.SHIPPER;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        vehicles.remove(vehicle);
    }

    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> availableVehicles = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            if ("AVAILABLE".equalsIgnoreCase(vehicle.getStatus())) {
                availableVehicles.add(vehicle);
            }
        }
        return availableVehicles;
    }

    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    public void assertOwnedBy(User actor) {
        if (actor.getRole() == Role.SHIPPER && actor.getUserId().equalsIgnoreCase(getUserId())) {
            return;
        }
        throw new IllegalArgumentException("You are not allowed to manage vehicles for this shipper.");
    }

    public static void assertActorOwnsShipperId(User actor, String shipperId) {
        if (actor.getRole() == Role.SHIPPER && actor.getUserId().equalsIgnoreCase(shipperId)) {
            return;
        }
        throw new IllegalArgumentException("You are not allowed to manage vehicles for this shipper.");
    }
}
