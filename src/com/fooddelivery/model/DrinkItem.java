package com.fooddelivery.model;

public class DrinkItem extends MenuItem {
    private boolean alcoholic;
    private double volumeMl;

    public DrinkItem(String itemId, String name, double price, String description, boolean alcoholic, double volumeMl) {
        super(itemId, name, price, description);
        this.alcoholic = alcoholic;
        this.volumeMl = volumeMl;
    }

    public boolean isAlcoholic() { return alcoholic; }
    public double getVolumeMl() { return volumeMl; }

    @Override
    public String getType() { return "DrinkItem"; }

    @Override
    public String toString() {
        return super.toString() + ", volume=" + volumeMl + "ml, alcoholic=" + alcoholic;
    }
}
