package com.fooddelivery.model;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private Address defaultAddress;
    private List<String> orderIds;

    public Customer(String userId, String name, String email, String phone, Address defaultAddress) {
        super(userId, name, email, phone);
        this.defaultAddress = defaultAddress;
        this.orderIds = new ArrayList<>();
    }

    public Address getDefaultAddress() { return defaultAddress; }
    public List<String> getOrderIds() { return orderIds; }

    public void addOrderId(String orderId) { orderIds.add(orderId); }

    @Override
    public String getRole() { return "Customer"; }
}
