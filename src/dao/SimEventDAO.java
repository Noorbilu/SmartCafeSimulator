package dao;

import db.DatabaseConnection;
import model.SimEvent;

import java.sql.*;

public class SimEventDAO {
    private Connection conn;

    public SimEventDAO() {
        conn = DatabaseConnection.getInstance().getConnection();
    }

    public boolean addSimEvent(SimEvent e) {
        String sql = "INSERT INTO sim_events (order_id, sim_time, status, event_type, peak_hour, customer_count) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, e.getOrderId());
            ps.setString(2, e.getSimTime());
            ps.setString(3, e.getStatus());
            ps.setString(4, e.getEventType());
            ps.setInt(5, e.isPeakHour() ? 1 : 0);
            ps.setInt(6, e.getCustomerCount());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}