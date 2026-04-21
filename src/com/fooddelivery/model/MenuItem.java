package com.fooddelivery.model;

public abstract class MenuItem {
    private String itemId;
    private String name;
    private double price;
    private String description;

    public MenuItem(String itemId, String name, double price, String description) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public String getItemId() { return itemId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }

    public abstract String getType();

    @Override
    public String toString() {
        return getType() + "{name='" + name + "', price=" + price + " RON, desc='" + description + "'}";
    }
}
