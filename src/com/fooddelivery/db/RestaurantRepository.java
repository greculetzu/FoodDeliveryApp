package com.fooddelivery.db;

import com.fooddelivery.model.Address;
import com.fooddelivery.model.Category;
import com.fooddelivery.model.Restaurant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RestaurantRepository extends GenericRepository<Restaurant> {

    private static RestaurantRepository instance;

    private RestaurantRepository() { super(); }

    public static RestaurantRepository getInstance() {
        if (instance == null) instance = new RestaurantRepository();
        return instance;
    }

    @Override
    protected Restaurant mapRow(ResultSet rs) throws SQLException {
        String street   = rs.getString("street");
        String city     = rs.getString("city");
        String zipCode  = rs.getString("postal_code");
        Address address = (street != null) ? new Address(street, city, zipCode) : null;
        Restaurant r = new Restaurant(
                rs.getString("id"),
                rs.getString("name"),
                Category.valueOf(rs.getString("category")),
                address
        );
        r.setRating(rs.getDouble("rating"));
        return r;
    }

    public void create(Restaurant r) {
        String addrId = saveAddress(r.getAddress());
        executeUpdate(
                "INSERT INTO restaurants (id, name, category, rating, address_id) VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING",
                r.getRestaurantId(), r.getName(), r.getCategory().name(), r.getRating(), addrId
        );
    }

    public Optional<Restaurant> findById(String id) {
        String sql = "SELECT r.id, r.name, r.category, r.rating, " +
                     "a.street, a.city, a.postal_code " +
                     "FROM restaurants r LEFT JOIN addresses a ON r.address_id = a.id " +
                     "WHERE r.id = ?";
        try (ResultSet rs = executeQuery(sql, id)) {
            if (rs != null && rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[RestaurantRepository] findById failed: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Restaurant> findAll() {
        String sql = "SELECT r.id, r.name, r.category, r.rating, " +
                     "a.street, a.city, a.postal_code " +
                     "FROM restaurants r LEFT JOIN addresses a ON r.address_id = a.id";
        List<Restaurant> list = new ArrayList<>();
        try (ResultSet rs = executeQuery(sql)) {
            while (rs != null && rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[RestaurantRepository] findAll failed: " + e.getMessage());
        }
        return list;
    }

    public void update(Restaurant r) {
        executeUpdate(
                "UPDATE restaurants SET name = ?, category = ?, rating = ? WHERE id = ?",
                r.getName(), r.getCategory().name(), r.getRating(), r.getRestaurantId()
        );
    }

    public void delete(String id) {
        executeUpdate("DELETE FROM restaurants WHERE id = ?", id);
    }

    private String saveAddress(Address a) {
        String addrId = UUID.randomUUID().toString();
        executeUpdate("INSERT INTO addresses (id, street, city, postal_code) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING",
                addrId, a.getStreet(), a.getCity(), a.getZipCode());
        return addrId;
    }
}
