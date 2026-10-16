package dao;

import db.DatabaseConnection;
import model.Order;
import model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private Connection conn;

    public OrderDAO() {
        conn = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Places an order, its items, and reduces inventory stock
     * all within a single database transaction.
     * Returns the new order ID, or -1 if the transaction failed (auto rolled back).
     */
    public int placeOrderTransactional(Order order, List<OrderItem> items) {
        int orderId = -1;
        try {
            conn.setAutoCommit(false);

            String orderSql = "INSERT INTO orders (user_id, order_date, order_type, total_amount, payment_status) VALUES (?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, order.getUserId());
                ps.setString(2, order.getOrderDate());
                ps.setString(3, order.getOrderType());
                ps.setDouble(4, order.getTotalAmount());
                ps.setString(5, order.getPaymentStatus());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) orderId = keys.getInt(1);
            }

            if (orderId == -1) throw new SQLException("Failed to generate order ID");

            String itemSql = "INSERT INTO order_items (order_id, item_id, quantity, unit_price, subtotal) VALUES (?,?,?,?,?)";
            String stockCheckSql = "SELECT stock_quantity FROM inventory WHERE item_id=?";
            String stockUpdateSql = "UPDATE inventory SET stock_quantity=?, last_updated=? WHERE item_id=?";

            for (OrderItem oi : items) {
                try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, oi.getItemId());
                    ps.setInt(3, oi.getQuantity());
                    ps.setDouble(4, oi.getUnitPrice());
                    ps.setDouble(5, oi.getSubtotal());
                    ps.executeUpdate();
                }

                int currentStock = 0;
                try (PreparedStatement ps = conn.prepareStatement(stockCheckSql)) {
                    ps.setInt(1, oi.getItemId());
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) currentStock = rs.getInt("stock_quantity");
                }

                int newStock = Math.max(0, currentStock - oi.getQuantity());
                try (PreparedStatement ps = conn.prepareStatement(stockUpdateSql)) {
                    ps.setInt(1, newStock);
                    ps.setString(2, java.time.LocalDateTime.now().toString());
                    ps.setInt(3, oi.getItemId());
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return orderId;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return -1;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int createOrder(Order o) {
        String sql = "INSERT INTO orders (user_id, order_date, order_type, total_amount, payment_status) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, o.getUserId());
            ps.setString(2, o.getOrderDate());
            ps.setString(3, o.getOrderType());
            ps.setDouble(4, o.getTotalAmount());
            ps.setString(5, o.getPaymentStatus());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean addOrderItem(OrderItem oi) {
        String sql = "INSERT INTO order_items (order_id, item_id, quantity, unit_price, subtotal) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, oi.getOrderId());
            ps.setInt(2, oi.getItemId());
            ps.setInt(3, oi.getQuantity());
            ps.setDouble(4, oi.getUnitPrice());
            ps.setDouble(5, oi.getSubtotal());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getTotalSales() {
        String sql = "SELECT SUM(total_amount) as total FROM orders WHERE payment_status='PAID'";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalOrders() {
        String sql = "SELECT COUNT(*) as cnt FROM orders";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("cnt");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Object[]> getBestSellingItems(int limit) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT i.item_name, SUM(oi.quantity) as total_qty, SUM(oi.subtotal) as total_rev " +
                     "FROM order_items oi JOIN items i ON oi.item_id = i.item_id " +
                     "GROUP BY oi.item_id ORDER BY total_qty DESC LIMIT ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{rs.getString("item_name"), rs.getInt("total_qty"), rs.getDouble("total_rev")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Object[]> getRecentOrdersDetailed(int limit) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT order_id, order_date, order_type, total_amount, payment_status FROM orders ORDER BY order_id DESC LIMIT ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("order_id"),
                        rs.getString("order_date") != null ? rs.getString("order_date").substring(0, 16) : "",
                        rs.getString("order_type"),
                        String.format("$%.2f", rs.getDouble("total_amount")),
                        rs.getString("payment_status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}