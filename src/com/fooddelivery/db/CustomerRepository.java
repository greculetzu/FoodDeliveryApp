package com.fooddelivery.db;

import com.fooddelivery.model.Address;
import com.fooddelivery.model.Courier;
import com.fooddelivery.model.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CustomerRepository extends GenericRepository<Customer> {

    private static CustomerRepository instance;

    private CustomerRepository() { super(); }

    public static CustomerRepository getInstance() {
        if (instance == null) instance = new CustomerRepository();
        return instance;
    }

    @Override
    protected Customer mapRow(ResultSet rs) throws SQLException {
        String street   = rs.getString("street");
        String city     = rs.getString("city");
        String zipCode  = rs.getString("postal_code");
        Address address = (street != null) ? new Address(street, city, zipCode) : null;
        return new Customer(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                address
        );
    }

    public void createCourier(Courier c) {
        executeUpdate(
                "INSERT INTO users (id, name, email, phone, role, vehicle_type, is_available) VALUES (?, ?, ?, ?, 'COURIER', ?, ?) ON CONFLICT DO NOTHING",
                c.getUserId(), c.getName(), c.getEmail(), c.getPhone(),
                c.getVehicleType(), c.isAvailable()
        );
    }

    public void create(Customer c) {
        String addrId = saveAddress(c.getDefaultAddress());
        executeUpdate(
                "INSERT INTO users (id, name, email, phone, role, default_address_id) VALUES (?, ?, ?, ?, 'CUSTOMER', ?) ON CONFLICT DO NOTHING",
                c.getUserId(), c.getName(), c.getEmail(), c.getPhone(), addrId
        );
    }

    public Optional<Customer> findById(String id) {
        String sql = "SELECT u.id, u.name, u.email, u.phone, " +
                     "a.street, a.city, a.postal_code " +
                     "FROM users u LEFT JOIN addresses a ON u.default_address_id = a.id " +
                     "WHERE u.id = ? AND u.role = 'CUSTOMER'";
        try (ResultSet rs = executeQuery(sql, id)) {
            if (rs != null && rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[CustomerRepository] findById failed: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Customer> findAll() {
        String sql = "SELECT u.id, u.name, u.email, u.phone, " +
                     "a.street, a.city, a.postal_code " +
                     "FROM users u LEFT JOIN addresses a ON u.default_address_id = a.id " +
                     "WHERE u.role = 'CUSTOMER'";
        List<Customer> list = new ArrayList<>();
        try (ResultSet rs = executeQuery(sql)) {
            while (rs != null && rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[CustomerRepository] findAll failed: " + e.getMessage());
        }
        return list;
    }

    public void update(Customer c) {
        String addrId = null;
        try (ResultSet rs = executeQuery("SELECT default_address_id FROM users WHERE id = ?", c.getUserId())) {
            if (rs != null && rs.next()) addrId = rs.getString(1);
        } catch (SQLException e) {
            System.err.println("[CustomerRepository] update (fetch addrId) failed: " + e.getMessage());
        }
        if (addrId != null && c.getDefaultAddress() != null) {
            executeUpdate("UPDATE addresses SET street = ?, city = ?, postal_code = ? WHERE id = ?",
                    c.getDefaultAddress().getStreet(),
                    c.getDefaultAddress().getCity(),
                    c.getDefaultAddress().getZipCode(),
                    addrId);
        }
        executeUpdate("UPDATE users SET name = ?, email = ?, phone = ? WHERE id = ?",
                c.getName(), c.getEmail(), c.getPhone(), c.getUserId());
    }

    public void delete(String id) {
        executeUpdate("DELETE FROM users WHERE id = ?", id);
    }

    private String saveAddress(Address a) {
        String addrId = UUID.randomUUID().toString();
        executeUpdate("INSERT INTO addresses (id, street, city, postal_code) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING",
                addrId, a.getStreet(), a.getCity(), a.getZipCode());
        return addrId;
    }
}
