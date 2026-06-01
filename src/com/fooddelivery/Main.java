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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        UserService         userService    = new UserService();
        RestaurantService   restaurantService = new RestaurantService();
        OrderService        orderService   = new OrderService();
        CustomerRepository  customerRepo   = CustomerRepository.getInstance();
        RestaurantRepository restaurantRepo = RestaurantRepository.getInstance();
        MenuItemRepository  menuItemRepo   = MenuItemRepository.getInstance();
        OrderRepository     orderRepo      = OrderRepository.getInstance();

        loadDataFromDB(userService, restaurantService, orderService,
                customerRepo, restaurantRepo, menuItemRepo, orderRepo);

        while (true) {
            showMenu();
            String line = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Optiune invalida. Incercati din nou.");
                continue;
            }

            switch (choice) {
                case 0  -> stageIIDemo();
                case 1  -> registerCustomer(scanner, userService, customerRepo);
                case 2  -> registerCourier(scanner, userService, customerRepo);
                case 3  -> registerRestaurant(scanner, restaurantService, restaurantRepo);
                case 4  -> addMenuItem(scanner, restaurantService, menuItemRepo);
                case 5  -> viewMenu(scanner, restaurantService);
                case 6  -> placeOrder(scanner, userService, restaurantService, orderService, orderRepo);
                case 7  -> assignCourier(scanner, userService, orderService, orderRepo);
                case 8  -> updateStatus(scanner, orderService, orderRepo);
                case 9  -> calculateTotal(scanner, orderService);
                case 10 -> addReview(scanner, restaurantService);
                case 11 -> viewRestaurantsByRating(restaurantService);
                case 12 -> searchByCategory(scanner, restaurantService);
                case 13 -> viewOrderHistory(scanner, orderService);
                case 99 -> { System.out.println("La revedere!"); return; }
                default -> System.out.println("Optiune invalida. Incercati din nou.");
            }
        }
    }

    // -------------------------------------------------------------------------
    // Menu display
    // -------------------------------------------------------------------------

    private static void loadDataFromDB(UserService userService,
                                       RestaurantService restaurantService,
                                       OrderService orderService,
                                       CustomerRepository customerRepo,
                                       RestaurantRepository restaurantRepo,
                                       MenuItemRepository menuItemRepo,
                                       OrderRepository orderRepo) {
        // 1. Customers
        customerRepo.findAll().forEach(userService::loadCustomer);

        // 2. Couriers
        customerRepo.findAllCouriers().forEach(userService::loadCourier);

        // 3. Restaurants
        for (Restaurant r : restaurantRepo.findAll()) {
            restaurantService.loadRestaurant(r);
            // 4. Menu items for this restaurant
            menuItemRepo.findByRestaurantId(r.getRestaurantId())
                        .forEach(r::addMenuItem);
        }

        // 5. Orders
        orderRepo.findAll().forEach(orderService::addOrder);

        System.out.println("[System] Date incarcate din baza de date.");
    }

    private static void showMenu() {
        System.out.println("\n==========================================");
        System.out.println("       FOOD DELIVERY APP");
        System.out.println("==========================================");
        System.out.println("1.  Inregistrare client");
        System.out.println("2.  Inregistrare curier");
        System.out.println("3.  Inregistrare restaurant");
        System.out.println("4.  Adaugare produs in meniu");
        System.out.println("5.  Vizualizare meniu restaurant");
        System.out.println("6.  Plasare comanda");
        System.out.println("7.  Asignare curier la comanda");
        System.out.println("8.  Actualizare status comanda");
        System.out.println("9.  Calcul total comanda");
        System.out.println("10. Adaugare recenzie");
        System.out.println("11. Vizualizare restaurante dupa rating");
        System.out.println("12. Cautare restaurante dupa categorie");
        System.out.println("13. Vizualizare istoric comenzi client");
        System.out.println("99. Exit");
        System.out.println("==========================================");
        System.out.print("Alegeti o optiune: ");
    }

    // -------------------------------------------------------------------------
    // Option handlers
    // -------------------------------------------------------------------------

    private static void registerCustomer(Scanner sc, UserService userService,
                                         CustomerRepository customerRepo) {
        System.out.print("ID: ");           String id     = sc.nextLine().trim();
        System.out.print("Nume: ");         String name   = sc.nextLine().trim();
        System.out.print("Email: ");        String email  = sc.nextLine().trim();
        System.out.print("Telefon: ");      String phone  = sc.nextLine().trim();
        System.out.print("Strada: ");       String street = sc.nextLine().trim();
        System.out.print("Oras: ");         String city   = sc.nextLine().trim();
        System.out.print("Cod postal: ");   String zip    = sc.nextLine().trim();

        Customer c = new Customer(id, name, email, phone, new Address(street, city, zip));
        userService.addCustomer(c);
        customerRepo.create(c);
        System.out.println("Client salvat in DB.");
    }

    private static void registerCourier(Scanner sc, UserService userService,
                                        CustomerRepository customerRepo) {
        System.out.print("ID: ");           String id      = sc.nextLine().trim();
        System.out.print("Nume: ");         String name    = sc.nextLine().trim();
        System.out.print("Email: ");        String email   = sc.nextLine().trim();
        System.out.print("Telefon: ");      String phone   = sc.nextLine().trim();
        System.out.print("Tip vehicul: ");  String vehicle = sc.nextLine().trim();

        Courier c = new Courier(id, name, email, phone, vehicle);
        userService.addCourier(c);
        customerRepo.createCourier(c);
        System.out.println("Curier salvat in DB.");
    }

    private static void registerRestaurant(Scanner sc, RestaurantService restaurantService,
                                           RestaurantRepository restaurantRepo) {
        System.out.print("ID: ");    String id   = sc.nextLine().trim();
        System.out.print("Nume: ");  String name = sc.nextLine().trim();
        System.out.println("Categorii disponibile: " + Arrays.toString(Category.values()));
        System.out.print("Categorie: "); String catStr = sc.nextLine().trim().toUpperCase();
        Category category;
        try {
            category = Category.valueOf(catStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Categorie invalida.");
            return;
        }
        System.out.print("Strada: ");     String street = sc.nextLine().trim();
        System.out.print("Oras: ");       String city   = sc.nextLine().trim();
        System.out.print("Cod postal: "); String zip    = sc.nextLine().trim();

        Restaurant r = new Restaurant(id, name, category, new Address(street, city, zip));
        restaurantService.addRestaurant(r);
        restaurantRepo.create(r);
        System.out.println("Restaurant salvat in DB.");
    }

    private static void addMenuItem(Scanner sc, RestaurantService restaurantService,
                                    MenuItemRepository menuItemRepo) {
        System.out.print("ID restaurant: "); String restaurantId = sc.nextLine().trim();
        if (restaurantService.getRestaurantById(restaurantId) == null) {
            System.out.println("Restaurant inexistent.");
            return;
        }
        System.out.println("Tip produs: 1 = FoodItem, 2 = DrinkItem");
        System.out.print("Tip: "); String typeStr = sc.nextLine().trim();

        System.out.print("ID produs: ");   String id   = sc.nextLine().trim();
        System.out.print("Nume: ");        String name = sc.nextLine().trim();
        System.out.print("Pret (RON): ");
        double price;
        try { price = Double.parseDouble(sc.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Pret invalid."); return; }
        System.out.print("Descriere: "); String desc = sc.nextLine().trim();

        MenuItem item;
        if ("1".equals(typeStr)) {
            System.out.print("Calorii: ");
            int cal;
            try { cal = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Calorii invalide."); return; }
            System.out.print("Vegetarian (y/n): ");
            boolean veg = sc.nextLine().trim().equalsIgnoreCase("y");
            item = new FoodItem(id, name, price, desc, cal, veg);
        } else {
            System.out.print("Volum (ml): ");
            int vol;
            try { vol = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Volum invalid."); return; }
            System.out.print("Alcoolica (y/n): ");
            boolean alc = sc.nextLine().trim().equalsIgnoreCase("y");
            item = new DrinkItem(id, name, price, desc, alc, vol);
        }

        restaurantService.addMenuItemToRestaurant(restaurantId, item);
        menuItemRepo.create(item, restaurantId);
        System.out.println("Produs salvat in DB.");
    }

    private static void viewMenu(Scanner sc, RestaurantService restaurantService) {
        System.out.print("ID restaurant: "); String restaurantId = sc.nextLine().trim();
        List<MenuItem> menu = restaurantService.getMenu(restaurantId);
        if (menu.isEmpty()) {
            System.out.println("Meniu gol sau restaurant inexistent.");
            return;
        }
        System.out.println("Meniu:");
        for (MenuItem item : menu) {
            System.out.println("  - " + item);
        }
    }

    private static void placeOrder(Scanner sc, UserService userService,
                                   RestaurantService restaurantService,
                                   OrderService orderService, OrderRepository orderRepo) {
        System.out.print("ID client: "); String customerId = sc.nextLine().trim();
        Customer customer = userService.getCustomerById(customerId);
        if (customer == null) { System.out.println("Client inexistent."); return; }

        System.out.print("ID restaurant: "); String restaurantId = sc.nextLine().trim();
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        if (restaurant == null) { System.out.println("Restaurant inexistent."); return; }

        List<MenuItem> menu = restaurantService.getMenu(restaurantId);
        if (menu.isEmpty()) { System.out.println("Meniu gol."); return; }

        System.out.println("Produse disponibile:");
        for (int i = 0; i < menu.size(); i++) {
            System.out.printf("  %d. %-30s %.2f RON%n",
                    i + 1, menu.get(i).getName(), menu.get(i).getPrice());
        }

        List<OrderItem> items = new ArrayList<>();
        System.out.println("Adaugati produse (introduceti 0 pentru a termina):");
        while (true) {
            System.out.print("  Numar produs: ");
            int idx;
            try { idx = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("  Input invalid."); continue; }
            if (idx == 0) break;
            if (idx < 1 || idx > menu.size()) { System.out.println("  Index invalid."); continue; }
            System.out.print("  Cantitate: ");
            int qty;
            try { qty = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("  Cantitate invalida."); continue; }
            if (qty <= 0) { System.out.println("  Cantitate invalida."); continue; }
            items.add(new OrderItem(menu.get(idx - 1), qty));
            System.out.printf("  Adaugat: %dx %s%n", qty, menu.get(idx - 1).getName());
        }
        if (items.isEmpty()) { System.out.println("Niciun produs selectat."); return; }

        System.out.println("Adresa livrare: 1 = Adresa implicita, 2 = Adresa noua");
        System.out.print("Optiune: ");
        Address deliveryAddress;
        if ("2".equals(sc.nextLine().trim())) {
            System.out.print("Strada: ");     String street = sc.nextLine().trim();
            System.out.print("Oras: ");       String city   = sc.nextLine().trim();
            System.out.print("Cod postal: "); String zip    = sc.nextLine().trim();
            deliveryAddress = new Address(street, city, zip);
        } else {
            deliveryAddress = customer.getDefaultAddress();
        }

        Order order = orderService.placeOrder(customer, restaurant, items, deliveryAddress);
        orderRepo.create(order);
        System.out.println("Comanda plasata cu succes!");
        System.out.println("Order ID: " + order.getOrderId());
        System.out.printf("Total: %.2f RON%n", order.calculateTotal());
    }

    private static void assignCourier(Scanner sc, UserService userService,
                                      OrderService orderService, OrderRepository orderRepo) {
        printAllOrders(orderService);
        System.out.print("ID comanda: "); String orderId = sc.nextLine().trim();
        Order order = orderService.getOrderById(orderId);
        if (order == null) { System.out.println("Comanda inexistenta."); return; }

        List<Courier> available = userService.getAvailableCouriers();
        if (available.isEmpty()) { System.out.println("Niciun curier disponibil."); return; }
        System.out.println("Curieri disponibili:");
        available.forEach(c -> System.out.println("  " + c));

        System.out.print("ID curier: "); String courierId = sc.nextLine().trim();
        Courier courier = userService.getCourierById(courierId);
        if (courier == null) { System.out.println("Curier inexistent."); return; }
        if (!courier.isAvailable()) { System.out.println("Curierul nu este disponibil."); return; }

        orderService.assignCourier(orderId, courier);
        orderRepo.update(order);
        System.out.println("Curier asignat si comanda actualizata in DB.");
    }

    private static void updateStatus(Scanner sc, OrderService orderService,
                                     OrderRepository orderRepo) {
        printAllOrders(orderService);
        System.out.print("ID comanda: "); String orderId = sc.nextLine().trim();
        Order order = orderService.getOrderById(orderId);
        if (order == null) { System.out.println("Comanda inexistenta."); return; }

        System.out.println("Status curent: " + order.getStatus());
        System.out.println("Statusuri disponibile: " + Arrays.toString(OrderStatus.values()));
        System.out.print("Status nou: ");
        String statusStr = sc.nextLine().trim().toUpperCase();
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Status invalid.");
            return;
        }

        orderService.updateOrderStatus(orderId, newStatus);
        orderRepo.update(order);
        System.out.println("Status actualizat in DB.");
    }

    private static void calculateTotal(Scanner sc, OrderService orderService) {
        printAllOrders(orderService);
        System.out.print("ID comanda: "); String orderId = sc.nextLine().trim();
        Order order = orderService.getOrderById(orderId);
        if (order == null) { System.out.println("Comanda inexistenta."); return; }
        System.out.printf("Total comanda: %.2f RON%n", orderService.calculateTotal(orderId));
    }

    private static void addReview(Scanner sc, RestaurantService restaurantService) {
        System.out.print("ID restaurant: "); String restaurantId = sc.nextLine().trim();
        if (restaurantService.getRestaurantById(restaurantId) == null) {
            System.out.println("Restaurant inexistent.");
            return;
        }
        System.out.print("ID client: ");     String customerId = sc.nextLine().trim();
        System.out.print("Rating (1-5): ");
        int rating;
        try { rating = Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Rating invalid."); return; }
        System.out.print("Comentariu: "); String comment = sc.nextLine().trim();

        try {
            Review review = new Review(UUID.randomUUID().toString(), customerId, rating, comment);
            restaurantService.addReview(restaurantId, review);
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private static void viewRestaurantsByRating(RestaurantService restaurantService) {
        System.out.println("Restaurante (descrescator dupa rating):");
        for (Restaurant r : restaurantService.getRestaurantsSortedByRating()) {
            System.out.printf("  %-25s | %-10s | Rating: %.1f%n",
                    r.getName(), r.getCategory(), r.getRating());
        }
    }

    private static void searchByCategory(Scanner sc, RestaurantService restaurantService) {
        System.out.println("Categorii disponibile: " + Arrays.toString(Category.values()));
        System.out.print("Categorie: "); String catStr = sc.nextLine().trim().toUpperCase();
        Category category;
        try {
            category = Category.valueOf(catStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Categorie invalida.");
            return;
        }
        List<Restaurant> results = restaurantService.getRestaurantsByCategory(category);
        if (results.isEmpty()) { System.out.println("Niciun restaurant gasit."); return; }
        System.out.println("Restaurante " + category + ":");
        results.forEach(r -> System.out.println("  " + r));
    }

    private static void viewOrderHistory(Scanner sc, OrderService orderService) {
        System.out.print("ID client: "); String customerId = sc.nextLine().trim();
        List<Order> orders = orderService.getOrderHistory(customerId);
        if (orders.isEmpty()) { System.out.println("Nicio comanda gasita."); return; }
        System.out.println("Comenzi (" + orders.size() + "):");
        orders.forEach(o -> System.out.println("  " + o));
    }

    // -------------------------------------------------------------------------
    // Hidden option 0 — self-contained Stage I + II demo
    // -------------------------------------------------------------------------

    private static void stageIIDemo() {
        UserService        userService       = new UserService();
        RestaurantService  restaurantService = new RestaurantService();
        OrderService       orderService      = new OrderService();
        CustomerRepository  customerRepo     = CustomerRepository.getInstance();
        RestaurantRepository restaurantRepo  = RestaurantRepository.getInstance();
        MenuItemRepository  menuItemRepo     = MenuItemRepository.getInstance();
        OrderRepository     orderRepo        = OrderRepository.getInstance();

        separator("1. INREGISTRARE CLIENTI");
        Customer ana = new Customer("C001", "Ana Popescu", "ana@mail.com", "0722111222",
                new Address("Str. Florilor 5", "Bucuresti", "010101"));
        Customer mihai = new Customer("C002", "Mihai Ionescu", "mihai@mail.com", "0733222333",
                new Address("Bd. Unirii 20", "Cluj-Napoca", "400001"));
        userService.addCustomer(ana);
        userService.addCustomer(mihai);

        separator("2. INREGISTRARE CURIER");
        Courier bogdan = new Courier("CR001", "Bogdan Rusu", "bogdan@mail.com", "0744333444", "Bicicleta");
        Courier elena  = new Courier("CR002", "Elena Vasile", "elena@mail.com", "0755444555", "Motocicleta");
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
        FoodItem pepperoni  = new FoodItem("P002", "Pizza Pepperoni",  38.0, "Sos rosii, pepperoni",  900, false);
        DrinkItem cola      = new DrinkItem("D001", "Coca-Cola 500ml",  8.0, "Bautura racoritoare", false, 500);
        DrinkItem bere      = new DrinkItem("D002", "Bere Ursus",      10.0, "Bere blonda",          true, 330);
        restaurantService.addMenuItemToRestaurant("R001", margherita);
        restaurantService.addMenuItemToRestaurant("R001", pepperoni);
        restaurantService.addMenuItemToRestaurant("R001", cola);
        restaurantService.addMenuItemToRestaurant("R001", bere);
        FoodItem burger    = new FoodItem("P003", "Classic Burger", 35.0, "Vita, salata, rosii", 820, false);
        FoodItem vegBurger = new FoodItem("P004", "Veggie Burger",  30.0, "Legume la gratar",    580, true);
        restaurantService.addMenuItemToRestaurant("R002", burger);
        restaurantService.addMenuItemToRestaurant("R002", vegBurger);

        separator("5. VIZUALIZARE MENIU RESTAURANT");
        System.out.println("Meniu Pizza Loco:");
        restaurantService.getMenu("R001").forEach(item -> System.out.println("  - " + item));

        separator("6. PLASARE COMANDA");
        List<OrderItem> items = Arrays.asList(new OrderItem(margherita, 2), new OrderItem(cola, 1));
        Order comanda1 = orderService.placeOrder(ana, pizzaLoco, items, ana.getDefaultAddress());

        separator("7. ASIGNARE CURIER");
        System.out.println("Curieri disponibili: " + userService.getAvailableCouriers());
        orderService.assignCourier(comanda1.getOrderId(), bogdan);

        separator("8. ACTUALIZARE STATUS COMANDA");
        orderService.updateOrderStatus(comanda1.getOrderId(), OrderStatus.PREPARING);
        orderService.updateOrderStatus(comanda1.getOrderId(), OrderStatus.ON_THE_WAY);
        orderService.updateOrderStatus(comanda1.getOrderId(), OrderStatus.DELIVERED);

        separator("9. CALCUL TOTAL COMANDA");
        System.out.printf("Total comanda: %.2f RON%n", orderService.calculateTotal(comanda1.getOrderId()));

        separator("10. ADAUGARE RECENZII");
        restaurantService.addReview("R001", new Review("REV001", "C001", 5, "Excelent! Pizza a venit calda."));
        restaurantService.addReview("R001", new Review("REV002", "C002", 4, "Foarte buna, dar livrarea a intarziat."));
        restaurantService.addReview("R002", new Review("REV003", "C001", 3, "Burger decent, nimic special."));

        separator("11. VIZUALIZARE RESTAURANTE SORTATE DUPA RATING");
        restaurantService.getRestaurantsSortedByRating()
                         .forEach(r -> System.out.println("  " + r));

        separator("12. CAUTARE RESTAURANTE DUPA CATEGORIE");
        System.out.println("Restaurante PIZZA: " +
                restaurantService.getRestaurantsByCategory(Category.PIZZA));

        separator("13. ISTORIC COMENZI CLIENT");
        List<OrderItem> items2 = Arrays.asList(new OrderItem(burger, 1), new OrderItem(bere, 2));
        Order comanda2 = orderService.placeOrder(ana, burgerKing, items2, ana.getDefaultAddress());
        orderService.getOrderHistory("C001")
                    .forEach(o -> System.out.println("  " + o));

        // ===== STAGE II DEMO =====
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

        separator("STAGE II.2 — READ FROM DATABASE");
        System.out.println("Customers from DB:");
        customerRepo.findAll().forEach(c -> System.out.println("  " + c));
        System.out.println("Restaurants from DB:");
        restaurantRepo.findAll().forEach(res -> System.out.println("  " + res));
        System.out.println("Orders for Ana (C001) from DB:");
        orderRepo.findByCustomerId("C001").forEach(o -> System.out.println("  " + o));

        separator("STAGE II.3 — UPDATE ORDER STATUS");
        comanda2.setStatus(OrderStatus.DELIVERED);
        orderRepo.update(comanda2);
        System.out.println("Updated comanda2 status -> DELIVERED in DB.");
        orderRepo.findById(comanda2.getOrderId())
                 .ifPresent(o -> System.out.println("Verified from DB: " + o));

        separator("STAGE II.4 — DELETE MENU ITEM");
        menuItemRepo.delete("D002");
        System.out.println("Deleted DrinkItem 'Bere Ursus' (D002) from DB.");
        System.out.println("Menu R001 from DB after delete:");
        menuItemRepo.findByRestaurantId("R001")
                    .forEach(m -> System.out.println("  " + m));

        separator("STAGE II.5 — AUDIT LOG");
        System.out.println("audit.csv contents:");
        try {
            Files.readAllLines(Paths.get("audit.csv")).forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Could not read audit.csv: " + e.getMessage());
        }
    }

    private static void printAllOrders(OrderService orderService) {
        List<Order> all = orderService.getAllOrders();
        if (all.isEmpty()) {
            System.out.println("Nu exista comenzi inregistrate.");
        } else {
            System.out.println("Comenzi existente:");
            for (Order o : all) {
                System.out.printf("  [%s] | Client: %s | Status: %s | Total: %.2f RON%n",
                        o.getOrderId(), o.getCustomerId(), o.getStatus(), o.calculateTotal());
            }
        }
    }

    private static void separator(String title) {
        System.out.println("\n========== " + title + " ==========");
    }
}
