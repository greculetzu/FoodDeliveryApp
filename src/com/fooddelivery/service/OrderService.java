package com.fooddelivery.service;

import com.fooddelivery.audit.AuditService;
import com.fooddelivery.model.Address;
import com.fooddelivery.model.Courier;
import com.fooddelivery.model.Customer;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderItem;
import com.fooddelivery.model.OrderStatus;
import com.fooddelivery.model.Restaurant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderService {
    // HashMap<orderId, Order> pentru acces rapid dupa ID
    private Map<String, Order> orders = new HashMap<>();

    public Order placeOrder(Customer customer, Restaurant restaurant,
                            List<OrderItem> items, Address deliveryAddress) {
        AuditService.getInstance().log("PLACE_ORDER");
        String orderId = UUID.randomUUID().toString();
        Order order = new Order(orderId, customer.getUserId(), restaurant.getRestaurantId(),
                items, deliveryAddress);
        orders.put(orderId, order);
        customer.addOrderId(orderId);
        System.out.println("[OrderService] Comanda plasata: " + order);
        return order;
    }

    public void assignCourier(String orderId, Courier courier) {
        AuditService.getInstance().log("ASSIGN_COURIER");
        Order order = orders.get(orderId);
        if (order != null) {
            order.setCourierId(courier.getUserId());
            courier.setAvailable(false);
            System.out.println("[OrderService] Curier " + courier.getName()
                    + " asignat la comanda " + orderId.substring(0, 8) + "...");
        }
    }

    public void updateOrderStatus(String orderId, OrderStatus newStatus) {
        AuditService.getInstance().log("UPDATE_ORDER_STATUS");
        Order order = orders.get(orderId);
        if (order != null) {
            order.setStatus(newStatus);
            System.out.println("[OrderService] Status actualizat -> " + newStatus
                    + " pentru comanda " + orderId.substring(0, 8) + "...");
        }
    }

    public List<Order> getOrderHistory(String customerId) {
        AuditService.getInstance().log("VIEW_ORDER_HISTORY");
        return orders.values().stream()
                .filter(o -> o.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public double calculateTotal(String orderId) {
        AuditService.getInstance().log("CALCULATE_TOTAL");
        Order order = orders.get(orderId);
        return order != null ? order.calculateTotal() : 0.0;
    }

    public Order getOrderById(String orderId) {
        return orders.get(orderId);
    }
}
