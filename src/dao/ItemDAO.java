package dao;

import db.DatabaseConnection;
import model.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {
    private Connection conn;
    private InventoryDAO inventoryDAO;

    public ItemDAO() {
        conn = DatabaseConnection.getInstance().getConnection();
        inventoryDAO = new InventoryDAO();
    }

    public List<Item> getAllItems() {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT i.*, c.category_name FROM items i LEFT JOIN categories c ON i.category_id=c.category_id ORDER BY i.item_id";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Item> getAvailableItemsInStock() {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT i.*, c.category_name, inv.stock_quantity FROM items i " +
                     "LEFT JOIN categories c ON i.category_id=c.category_id " +
                     "LEFT JOIN inventory inv ON i.item_id = inv.item_id " +
                     "WHERE i.is_available=1 AND inv.stock_quantity > 0 ORDER BY i.item_name";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Item it = mapRow(rs);
                it.setStock(rs.getInt("stock_quantity"));
                list.add(it);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Item getItemById(int id) {
        String sql = "SELECT i.*, c.category_name FROM items i LEFT JOIN categories c ON i.category_id=c.category_id WHERE i.item_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int addItem(Item it) {
        String sql = "INSERT INTO items (category_id, item_name, price, is_combo, is_available) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, it.getCategoryId());
            ps.setString(2, it.getItemName());
            ps.setDouble(3, it.getPrice());
            ps.setInt(4, it.isCombo() ? 1 : 0);
            ps.setInt(5, it.isAvailable() ? 1 : 0);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int newId = keys.getInt(1);
                inventoryDAO.addInventoryRecord(newId, 0);
                return newId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateItem(Item it) {
        String sql = "UPDATE items SET category_id=?, item_name=?, price=?, is_combo=?, is_available=? WHERE item_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, it.getCategoryId());
            ps.setString(2, it.getItemName());
            ps.setDouble(3, it.getPrice());
            ps.setInt(4, it.isCombo() ? 1 : 0);
            ps.setInt(5, it.isAvailable() ? 1 : 0);
            ps.setInt(6, it.getItemId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteItem(int itemId) {
        try {
            PreparedStatement ps1 = conn.prepareStatement("DELETE FROM inventory WHERE item_id=?");
            ps1.setInt(1, itemId);
            ps1.executeUpdate();
            PreparedStatement ps2 = conn.prepareStatement("DELETE FROM items WHERE item_id=?");
            ps2.setInt(1, itemId);
            ps2.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Item mapRow(ResultSet rs) throws SQLException {
        Item it = new Item();
        it.setItemId(rs.getInt("item_id"));
        it.setCategoryId(rs.getInt("category_id"));
        it.setCategoryName(rs.getString("category_name"));
        it.setItemName(rs.getString("item_name"));
        it.setPrice(rs.getDouble("price"));
        it.setCombo(rs.getInt("is_combo") == 1);
        it.setAvailable(rs.getInt("is_available") == 1);
        return it;
    }
}