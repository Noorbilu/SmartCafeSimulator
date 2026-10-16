package ui;

import model.User;
import util.UITheme;

import javax.swing.*;
import java.awt.*;

public class CashierDashboard extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JButton[] sidebarButtons;

    public CashierDashboard(User user) {
        setTitle("Smart Café Simulator - Cashier");
        setSize(1300, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(230, 0));

        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(UITheme.SIDEBAR_BG);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(25, 24, 25, 24));
        JLabel logo = new JLabel("☕ SMART CAFÉ");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logo.setForeground(Color.WHITE);
        JLabel role = new JLabel("CASHIER PANEL");
        role.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        role.setForeground(UITheme.ACCENT);
        logoPanel.add(logo);
        logoPanel.add(role);
        sidebar.add(logoPanel);

        String[] menuItems = {"Point of Sale", "Order History", "View Menu & Stock"};
        sidebarButtons = new JButton[menuItems.length];

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.add(wrap(new POSPanel(user)), "Point of Sale");
        contentPanel.add(wrap(new OrderHistoryPanel()), "Order History");
        contentPanel.add(wrap(new ViewMenuPanel()), "View Menu & Stock");

        for (int i = 0; i < menuItems.length; i++) {
            final String cardName = menuItems[i];
            final int index = i;
            JButton btn = UITheme.createSidebarButton("  " + menuItems[i]);
            btn.addActionListener(e -> {
                cardLayout.show(contentPanel, cardName);
                highlightButton(index);
            });
            sidebarButtons[i] = btn;
            sidebar.add(btn);
        }
        sidebar.add(Box.createVerticalGlue());
        highlightButton(0);

        JPanel header = UITheme.createHeaderBar("Point of Sale",
                "👤 " + user.getFullName() + " (CASHIER)",
                this::logout);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.add(header, BorderLayout.NORTH);
        mainArea.add(contentPanel, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(mainArea, BorderLayout.CENTER);
    }

    private JPanel wrap(JPanel panel) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BACKGROUND);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    private void highlightButton(int activeIndex) {
        for (int i = 0; i < sidebarButtons.length; i++) {
            sidebarButtons[i].setBackground(i == activeIndex ? UITheme.SIDEBAR_ACTIVE : UITheme.SIDEBAR_BG);
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Logout from Smart Café Simulator?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}