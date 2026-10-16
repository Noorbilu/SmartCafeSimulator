package ui;

import dao.UserDAO;
import model.User;
import util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageUsersPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private UserDAO userDAO = new UserDAO();

    public ManageUsersPanel() {
        setBackground(UITheme.BACKGROUND);
        setLayout(new BorderLayout(0, 15));

        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 225, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel title = new JLabel("User Management");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_DARK);

        String[] cols = {"ID", "Username", "Full Name", "Role"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(UITheme.CARD_BG);

        JButton addBtn = UITheme.createPrimaryButton("+ Add User");
        JButton editBtn = UITheme.createSecondaryButton("Edit User");
        JButton deleteBtn = UITheme.createDangerButton("Delete User");
        JButton refreshBtn = UITheme.createSecondaryButton("Refresh");

        addBtn.addActionListener(e -> showUserDialog(null));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a user to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) model.getValueAt(row, 0);
            showUserDialog(findUserById(id));
        });
        deleteBtn.addActionListener(e -> deleteUser());
        refreshBtn.addActionListener(e -> loadData());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        card.add(title, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        card.add(btnPanel, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);

        loadData();
    }

    private User findUserById(int id) {
        for (User u : userDAO.getAllUsers()) if (u.getUserId() == id) return u;
        return null;
    }

    private void loadData() {
        model.setRowCount(0);
        List<User> users = userDAO.getAllUsers();
        for (User u : users) {
            model.addRow(new Object[]{u.getUserId(), u.getUsername(), u.getFullName(), u.getRole()});
        }
    }

    private void showUserDialog(User existing) {
        JTextField usernameField = new JTextField(existing != null ? existing.getUsername() : "");
        JPasswordField passwordField = new JPasswordField(existing != null ? existing.getPassword() : "");
        JTextField fullNameField = new JTextField(existing != null ? existing.getFullName() : "");
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"ADMIN", "CASHIER"});
        if (existing != null) roleCombo.setSelectedItem(existing.getRole());

        JPanel panel = new JPanel(new GridLayout(4, 2, 8, 8));
        panel.add(new JLabel("Username:")); panel.add(usernameField);
        panel.add(new JLabel("Password:")); panel.add(passwordField);
        panel.add(new JLabel("Full Name:")); panel.add(fullNameField);
        panel.add(new JLabel("Role:")); panel.add(roleCombo);

        int result = JOptionPane.showConfirmDialog(this, panel,
                existing == null ? "Add New User" : "Edit User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String fullName = fullNameField.getText().trim();
            String role = (String) roleCombo.getSelectedItem();

            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (existing == null) {
                if (userDAO.usernameExists(username)) {
                    JOptionPane.showMessageDialog(this, "Username already exists!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                User u = new User();
                u.setUsername(username);
                u.setPassword(password);
                u.setFullName(fullName);
                u.setRole(role);
                userDAO.addUser(u);
                JOptionPane.showMessageDialog(this, "User added successfully!");
            } else {
                existing.setUsername(username);
                existing.setPassword(password);
                existing.setFullName(fullName);
                existing.setRole(role);
                userDAO.updateUser(existing);
                JOptionPane.showMessageDialog(this, "User updated successfully!");
            }
            loadData();
        }
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        String username = (String) model.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete user '" + username + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            userDAO.deleteUser(id);
            loadData();
            JOptionPane.showMessageDialog(this, "User deleted.");
        }
    }
}