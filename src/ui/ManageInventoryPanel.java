package ui;

import dao.InventoryDAO;
import model.Inventory;
import util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageInventoryPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private InventoryDAO inventoryDAO = new InventoryDAO();
    private static final int LOW_STOCK_THRESHOLD = 10;

    public ManageInventoryPanel() {
        setBackground(UITheme.BACKGROUND);
        setLayout(new BorderLayout(0, 15));

        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 225, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel title = new JLabel("Inventory Management");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_DARK);

        String[] cols = {"Inventory ID", "Item ID", "Item Name", "Stock Qty", "Last Updated"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                int stock = Integer.parseInt(model.getValueAt(row, 3).toString());
                if (!isSelected) {
                    c.setBackground(stock <= LOW_STOCK_THRESHOLD ? new Color(255, 225, 220) : Color.WHITE);
                } else {
                    c.setBackground(new Color(255, 224, 178));
                }
                setForeground(UITheme.TEXT_DARK);
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(UITheme.CARD_BG);

        JButton updateBtn = UITheme.createPrimaryButton("Update Stock");
        JButton refreshBtn = UITheme.createSecondaryButton("Refresh");
        JLabel legend = new JLabel("  🔴 Red rows = Low Stock (≤ " + LOW_STOCK_THRESHOLD + ")");
        legend.setFont(UITheme.FONT_LABEL);
        legend.setForeground(UITheme.TEXT_MUTED);

        updateBtn.addActionListener(e -> updateStock());
        refreshBtn.addActionListener(e -> loadData());

        btnPanel.add(updateBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(legend);

        card.add(title, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        card.add(btnPanel, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        List<Inventory> list = inventoryDAO.getAllInventory();
        for (Inventory inv : list) {
            model.addRow(new Object[]{
                inv.getInventoryId(), inv.getItemId(), inv.getItemName(),
                inv.getStockQuantity(), inv.getLastUpdated()
            });
        }
    }

    private void updateStock() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an item to update stock.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int itemId = (int) model.getValueAt(row, 1);
        String itemName = (String) model.getValueAt(row, 2);
        int currentStock = (int) model.getValueAt(row, 3);

        String input = JOptionPane.showInputDialog(this,
                "Enter new stock quantity for '" + itemName + "':\n(Current: " + currentStock + ")",
                currentStock);

        if (input != null) {
            try {
                int newStock = Integer.parseInt(input.trim());
                if (newStock < 0) throw new NumberFormatException();
                inventoryDAO.setStock(itemId, newStock);
                loadData();
                JOptionPane.showMessageDialog(this, "Stock updated successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid non-negative number!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}