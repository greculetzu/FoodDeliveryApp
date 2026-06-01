package com.fooddelivery.db;

import com.fooddelivery.model.DrinkItem;
import com.fooddelivery.model.FoodItem;
import com.fooddelivery.model.MenuItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuItemRepository extends GenericRepository<MenuItem> {

    private static MenuItemRepository instance;

    private MenuItemRepository() { super(); }

    public static MenuItemRepository getInstance() {
        if (instance == null) instance = new MenuItemRepository();
        return instance;
    }

    @Override
    protected MenuItem mapRow(ResultSet rs) throws SQLException {
        String id          = rs.getString("id");
        String name        = rs.getString("name");
        double price       = rs.getDouble("price");
        String description = rs.getString("description");
        String type        = rs.getString("type");

        if ("FoodItem".equals(type)) {
            int calories     = rs.getObject("calories") != null ? rs.getInt("calories") : 0;
            boolean veg      = rs.getObject("is_vegetarian") != null && rs.getBoolean("is_vegetarian");
            return new FoodItem(id, name, price, description, calories, veg);
        } else {
            int volume       = rs.getObject("volume_ml") != null ? rs.getInt("volume_ml") : 0;
            boolean alcoholic = rs.getObject("is_alcoholic") != null && rs.getBoolean("is_alcoholic");
            return new DrinkItem(id, name, price, description, alcoholic, volume);
        }
    }

    public void create(MenuItem item, String restaurantId) {
        Object calories    = null;
        Object isVeg       = null;
        Object volumeMl    = null;
        Object isAlcoholic = null;

        if (item instanceof FoodItem fi) {
            calories = fi.getCalories();
            isVeg    = fi.isVegetarian();
        } else if (item instanceof DrinkItem di) {
            volumeMl    = di.getVolumeMl();
            isAlcoholic = di.isAlcoholic();
        }

        executeUpdate(
                "INSERT INTO menu_items " +
                "(id, restaurant_id, name, price, description, type, calories, is_vegetarian, volume_ml, is_alcoholic) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING",
                item.getItemId(), restaurantId, item.getName(), item.getPrice(),
                item.getDescription(), item.getType(),
                calories, isVeg, volumeMl, isAlcoholic
        );
    }

    public List<MenuItem> findByRestaurantId(String restaurantId) {
        String sql = "SELECT id, name, price, description, type, calories, is_vegetarian, volume_ml, is_alcoholic " +
                     "FROM menu_items WHERE restaurant_id = ?";
        List<MenuItem> list = new ArrayList<>();
        try (ResultSet rs = executeQuery(sql, restaurantId)) {
            while (rs != null && rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[MenuItemRepository] findByRestaurantId failed: " + e.getMessage());
        }
        return list;
    }

    public void update(MenuItem item) {
        Object calories    = null;
        Object isVeg       = null;
        Object volumeMl    = null;
        Object isAlcoholic = null;

        if (item instanceof FoodItem fi) {
            calories = fi.getCalories();
            isVeg    = fi.isVegetarian();
        } else if (item instanceof DrinkItem di) {
            volumeMl    = di.getVolumeMl();
            isAlcoholic = di.isAlcoholic();
        }

        executeUpdate(
                "UPDATE menu_items SET name = ?, price = ?, description = ?, " +
                "calories = ?, is_vegetarian = ?, volume_ml = ?, is_alcoholic = ? WHERE id = ?",
                item.getName(), item.getPrice(), item.getDescription(),
                calories, isVeg, volumeMl, isAlcoholic,
                item.getItemId()
        );
    }

    public void delete(String id) {
        executeUpdate("DELETE FROM order_items WHERE menu_item_id = ?", id);
        executeUpdate("DELETE FROM menu_items WHERE id = ?", id);
    }
}
