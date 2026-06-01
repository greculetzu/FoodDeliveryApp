# FoodDeliveryApp — POO 2 Java Project

## Project location
C:\Users\dobre\FoodDeliveryApp

## GitHub
https://github.com/greculetzu/FoodDeliveryApp

## Build system
Plain Java project in IntelliJ IDEA. No Maven/Gradle.
Source root: src/
Main class: Main.java (in src/com/fooddelivery/)

## Package structure
src/
com/fooddelivery/
model/        ← entity classes
service/      ← business logic services
db/           ← JDBC database services (Stage II)
audit/        ← CSV audit service (Stage II)
Main.java

## Stage I — ALREADY IMPLEMENTED (do not rewrite unless broken)
### Model classes in com.fooddelivery.model:
- User.java (abstract) — id, name, email, phone; abstract getRole()
- Customer.java extends User — defaultAddress (Address), List<String> orderIds
- Courier.java extends User — vehicleType (String), isAvailable (boolean)
- MenuItem.java (abstract) — id, name, price, description; abstract getType()
- FoodItem.java extends MenuItem — calories (int), isVegetarian (boolean)
- DrinkItem.java extends MenuItem — volumeMl (int), isAlcoholic (boolean)
- Address.java — street, city, postalCode; private fields + getters
- Review.java — reviewId, customerId, rating (1-5 validated), comment
- Restaurant.java — implements Comparable<Restaurant> by rating descending;
  List<MenuItem> menu; List<Review> reviews; updateRating()
- OrderItem.java — MenuItem + quantity; getSubtotal()
- Order.java — UUID id, customerId, restaurantId, courierId,
  List<OrderItem>, Address deliveryAddress,
  OrderStatus status, LocalDateTime placedAt; calculateTotal()
- Category.java (enum) — PIZZA, BURGER, SUSHI, CHINESE, SALADS, DESSERTS
- OrderStatus.java (enum) — PLACED, PREPARING, ON_THE_WAY, DELIVERED, CANCELLED

### Service classes in com.fooddelivery.service:
- UserService.java — List<Customer>, List<Courier>; add/find/list operations
- RestaurantService.java — TreeSet<Restaurant>; add, addReview (remove-update-reinsert), searchByCategory, listSortedByRating
- OrderService.java — HashMap<String, Order>; placeOrder, assignCourier, updateStatus, calculateTotal, getOrderHistory

### 13 actions demonstrated in Main.java:
1. Register customer
2. Register courier
3. Register restaurant
4. Add items to menu
5. View restaurant menu
6. Place order
7. Assign courier to order
8. Update order status
9. Calculate order total
10. Add review
11. View restaurants sorted by rating
12. Search restaurants by category
13. View customer order history

## Stage II — TO BE IMPLEMENTED
### 1. JDBC Persistence (PostgreSQL)
- DatabaseConnection.java (singleton) — manages single Connection
- GenericRepository.java (generic singleton base) — generic read/write helpers
- CustomerRepository.java — CRUD for Customer
- RestaurantRepository.java — CRUD for Restaurant
- OrderRepository.java — CRUD for Order
- MenuItemRepository.java — CRUD for MenuItem (FoodItem + DrinkItem)
- SQL schema file: schema.sql

### 2. Audit Service
- AuditService.java (singleton) — writes to audit.csv
- Format: action_name,timestamp
- Called every time one of the 13 actions is executed

## Code style rules
- All fields private, use getters/setters
- Singletons use private constructor + static getInstance()
- No Spring, no Hibernate — plain JDBC only
- Java 17+
- Each class in its own file
- No compilation errors — always verify before finishing