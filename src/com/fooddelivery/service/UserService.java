package com.fooddelivery.service;

import com.fooddelivery.model.Courier;
import com.fooddelivery.model.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserService {
    private List<Customer> customers = new ArrayList<>();
    private List<Courier> couriers = new ArrayList<>();

    public void addCustomer(Customer customer) {
        customers.add(customer);
        System.out.println("[UserService] Client inregistrat: " + customer.getName());
    }

    public void addCourier(Courier courier) {
        couriers.add(courier);
        System.out.println("[UserService] Curier inregistrat: " + courier.getName());
    }

    public Customer getCustomerById(String id) {
        return customers.stream()
                .filter(c -> c.getUserId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Courier getCourierById(String id) {
        return couriers.stream()
                .filter(c -> c.getUserId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Courier> getAvailableCouriers() {
        return couriers.stream()
                .filter(Courier::isAvailable)
                .collect(Collectors.toList());
    }

    public List<Customer> getAllCustomers() { return customers; }
    public List<Courier> getAllCouriers() { return couriers; }
}
