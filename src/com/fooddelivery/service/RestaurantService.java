package com.fooddelivery.service;

import com.fooddelivery.model.Category;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.model.Review;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class RestaurantService {
    // TreeSet sortat dupa rating (descendent) prin Comparable din Restaurant
    private TreeSet<Restaurant> restaurants = new TreeSet<>();

    public void addRestaurant(Restaurant restaurant) {
        restaurants.add(restaurant);
        System.out.println("[RestaurantService] Restaurant adaugat: " + restaurant.getName());
    }

    public TreeSet<Restaurant> getRestaurantsSortedByRating() {
        return restaurants;
    }

    public List<Restaurant> getRestaurantsByCategory(Category category) {
        return restaurants.stream()
                .filter(r -> r.getCategory() == category)
                .collect(Collectors.toList());
    }

    public Restaurant getRestaurantById(String id) {
        return restaurants.stream()
                .filter(r -> r.getRestaurantId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void addMenuItemToRestaurant(String restaurantId, MenuItem item) {
        Restaurant r = getRestaurantById(restaurantId);
        if (r != null) {
            r.addMenuItem(item);
            System.out.println("[RestaurantService] Produs adaugat in meniu: " + item.getName()
                    + " -> " + r.getName());
        }
    }

    public void addReview(String restaurantId, Review review) {
        Restaurant r = getRestaurantById(restaurantId);
        if (r != null) {
            // Scoatem din TreeSet, actualizam ratingul, reinserăm (ordinea se recalculeaza)
            restaurants.remove(r);
            r.addReview(review);
            r.updateRating();
            restaurants.add(r);
            System.out.println("[RestaurantService] Recenzie adaugata la " + r.getName()
                    + " | Rating nou: " + String.format("%.1f", r.getRating()));
        }
    }
}
