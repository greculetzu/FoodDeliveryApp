-- addresses table
CREATE TABLE IF NOT EXISTS addresses (
    id VARCHAR(36) PRIMARY KEY,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL
);

-- users table (stores both customers and couriers, role column differentiates)
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL,         -- 'CUSTOMER' or 'COURIER'
    default_address_id VARCHAR(36) REFERENCES addresses(id),
    vehicle_type VARCHAR(50),          -- only for couriers
    is_available BOOLEAN DEFAULT TRUE  -- only for couriers
);

-- restaurants table
CREATE TABLE IF NOT EXISTS restaurants (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    rating DOUBLE PRECISION DEFAULT 0.0,
    address_id VARCHAR(36) REFERENCES addresses(id)
);

-- menu_items table (stores both FoodItem and DrinkItem)
CREATE TABLE IF NOT EXISTS menu_items (
    id VARCHAR(36) PRIMARY KEY,
    restaurant_id VARCHAR(36) REFERENCES restaurants(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL,         -- 'FoodItem' or 'DrinkItem'
    calories INT,                      -- only for FoodItem
    is_vegetarian BOOLEAN,             -- only for FoodItem
    volume_ml INT,                     -- only for DrinkItem
    is_alcoholic BOOLEAN               -- only for DrinkItem
);

-- orders table
CREATE TABLE IF NOT EXISTS orders (
    id VARCHAR(36) PRIMARY KEY,
    customer_id VARCHAR(36) REFERENCES users(id),
    restaurant_id VARCHAR(36) REFERENCES restaurants(id),
    courier_id VARCHAR(36) REFERENCES users(id),
    delivery_address_id VARCHAR(36) REFERENCES addresses(id),
    status VARCHAR(30) NOT NULL,
    placed_at TIMESTAMP NOT NULL
);

-- order_items table
CREATE TABLE IF NOT EXISTS order_items (
    id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) REFERENCES orders(id) ON DELETE CASCADE,
    menu_item_id VARCHAR(36) REFERENCES menu_items(id),
    quantity INT NOT NULL
);

-- reviews table
CREATE TABLE IF NOT EXISTS reviews (
    id VARCHAR(36) PRIMARY KEY,
    restaurant_id VARCHAR(36) REFERENCES restaurants(id) ON DELETE CASCADE,
    customer_id VARCHAR(36) REFERENCES users(id),
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT
);
