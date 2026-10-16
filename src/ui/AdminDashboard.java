package ui;

import model.User;
import util.UITheme;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JButton[] sidebarButtons;

    public AdminDashboard(User user) {
        setTitle("Smart Café Simulator - Admin");
        setSize(1300, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sidebar
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
        JLabel role = new JLabel("ADMIN PANEL");
        role.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        role.setForeground(UITheme.ACCENT);
        logoPanel.add(logo);
        logoPanel.add(role);
        sidebar.add(logoPanel);

        String[] menuItems = {"Dashboard", "Menu Management", "Inventory", "Sales Reports", "User Management", "Simulation"};
        sidebarButtons = new JButton[menuItems.length];

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(wrap(new DashboardHomePanel()), "Dashboard");
        contentPanel.add(wrap(new ManageMenuPanel()), "Menu Management");
        contentPanel.add(wrap(new ManageInventoryPanel()), "Inventory");
        contentPanel.add(wrap(new SalesReportPanel()), "Sales Reports");
        contentPanel.add(wrap(new ManageUsersPanel()), "User Management");
        contentPanel.add(wrap(new SimulationPanel(user)), "Simulation");

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

        // Header
        JPanel header = UITheme.createHeaderBar("Admin Dashboard",
                "👤 " + user.getFullName() + " (ADMIN)",
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