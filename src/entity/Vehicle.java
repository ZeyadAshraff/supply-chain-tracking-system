package entity;

public class Vehicle {
    private final String vehicleId;
    private String vehicleType;
    private String licensePlate;
    private int capacity;
    private String status;

    public Vehicle(String vehicleId, String vehicleType, String licensePlate, int capacity, String status) {
        this.vehicleId = vehicleId;
        this.vehicleType = vehicleType;
        this.licensePlate = licensePlate;
        this.capacity = capacity;
        this.status = status;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Vehicle status is required.");
        }
        this.status = status;
    }
}
