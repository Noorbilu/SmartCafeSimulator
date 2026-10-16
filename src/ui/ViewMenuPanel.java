package ui;

import dao.InventoryDAO;
import dao.ItemDAO;
import model.Inventory;
import model.Item;
import util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewMenuPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;

    public ViewMenuPanel() {
        setBackground(UITheme.BACKGROUND);
        setLayout(new BorderLayout(0, 15));

        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 225, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel title = new JLabel("Menu & Stock Overview");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_DARK);

        String[] cols = {"ID", "Item Name", "Category", "Price", "Combo", "Available", "Stock"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);

        JButton refreshBtn = UITheme.createPrimaryButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setBackground(UITheme.CARD_BG);
        bottom.add(refreshBtn);

        card.add(title, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        ItemDAO itemDAO = new ItemDAO();
        InventoryDAO inventoryDAO = new InventoryDAO();

        Map<Integer, Integer> stockMap = new HashMap<>();
        for (Inventory inv : inventoryDAO.getAllInventory()) {
            stockMap.put(inv.getItemId(), inv.getStockQuantity());
        }

        List<Item> items = itemDAO.getAllItems();
        for (Item it : items) {
            model.addRow(new Object[]{
                it.getItemId(), it.getItemName(), it.getCategoryName(),
                String.format("$%.2f", it.getPrice()),
                it.isCombo() ? "Yes" : "No",
                it.isAvailable() ? "Yes" : "No",
                stockMap.getOrDefault(it.getItemId(), 0)
            });
        }
    }
}