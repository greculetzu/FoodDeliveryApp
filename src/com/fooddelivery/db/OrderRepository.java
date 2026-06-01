package com.fooddelivery.db;

import com.fooddelivery.model.Address;
import com.fooddelivery.model.DrinkItem;
import com.fooddelivery.model.FoodItem;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderItem;
import com.fooddelivery.model.OrderStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderRepository extends GenericRepository<Order> {

    private static OrderRepository instance;

    private OrderRepository() { super(); }

    public static OrderRepository getInstance() {
        if (instance == null) instance = new OrderRepository();
        return instance;
    }

    // mapRow maps only the order header row (items loaded separately via loadItems).
    @Override
    protected Order mapRow(ResultSet rs) throws SQLException {
        String street   = rs.getString("street");
        String city     = rs.getString("city");
        String zipCode  = rs.getString("postal_code");
        Address addr    = (street != null) ? new Address(street, city, zipCode) : null;

        return new Order(
                rs.getString("id"),
                rs.getString("customer_id"),
                rs.getString("restaurant_id"),
                rs.getString("courier_id"),
                new ArrayList<>(),
                addr,
                OrderStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("placed_at").toLocalDateTime()
        );
    }

    public void create(Order o) {
        String addrId = saveAddress(o.getDeliveryAddress());
        executeUpdate(
                "INSERT INTO orders (id, customer_id, restaurant_id, courier_id, delivery_address_id, status, placed_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING",
                o.getOrderId(), o.getCustomerId(), o.getRestaurantId(), o.getCourierId(),
                addrId, o.getStatus().name(), Timestamp.valueOf(o.getPlacedAt())
        );
        for (OrderItem item : o.getItems()) {
            executeUpdate(
                    "INSERT INTO order_items (id, order_id, menu_item_id, quantity) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING",
                    UUID.randomUUID().toString(),
                    o.getOrderId(),
                    item.getMenuItem().getItemId(),
                    item.getQuantity()
            );
        }
    }

    public List<Order> findAll() {
        String sql = "SELECT o.id, o.customer_id, o.restaurant_id, o.courier_id, " +
                     "o.status, o.placed_at, a.street, a.city, a.postal_code " +
                     "FROM orders o LEFT JOIN addresses a ON o.delivery_address_id = a.id";
        List<Order> list = new ArrayList<>();
        try (ResultSet rs = executeQuery(sql)) {
            while (rs != null && rs.next()) {
                Order order = mapRow(rs);
                order.getItems().addAll(loadItems(order.getOrderId()));
                list.add(order);
            }
        } catch (SQLException e) {
            System.err.println("[OrderRepository] findAll failed: " + e.getMessage());
        }
        return list;
    }

    public Optional<Order> findById(String id) {
        String sql = "SELECT o.id, o.customer_id, o.restaurant_id, o.courier_id, " +
                     "o.status, o.placed_at, a.street, a.city, a.postal_code " +
                     "FROM orders o LEFT JOIN addresses a ON o.delivery_address_id = a.id " +
                     "WHERE o.id = ?";
        try (ResultSet rs = executeQuery(sql, id)) {
            if (rs != null && rs.next()) {
                Order order = mapRow(rs);
                order.getItems().addAll(loadItems(id));
                return Optional.of(order);
            }
        } catch (SQLException e) {
            System.err.println("[OrderRepository] findById failed: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Order> findByCustomerId(String customerId) {
        String sql = "SELECT o.id, o.customer_id, o.restaurant_id, o.courier_id, " +
                     "o.status, o.placed_at, a.street, a.city, a.postal_code " +
                     "FROM orders o LEFT JOIN addresses a ON o.delivery_address_id = a.id " +
                     "WHERE o.customer_id = ?";
        List<Order> list = new ArrayList<>();
        try (ResultSet rs = executeQuery(sql, customerId)) {
            while (rs != null && rs.next()) {
                Order order = mapRow(rs);
                order.getItems().addAll(loadItems(order.getOrderId()));
                list.add(order);
            }
        } catch (SQLException e) {
            System.err.println("[OrderRepository] findByCustomerId failed: " + e.getMessage());
        }
        return list;
    }

    public void update(Order o) {
        executeUpdate("UPDATE orders SET status = ?, courier_id = ? WHERE id = ?",
                o.getStatus().name(), o.getCourierId(), o.getOrderId());
    }

    public void delete(String id) {
        executeUpdate("DELETE FROM orders WHERE id = ?", id);
    }

    private List<OrderItem> loadItems(String orderId) {
        String sql = "SELECT oi.quantity, mi.id, mi.name, mi.price, mi.description, " +
                     "mi.type, mi.calories, mi.is_vegetarian, mi.volume_ml, mi.is_alcoholic " +
                     "FROM order_items oi JOIN menu_items mi ON oi.menu_item_id = mi.id " +
                     "WHERE oi.order_id = ?";
        List<OrderItem> items = new ArrayList<>();
        try (ResultSet rs = executeQuery(sql, orderId)) {
            while (rs != null && rs.next()) {
                MenuItem menuItem = buildMenuItem(rs);
                items.add(new OrderItem(menuItem, rs.getInt("quantity")));
            }
        } catch (SQLException e) {
            System.err.println("[OrderRepository] loadItems failed: " + e.getMessage());
        }
        return items;
    }

    private MenuItem buildMenuItem(ResultSet rs) throws SQLException {
        String id          = rs.getString("id");
        String name        = rs.getString("name");
        double price       = rs.getDouble("price");
        String description = rs.getString("description");
        String type        = rs.getString("type");

        if ("FoodItem".equals(type)) {
            int calories  = rs.getObject("calories") != null ? rs.getInt("calories") : 0;
            boolean veg   = rs.getObject("is_vegetarian") != null && rs.getBoolean("is_vegetarian");
            return new FoodItem(id, name, price, description, calories, veg);
        } else {
            int volume       = rs.getObject("volume_ml") != null ? rs.getInt("volume_ml") : 0;
            boolean alcoholic = rs.getObject("is_alcoholic") != null && rs.getBoolean("is_alcoholic");
            return new DrinkItem(id, name, price, description, alcoholic, volume);
        }
    }

    private String saveAddress(Address a) {
        String addrId = UUID.randomUUID().toString();
        executeUpdate("INSERT INTO addresses (id, street, city, postal_code) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING",
                addrId, a.getStreet(), a.getCity(), a.getZipCode());
        return addrId;
    }
}
