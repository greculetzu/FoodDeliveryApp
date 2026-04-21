package com.fooddelivery;

import com.fooddelivery.model.*;
import com.fooddelivery.service.OrderService;
import com.fooddelivery.service.RestaurantService;
import com.fooddelivery.service.UserService;

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
        for (MenuItem item : r.getMenu()) {
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
        orderService.placeOrder(ana, burgerKing, items2, ana.getDefaultAddress());

        List<Order> istoricAna = orderService.getOrderHistory("C001");
        System.out.println("Istoricul comenzilor Ana (" + istoricAna.size() + " comenzi):");
        for (Order o : istoricAna) {
            System.out.println("  " + o);
        }
    }

    private static void separator(String title) {
        System.out.println("\n========== " + title + " ==========");
    }
}
