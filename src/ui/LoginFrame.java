package ui;

import dao.UserDAO;
import model.User;
import util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Smart Café Simulator - Login");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new GridLayout(1, 2));

        // LEFT: Branding panel
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(UITheme.SIDEBAR_BG);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(new EmptyBorder(60, 50, 60, 50));

        JLabel logo = new JLabel("☕");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        logo.setForeground(UITheme.ACCENT);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brand = new JLabel("SMART CAFÉ");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 34));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brand2 = new JLabel("SIMULATOR");
        brand2.setFont(new Font("Segoe UI", Font.BOLD, 34));
        brand2.setForeground(UITheme.ACCENT);
        brand2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("<html><div style='width:280px'>Point of Sale & Inventory Management System with real-time customer simulation.</div></html>");
        tagline.setFont(UITheme.FONT_SUBTITLE);
        tagline.setForeground(new Color(200, 190, 185));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);
        tagline.setBorder(new EmptyBorder(20, 0, 0, 0));

        leftPanel.add(logo);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(brand);
        leftPanel.add(brand2);
        leftPanel.add(tagline);
        leftPanel.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("v2.0 — Core Java • Swing • SQLite");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(new Color(150, 140, 135));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(footer);

        // RIGHT: Login form
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(UITheme.BACKGROUND);
        rightPanel.setLayout(new GridBagLayout());

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(UITheme.CARD_BG);
        formCard.setBorder(new EmptyBorder(40, 45, 40, 45));
        formCard.setPreferredSize(new Dimension(380, 420));

        JLabel welcomeLabel = new JLabel("Welcome Back");
        welcomeLabel.setFont(UITheme.FONT_TITLE);
        welcomeLabel.setForeground(UITheme.TEXT_DARK);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel("Sign in to continue to your dashboard");
        subLabel.setFont(UITheme.FONT_SUBTITLE);
        subLabel.setForeground(UITheme.TEXT_MUTED);
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subLabel.setBorder(new EmptyBorder(5, 0, 30, 0));

        JLabel userLabel = createFieldLabel("USERNAME");
        usernameField = createStyledTextField();

        JLabel passLabel = createFieldLabel("PASSWORD");
        passwordField = createStyledPasswordField();

        JButton loginBtn = UITheme.createPrimaryButton("LOGIN");
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginBtn.addActionListener(e -> attemptLogin());

        JPanel hintPanel = new JPanel();
        hintPanel.setLayout(new BoxLayout(hintPanel, BoxLayout.Y_AXIS));
        hintPanel.setBackground(new Color(255, 248, 230));
        hintPanel.setBorder(new EmptyBorder(12, 15, 12, 15));
        hintPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        hintPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel hint1 = new JLabel("Admin: admin / admin123");
        hint1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JLabel hint2 = new JLabel("Cashier: cashier / cashier123");
        hint2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hintPanel.add(hint1);
        hintPanel.add(hint2);

        formCard.add(welcomeLabel);
        formCard.add(subLabel);
        formCard.add(userLabel);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(usernameField);
        formCard.add(Box.createVerticalStrut(18));
        formCard.add(passLabel);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(passwordField);
        formCard.add(Box.createVerticalStrut(25));
        formCard.add(loginBtn);
        formCard.add(Box.createVerticalStrut(25));
        formCard.add(hintPanel);

        rightPanel.add(formCard);

        root.add(leftPanel);
        root.add(rightPanel);
        add(root);

        passwordField.addActionListener(e -> attemptLogin());
    }

    private JLabel createFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(UITheme.TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(UITheme.FONT_LABEL);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 215, 210)),
                new EmptyBorder(8, 5, 8, 5)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(UITheme.FONT_LABEL);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 215, 210)),
                new EmptyBorder(8, 5, 8, 5)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.login(username, password);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            return;
        }

        dispose();

        if (user.getRole().equalsIgnoreCase("ADMIN")) {
            new AdminDashboard(user).setVisible(true);
        } else {
            new CashierDashboard(user).setVisible(true);
        }
    }
}