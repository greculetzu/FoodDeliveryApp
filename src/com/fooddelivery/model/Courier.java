package com.fooddelivery.model;

public class Courier extends User {
    private String vehicleType;
    private boolean available;

    public Courier(String userId, String name, String email, String phone, String vehicleType) {
        super(userId, name, email, phone);
        this.vehicleType = vehicleType;
        this.available = true;
    }

    public String getVehicleType() { return vehicleType; }
    public boolean isAvailable() { return available; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String getRole() { return "Courier"; }

    @Override
    public String toString() {
        return super.toString() + ", vehicle=" + vehicleType + ", available=" + available;
    }
}
