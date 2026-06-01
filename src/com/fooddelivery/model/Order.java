package com.fooddelivery.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Order {
    private String orderId;
    private String customerId;
    private String restaurantId;
    private String courierId;
    private List<OrderItem> items;
    private Address deliveryAddress;
    private OrderStatus status;
    private LocalDateTime placedAt;

    public Order(String orderId, String customerId, String restaurantId,
                 List<OrderItem> items, Address deliveryAddress) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.items = items;
        this.deliveryAddress = deliveryAddress;
        this.status = OrderStatus.PLACED;
        this.placedAt = LocalDateTime.now();
    }

    public Order(String orderId, String customerId, String restaurantId, String courierId,
                 List<OrderItem> items, Address deliveryAddress,
                 OrderStatus status, LocalDateTime placedAt) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.courierId = courierId;
        this.items = items;
        this.deliveryAddress = deliveryAddress;
        this.status = status;
        this.placedAt = placedAt;
    }

    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public String getRestaurantId() { return restaurantId; }
    public String getCourierId() { return courierId; }
    public List<OrderItem> getItems() { return items; }
    public Address getDeliveryAddress() { return deliveryAddress; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getPlacedAt() { return placedAt; }

    public void setCourierId(String courierId) { this.courierId = courierId; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setDeliveryAddress(Address deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public double calculateTotal() {
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "Order{id='" + orderId.substring(0, 8) + "...', status=" + status
                + ", total=" + String.format("%.2f", calculateTotal()) + " RON"
                + ", placedAt=" + placedAt.format(fmt) + "}";
    }
}
