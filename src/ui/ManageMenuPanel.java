package ui;

import dao.CategoryDAO;
import dao.ItemDAO;
import model.Category;
import model.Item;
import util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageMenuPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private ItemDAO itemDAO = new ItemDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();

    public ManageMenuPanel() {
        setBackground(UITheme.BACKGROUND);
        setLayout(new BorderLayout(0, 15));

        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 225, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel title = new JLabel("Menu Management");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_DARK);

        String[] cols = {"ID", "Item Name", "Category", "Price", "Combo", "Available"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(UITheme.CARD_BG);

        JButton addBtn = UITheme.createPrimaryButton("+ Add Item");
        JButton editBtn = UITheme.createSecondaryButton("Edit Item");
        JButton deleteBtn = UITheme.createDangerButton("Delete Item");
        JButton addCatBtn = UITheme.createSecondaryButton("+ Add Category");
        JButton refreshBtn = UITheme.createSecondaryButton("Refresh");

        addBtn.addActionListener(e -> showItemDialog(null));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select an item to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) model.getValueAt(row, 0);
            showItemDialog(itemDAO.getItemById(id));
        });
        deleteBtn.addActionListener(e -> deleteItem());
        addCatBtn.addActionListener(e -> addCategory());
        refreshBtn.addActionListener(e -> loadData());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(addCatBtn);
        btnPanel.add(refreshBtn);

        card.add(title, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        card.add(btnPanel, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        List<Item> items = itemDAO.getAllItems();
        for (Item it : items) {
            model.addRow(new Object[]{
                it.getItemId(), it.getItemName(), it.getCategoryName(),
                String.format("$%.2f", it.getPrice()),
                it.isCombo() ? "Yes" : "No",
                it.isAvailable() ? "Yes" : "No"
            });
        }
    }

    private void addCategory() {
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("Category Name:")); panel.add(nameField);
        panel.add(new JLabel("Description:")); panel.add(descField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Category", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Category name is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Category c = new Category();
            c.setCategoryName(name);
            c.setDescription(descField.getText().trim());
            categoryDAO.addCategory(c);
            JOptionPane.showMessageDialog(this, "Category added successfully!");
        }
    }

    private void showItemDialog(Item existing) {
        List<Category> categories = categoryDAO.getAllCategories();
        if (categories.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add a category first!", "No Categories", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField nameField = new JTextField(existing != null ? existing.getItemName() : "");
        JTextField priceField = new JTextField(existing != null ? String.valueOf(existing.getPrice()) : "");
        JComboBox<String> catCombo = new JComboBox<>();
        for (Category c : categories) catCombo.addItem(c.getCategoryName());
        if (existing != null) catCombo.setSelectedItem(existing.getCategoryName());

        JCheckBox comboCheck = new JCheckBox("Is Combo Item", existing != null && existing.isCombo());
        JCheckBox availCheck = new JCheckBox("Is Available", existing == null || existing.isAvailable());

        JPanel panel = new JPanel(new GridLayout(5, 2, 8, 8));
        panel.add(new JLabel("Item Name:")); panel.add(nameField);
        panel.add(new JLabel("Price:")); panel.add(priceField);
        panel.add(new JLabel("Category:")); panel.add(catCombo);
        panel.add(new JLabel("")); panel.add(comboCheck);
        panel.add(new JLabel("")); panel.add(availCheck);

        int result = JOptionPane.showConfirmDialog(this, panel,
                existing == null ? "Add New Item" : "Edit Item",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String priceText = priceField.getText().trim();

            if (name.isEmpty() || priceText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Price are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceText);
                if (price < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive price!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int categoryId = categories.get(catCombo.getSelectedIndex()).getCategoryId();

            if (existing == null) {
                Item it = new Item();
                it.setItemName(name);
                it.setPrice(price);
                it.setCategoryId(categoryId);
                it.setCombo(comboCheck.isSelected());
                it.setAvailable(availCheck.isSelected());
                itemDAO.addItem(it);
                JOptionPane.showMessageDialog(this, "Item added! Set initial stock in the Inventory tab.");
            } else {
                existing.setItemName(name);
                existing.setPrice(price);
                existing.setCategoryId(categoryId);
                existing.setCombo(comboCheck.isSelected());
                existing.setAvailable(availCheck.isSelected());
                itemDAO.updateItem(existing);
                JOptionPane.showMessageDialog(this, "Item updated successfully!");
            }
            loadData();
        }
    }

    private void deleteItem() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an item to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete item '" + name + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            itemDAO.deleteItem(id);
            loadData();
            JOptionPane.showMessageDialog(this, "Item deleted.");
        }
    }
}