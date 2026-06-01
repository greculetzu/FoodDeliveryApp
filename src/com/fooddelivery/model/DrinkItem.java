package com.fooddelivery.model;

public class DrinkItem extends MenuItem {
    private boolean alcoholic;
    private int volumeMl;

    public DrinkItem(String itemId, String name, double price, String description, boolean alcoholic, int volumeMl) {
        super(itemId, name, price, description);
        this.alcoholic = alcoholic;
        this.volumeMl = volumeMl;
    }

    public boolean isAlcoholic() { return alcoholic; }
    public int getVolumeMl() { return volumeMl; }
    public void setAlcoholic(boolean alcoholic) { this.alcoholic = alcoholic; }
    public void setVolumeMl(int volumeMl) { this.volumeMl = volumeMl; }

    @Override
    public String getType() { return "DrinkItem"; }

    @Override
    public String toString() {
        return super.toString() + ", volume=" + volumeMl + "ml, alcoholic=" + alcoholic;
    }
}
