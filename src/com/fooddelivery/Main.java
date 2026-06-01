package com.fooddelivery;

import com.fooddelivery.db.CustomerRepository;
import com.fooddelivery.db.MenuItemRepository;
import com.fooddelivery.db.OrderRepository;
import com.fooddelivery.db.RestaurantRepository;
import com.fooddelivery.model.*;
import com.fooddelivery.service.OrderService;
import com.fooddelivery.service.RestaurantService;
import com.fooddelivery.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        UserService userService = new UserService();
        RestaurantService restaurantService = new RestaurantService();
        OrderService orderService = new OrderService();

        separator("1. INREGISTRARE CLIENTI");
        Customer ana = new Customer("C001", "Ana Popescu", "ana@mail.com", "0722111222",
                new Address("Str. Florilor 5", "Bucuresti", "010101"));
        Customer mihai = new Customer("C002", "Mihai Ionescu", "mihai@mail.com", "0733222333",
                new Address("Bd. Unirii 20", "Cluj-Napoca", "400001"));
        userService.addCustomer(ana);
        userService.addCustomer(mihai);

        separator("2. INREGISTRARE CURIER");
        Courier bogdan = new Courier("CR001", "Bogdan Rusu", "bogdan@mail.com", "0744333444", "Bicicleta");
        Courier elena = new Courier("CR002", "Elena Vasile", "elena@mail.com", "0755444555", "Motocicleta");
        userService.addCourier(bogdan);
        userService.addCourier(elena);

        separator("3. INREGISTRARE RESTAURANTE");
        Restaurant pizzaLoco = new Restaurant("R001", "Pizza Loco", Category.PIZZA,
                new Address("Calea Victoriei 10", "Bucuresti", "010201"));
        Restaurant burgerKing = new Restaurant("R002", "Burger Palace", Category.BURGER,
                new Address("Str. Republicii 3", "Bucuresti", "020202"));
        Restaurant sushiZen = new Restaurant("R003", "Sushi Zen", Category.SUSHI,
                new Address("Bd. Magheru 15", "Bucuresti", "030303"));
        restaurantService.addRestaurant(pizzaLoco);
        restaurantService.addRestaurant(burgerKing);
        restaurantService.addRestaurant(sushiZen);

        separator("4. ADAUGARE PRODUSE IN MENIU");
        FoodItem margherita = new FoodItem("P001", "Pizza Margherita", 32.0, "Sos rosii, mozzarella", 750, true);
        FoodItem pepperoni = new FoodItem("P002", "Pizza Pepperoni", 38.0, "Sos rosii, pepperoni", 900, false);
        DrinkItem cola = new DrinkItem("D001", "Coca-Cola 500ml", 8.0, "Bautura racoritoare", false, 500);
        DrinkItem bere = new DrinkItem("D002", "Bere Ursus", 10.0, "Bere blonda", true, 330);

        restaurantService.addMenuItemToRestaurant("R001", margherita);
        restaurantService.addMenuItemToRestaurant("R001", pepperoni);
        restaurantService.addMenuItemToRestaurant("R001", cola);
        restaurantService.addMenuItemToRestaurant("R001", bere);

        FoodItem burger = new FoodItem("P003", "Classic Burger", 35.0, "Vita, salata, rosii", 820, false);
        FoodItem vegBurger = new FoodItem("P004", "Veggie Burger", 30.0, "Legume la gratar", 580, true);
        restaurantService.addMenuItemToRestaurant("R002", burger);
        restaurantService.addMenuItemToRestaurant("R002", vegBurger);

        separator("5. VIZUALIZARE MENIU RESTAURANT");
        Restaurant r = restaurantService.getRestaurantById("R001");
        System.out.println("Meniu " + r.getName() + ":");
        for (MenuItem item : restaurantService.getMenu("R001")) {
            System.out.println("  - " + item);
        }

        separator("6. PLASARE COMANDA");
        List<OrderItem> items = Arrays.asList(
                new OrderItem(margherita, 2),
                new OrderItem(cola, 1)
        );
        Order comanda1 = orderService.placeOrder(ana, pizzaLoco, items, ana.getDefaultAddress());

        separator("7. ASIGNARE CURIER");
        System.out.println("Curieri disponibili: " + userService.getAvailableCouriers());
        orderService.assignCourier(comanda1.getOrderId(), bogdan);

        separator("8. ACTUALIZARE STATUS COMANDA");
        orderService.updateOrderStatus(comanda1.getOrderId(), OrderStatus.PREPARING);
        orderService.updateOrderStatus(comanda1.getOrderId(), OrderStatus.ON_THE_WAY);
        orderService.updateOrderStatus(comanda1.getOrderId(), OrderStatus.DELIVERED);

        separator("9. CALCUL TOTAL COMANDA");
        double total = orderService.calculateTotal(comanda1.getOrderId());
        System.out.println("Total comanda: " + String.format("%.2f", total) + " RON");

        separator("10. ADAUGARE RECENZII");
        Review r1 = new Review("REV001", "C001", 5, "Excelent! Pizza a venit calda.");
        Review r2 = new Review("REV002", "C002", 4, "Foarte buna, dar livrarea a intarziat.");
        restaurantService.addReview("R001", r1);
        restaurantService.addReview("R001", r2);

        Review rb1 = new Review("REV003", "C001", 3, "Burger decent, nimic special.");
        restaurantService.addReview("R002", rb1);

        separator("11. VIZUALIZARE RESTAURANTE SORTATE DUPA RATING");
        System.out.println("Restaurante (descrescator dupa rating):");
        for (Restaurant restaurant : restaurantService.getRestaurantsSortedByRating()) {
            System.out.println("  " + restaurant);
        }

        separator("12. CAUTARE RESTAURANTE DUPA CATEGORIE");
        List<Restaurant> pizzerii = restaurantService.getRestaurantsByCategory(Category.PIZZA);
        System.out.println("Restaurante PIZZA: " + pizzerii);

        separator("13. ISTORIC COMENZI CLIENT");
        // A doua comanda pentru ana
        List<OrderItem> items2 = Arrays.asList(new OrderItem(burger, 1), new OrderItem(bere, 2));
        Order comanda2 = orderService.placeOrder(ana, burgerKing, items2, ana.getDefaultAddress());

        List<Order> istoricAna = orderService.getOrderHistory("C001");
        System.out.println("Istoricul comenzilor Ana (" + istoricAna.size() + " comenzi):");
        for (Order o : istoricAna) {
            System.out.println("  " + o);
        }

        stageIIDemo(ana, mihai, bogdan, elena,
                pizzaLoco, burgerKing, sushiZen,
                margherita, pepperoni, cola, bere,
                burger, vegBurger,
                comanda1, comanda2);
    }

    private static void stageIIDemo(
            Customer ana, Customer mihai, Courier bogdan, Courier elena,
            Restaurant pizzaLoco, Restaurant burgerKing, Restaurant sushiZen,
            FoodItem margherita, FoodItem pepperoni, DrinkItem cola, DrinkItem bere,
            FoodItem burger, FoodItem vegBurger,
            Order comanda1, Order comanda2) {

        // ===== STAGE II DEMO =====
        CustomerRepository  customerRepo  = CustomerRepository.getInstance();
        RestaurantRepository restaurantRepo = RestaurantRepository.getInstance();
        MenuItemRepository  menuItemRepo  = MenuItemRepository.getInstance();
        OrderRepository     orderRepo     = OrderRepository.getInstance();

        // --- 1. SAVE TO DATABASE ---
        separator("STAGE II.1 — SAVE TO DATABASE");
        customerRepo.create(ana);
        customerRepo.create(mihai);
        customerRepo.createCourier(bogdan);
        customerRepo.createCourier(elena);
        System.out.println("Saved 2 customers + 2 couriers to DB.");

        restaurantRepo.create(pizzaLoco);
        restaurantRepo.create(burgerKing);
        restaurantRepo.create(sushiZen);
        System.out.println("Saved 3 restaurants to DB.");

        menuItemRepo.create(margherita, "R001");
        menuItemRepo.create(pepperoni,  "R001");
        menuItemRepo.create(cola,       "R001");
        menuItemRepo.create(bere,       "R001");
        menuItemRepo.create(burger,     "R002");
        menuItemRepo.create(vegBurger,  "R002");
        System.out.println("Saved 6 menu items to DB.");

        orderRepo.create(comanda1);
        orderRepo.create(comanda2);
        System.out.println("Saved 2 orders to DB.");

        // --- 2. READ FROM DATABASE ---
        separator("STAGE II.2 — READ FROM DATABASE");
        System.out.println("Customers from DB:");
        customerRepo.findAll().forEach(c -> System.out.println("  " + c));

        System.out.println("Restaurants from DB:");
        restaurantRepo.findAll().forEach(res -> System.out.println("  " + res));

        System.out.println("Orders for Ana (C001) from DB:");
        orderRepo.findByCustomerId("C001").forEach(o -> System.out.println("  " + o));

        // --- 3. UPDATE order status ---
        separator("STAGE II.3 — UPDATE ORDER STATUS");
        comanda2.setStatus(OrderStatus.DELIVERED);
        orderRepo.update(comanda2);
        System.out.println("Updated comanda2 status -> DELIVERED in DB.");
        orderRepo.findById(comanda2.getOrderId())
                 .ifPresent(o -> System.out.println("Verified from DB: " + o));

        // --- 4. DELETE a menu item ---
        separator("STAGE II.4 — DELETE MENU ITEM");
        menuItemRepo.delete("D002");
        System.out.println("Deleted DrinkItem 'Bere Ursus' (D002) from DB.");
        System.out.println("Menu R001 from DB after delete:");
        menuItemRepo.findByRestaurantId("R001")
                    .forEach(m -> System.out.println("  " + m));

        // --- 5. PRINT audit.csv ---
        separator("STAGE II.5 — AUDIT LOG");
        System.out.println("audit.csv contents:");
        try {
            Files.readAllLines(Paths.get("audit.csv")).forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Could not read audit.csv: " + e.getMessage());
        }
    }

    private static void separator(String title) {
        System.out.println("\n========== " + title + " ==========");
    }
}
