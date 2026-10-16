package dao;

import db.DatabaseConnection;
import model.Inventory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {
    private Connection conn;

    public InventoryDAO() {
        conn = DatabaseConnection.getInstance().getConnection();
    }

    public List<Inventory> getAllInventory() {
        List<Inventory> list = new ArrayList<>();
        String sql = "SELECT inv.inventory_id, inv.item_id, i.item_name, inv.stock_quantity, inv.last_updated " +
                     "FROM inventory inv JOIN items i ON inv.item_id = i.item_id ORDER BY i.item_name";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getStock(int itemId) {
        String sql = "SELECT stock_quantity FROM inventory WHERE item_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("stock_quantity");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean setStock(int itemId, int qty) {
        String sql = "UPDATE inventory SET stock_quantity=?, last_updated=? WHERE item_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setString(2, java.time.LocalDateTime.now().toString());
            ps.setInt(3, itemId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean reduceStock(int itemId, int qty) {
        int current = getStock(itemId);
        int newQty = Math.max(0, current - qty);
        return setStock(itemId, newQty);
    }

    public boolean addInventoryRecord(int itemId, int qty) {
        String sql = "INSERT INTO inventory (item_id, stock_quantity, last_updated) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            ps.setInt(2, qty);
            ps.setString(3, java.time.LocalDateTime.now().toString());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Inventory> getLowStockItems(int threshold) {
        List<Inventory> list = new ArrayList<>();
        String sql = "SELECT inv.inventory_id, inv.item_id, i.item_name, inv.stock_quantity, inv.last_updated " +
                     "FROM inventory inv JOIN items i ON inv.item_id = i.item_id WHERE inv.stock_quantity <= ? ORDER BY inv.stock_quantity";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Inventory mapRow(ResultSet rs) throws SQLException {
        Inventory inv = new Inventory();
        inv.setInventoryId(rs.getInt("inventory_id"));
        inv.setItemId(rs.getInt("item_id"));
        inv.setItemName(rs.getString("item_name"));
        inv.setStockQuantity(rs.getInt("stock_quantity"));
        inv.setLastUpdated(rs.getString("last_updated"));
        return inv;
    }
}