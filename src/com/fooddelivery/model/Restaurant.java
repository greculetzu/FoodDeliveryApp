package com.fooddelivery.model;

import java.util.ArrayList;
import java.util.List;

public class Restaurant implements Comparable<Restaurant> {
    private String restaurantId;
    private String name;
    private Category category;
    private double rating;
    private Address address;
    private List<MenuItem> menu;
    private List<Review> reviews;

    public Restaurant(String restaurantId, String name, Category category, Address address) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.category = category;
        this.address = address;
        this.rating = 0.0;
        this.menu = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    public String getRestaurantId() { return restaurantId; }
    public String getName() { return name; }
    public Category getCategory() { return category; }
    public double getRating() { return rating; }
    public Address getAddress() { return address; }
    public List<MenuItem> getMenu() { return menu; }
    public List<Review> getReviews() { return reviews; }

    public void addMenuItem(MenuItem item) { menu.add(item); }

    public void addReview(Review review) { reviews.add(review); }

    public void updateRating() {
        if (reviews.isEmpty()) { rating = 0.0; return; }
        rating = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }

    @Override
    public int compareTo(Restaurant other) {
        int cmp = Double.compare(other.rating, this.rating);
        if (cmp != 0) return cmp;
        return this.restaurantId.compareTo(other.restaurantId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Restaurant)) return false;
        return restaurantId.equals(((Restaurant) o).restaurantId);
    }

    @Override
    public int hashCode() { return restaurantId.hashCode(); }

    @Override
    public String toString() {
        return "Restaurant{name='" + name + "', category=" + category + ", rating=" + String.format("%.1f", rating) + "}";
    }
}
