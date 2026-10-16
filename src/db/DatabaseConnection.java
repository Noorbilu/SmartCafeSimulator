package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton JDBC Connection Manager for Smart Café Simulator.
 * Uses embedded SQLite — no server installation needed.
 * Auto-creates schema (matching the ER Diagram) and seeds demo data on first
 * run.
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:cafe.db";

    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            try (Statement st = connection.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
            createTables();
            seedData();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage(), e);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private void createTables() throws SQLException {
        String[] ddl = {
                "CREATE TABLE IF NOT EXISTS users (" +
                        "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT UNIQUE NOT NULL," +
                        "password TEXT NOT NULL," +
                        "full_name TEXT," +
                        "role TEXT NOT NULL CHECK(role IN ('ADMIN','CASHIER'))" +
                        ")",

                "CREATE TABLE IF NOT EXISTS categories (" +
                        "category_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "category_name TEXT NOT NULL," +
                        "description TEXT" +
                        ")",

                "CREATE TABLE IF NOT EXISTS items (" +
                        "item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "category_id INTEGER," +
                        "item_name TEXT NOT NULL," +
                        "price REAL NOT NULL," +
                        "is_combo INTEGER DEFAULT 0," +
                        "is_available INTEGER DEFAULT 1," +
                        "FOREIGN KEY (category_id) REFERENCES categories(category_id)" +
                        ")",

                "CREATE TABLE IF NOT EXISTS inventory (" +
                        "inventory_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "item_id INTEGER UNIQUE," +
                        "stock_quantity INTEGER DEFAULT 0," +
                        "last_updated TEXT," +
                        "FOREIGN KEY (item_id) REFERENCES items(item_id)" +
                        ")",

                "CREATE TABLE IF NOT EXISTS orders (" +
                        "order_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER," +
                        "order_date TEXT," +
                        "order_type TEXT," +
                        "total_amount REAL," +
                        "payment_status TEXT," +
                        "FOREIGN KEY (user_id) REFERENCES users(user_id)" +
                        ")",

                "CREATE TABLE IF NOT EXISTS order_items (" +
                        "order_item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "order_id INTEGER," +
                        "item_id INTEGER," +
                        "quantity INTEGER," +
                        "unit_price REAL," +
                        "subtotal REAL," +
                        "FOREIGN KEY (order_id) REFERENCES orders(order_id)," +
                        "FOREIGN KEY (item_id) REFERENCES items(item_id)" +
                        ")",

                "CREATE TABLE IF NOT EXISTS sim_events (" +
                        "sim_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "order_id INTEGER," +
                        "sim_time TEXT," +
                        "status TEXT," +
                        "event_type TEXT," +
                        "peak_hour INTEGER," +
                        "customer_count INTEGER," +
                        "FOREIGN KEY (order_id) REFERENCES orders(order_id)" +
                        ")"
        };

        try (Statement st = connection.createStatement()) {
            for (String sql : ddl)
                st.execute(sql);
        }
    }

    private void seedData() throws SQLException {
        try (Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery("SELECT COUNT(*) as cnt FROM users")) {
            if (rs.next() && rs.getInt("cnt") == 0) {
                insertDefaultData();
            }
        }
    }

    private void insertDefaultData() throws SQLException {
        try (Statement st = connection.createStatement()) {
            // Default login users
            st.execute("INSERT INTO users (username, password, full_name, role) VALUES " +
                    "('admin', 'admin123', 'System Administrator', 'ADMIN')");
            st.execute("INSERT INTO users (username, password, full_name, role) VALUES " +
                    "('cashier', 'cashier123', 'Cashier One', 'CASHIER')");

            // Categories
            st.execute(
                    "INSERT INTO categories (category_name, description) VALUES ('Beverages', 'Hot and cold drinks')");
            st.execute(
                    "INSERT INTO categories (category_name, description) VALUES ('Snacks', 'Light snacks and fries')");
            st.execute(
                    "INSERT INTO categories (category_name, description) VALUES ('Main Course', 'Full meals and burgers')");
            st.execute("INSERT INTO categories (category_name, description) VALUES ('Desserts', 'Sweet treats')");

            // Items (category_id: 1=Beverages, 2=Snacks, 3=Main Course, 4=Desserts)
            st.execute(
                    "INSERT INTO items (category_id, item_name, price, is_combo, is_available) VALUES (1, 'Espresso', 120, 0, 1)");
            st.execute(
                    "INSERT INTO items (category_id, item_name, price, is_combo, is_available) VALUES (1, 'Cappuccino', 150, 0, 1)");
            st.execute(
                    "INSERT INTO items (category_id, item_name, price, is_combo, is_available) VALUES (1, 'Iced Tea', 100, 0, 1)");
            st.execute(
                    "INSERT INTO items (category_id, item_name, price, is_combo, is_available) VALUES (2, 'French Fries', 90, 0, 1)");
            st.execute(
                    "INSERT INTO items (category_id, item_name, price, is_combo, is_available) VALUES (2, 'Sandwich', 130, 0, 1)");
            st.execute(
                    "INSERT INTO items (category_id, item_name, price, is_combo, is_available) VALUES (3, 'Burger', 200, 0, 1)");
            st.execute(
                    "INSERT INTO items (category_id, item_name, price, is_combo, is_available) VALUES (3, 'Pasta', 220, 0, 1)");
            st.execute(
                    "INSERT INTO items (category_id, item_name, price, is_combo, is_available) VALUES (4, 'Chocolate Cake', 150, 0, 1)");
            st.execute(
                    "INSERT INTO items (category_id, item_name, price, is_combo, is_available) VALUES (3, 'Combo Meal (Burger+Fries+Drink)', 350, 1, 1)");

            // Inventory for items 1..9 (matches insertion order above)
            int[] stocks = { 50, 50, 40, 60, 30, 25, 20, 15, 15 };
            for (int i = 0; i < stocks.length; i++) {
                st.execute("INSERT INTO inventory (item_id, stock_quantity, last_updated) VALUES (" +
                        (i + 1) + ", " + stocks[i] + ", '" + java.time.LocalDateTime.now() + "')");
            }
        }
    }
}