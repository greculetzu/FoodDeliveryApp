package com.fooddelivery.service;

import com.fooddelivery.audit.AuditService;
import com.fooddelivery.model.Category;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.model.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class RestaurantService {
    // TreeSet sortat dupa rating (descendent) prin Comparable din Restaurant
    private TreeSet<Restaurant> restaurants = new TreeSet<>();

    public void addRestaurant(Restaurant restaurant) {
        AuditService.getInstance().log("REGISTER_RESTAURANT");
        restaurants.add(restaurant);
        System.out.println("[RestaurantService] Restaurant adaugat: " + restaurant.getName());
    }

    public TreeSet<Restaurant> getRestaurantsSortedByRating() {
        AuditService.getInstance().log("VIEW_RESTAURANTS_BY_RATING");
        return restaurants;
    }

    public List<Restaurant> getRestaurantsByCategory(Category category) {
        AuditService.getInstance().log("SEARCH_BY_CATEGORY");
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

    public List<MenuItem> getMenu(String restaurantId) {
        AuditService.getInstance().log("VIEW_MENU");
        Restaurant r = getRestaurantById(restaurantId);
        return r != null ? r.getMenu() : new ArrayList<>();
    }

    public void addMenuItemToRestaurant(String restaurantId, MenuItem item) {
        AuditService.getInstance().log("ADD_MENU_ITEM");
        Restaurant r = getRestaurantById(restaurantId);
        if (r != null) {
            r.addMenuItem(item);
            System.out.println("[RestaurantService] Produs adaugat in meniu: " + item.getName()
                    + " -> " + r.getName());
        }
    }

    public void loadRestaurant(Restaurant restaurant) {
        if (getRestaurantById(restaurant.getRestaurantId()) == null) restaurants.add(restaurant);
    }

    public void addReview(String restaurantId, Review review) {
        AuditService.getInstance().log("ADD_REVIEW");
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
