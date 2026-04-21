package com.fooddelivery.model;

public class FoodItem extends MenuItem {
    private int calories;
    private boolean vegetarian;

    public FoodItem(String itemId, String name, double price, String description, int calories, boolean vegetarian) {
        super(itemId, name, price, description);
        this.calories = calories;
        this.vegetarian = vegetarian;
    }

    public int getCalories() { return calories; }
    public boolean isVegetarian() { return vegetarian; }

    @Override
    public String getType() { return "FoodItem"; }

    @Override
    public String toString() {
        return super.toString() + ", calories=" + calories + ", vegetarian=" + vegetarian;
    }
}
