package ui;

import dao.InventoryDAO;
import dao.ItemDAO;
import dao.OrderDAO;
import dao.UserDAO;
import util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardHomePanel extends JPanel {
    public DashboardHomePanel() {
        setLayout(new BorderLayout(0, 20));
        setBackground(UITheme.BACKGROUND);

        OrderDAO orderDAO = new OrderDAO();
        InventoryDAO inventoryDAO = new InventoryDAO();
        ItemDAO itemDAO = new ItemDAO();
        UserDAO userDAO = new UserDAO();

        // Stat cards row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 15, 0));
        statsRow.setBackground(UITheme.BACKGROUND);
        statsRow.add(UITheme.createStatCard("TOTAL REVENUE",
                String.format("$%.2f", orderDAO.getTotalSales()), UITheme.SUCCESS));
        statsRow.add(UITheme.createStatCard("TOTAL ORDERS",
                String.valueOf(orderDAO.getTotalOrders()), UITheme.ACCENT));
        statsRow.add(UITheme.createStatCard("LOW STOCK ITEMS",
                String.valueOf(inventoryDAO.getLowStockItems(10).size()), UITheme.DANGER));
        statsRow.add(UITheme.createStatCard("MENU ITEMS",
                String.valueOf(itemDAO.getAllItems().size()), UITheme.PRIMARY));

        // Bottom split: Best sellers chart + recent orders table
        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomRow.setBackground(UITheme.BACKGROUND);

        JPanel chartCard = new JPanel(new BorderLayout());
        chartCard.setBackground(UITheme.CARD_BG);
        chartCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 225, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        JLabel chartTitle = new JLabel("Best Selling Items");
        chartTitle.setFont(UITheme.FONT_BOLD);
        chartCard.add(chartTitle, BorderLayout.NORTH);
        chartCard.add(new BarChartPanel(orderDAO.getBestSellingItems(5)), BorderLayout.CENTER);

        JPanel ordersCard = new JPanel(new BorderLayout());
        ordersCard.setBackground(UITheme.CARD_BG);
        ordersCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 225, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        JLabel ordersTitle = new JLabel("Inventory Alert");
        ordersTitle.setFont(UITheme.FONT_BOLD);

        String[] cols = {"Item", "Stock Left"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        List<model.Inventory> lowStock = inventoryDAO.getLowStockItems(15);
        for (model.Inventory inv : lowStock) {
            model.addRow(new Object[]{inv.getItemName(), inv.getStockQuantity()});
        }
        JTable table = new JTable(model);
        UITheme.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);

        ordersCard.add(ordersTitle, BorderLayout.NORTH);
        ordersCard.add(scrollPane, BorderLayout.CENTER);

        bottomRow.add(chartCard);
        bottomRow.add(ordersCard);

        add(statsRow, BorderLayout.NORTH);
        add(bottomRow, BorderLayout.CENTER);
    }

    // Simple inline bar chart
    static class BarChartPanel extends JPanel {
        private List<Object[]> data;

        BarChartPanel(List<Object[]> data) {
            this.data = data;
            setBackground(UITheme.CARD_BG);
            setPreferredSize(new Dimension(400, 250));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) {
                g.setColor(UITheme.TEXT_MUTED);
                g.drawString("No sales data yet. Run a simulation or place an order.", 20, 100);
                return;
            }
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int maxQty = 1;
            for (Object[] row : data) {
                int qty = (int) row[1];
                if (qty > maxQty) maxQty = qty;
            }

            int width = getWidth() - 40;
            int height = getHeight() - 60;
            int barWidth = width / data.size() - 20;
            int x = 30;

            for (Object[] row : data) {
                String name = (String) row[0];
                int qty = (int) row[1];
                int barHeight = (int) ((qty / (double) maxQty) * height);

                g2.setColor(UITheme.ACCENT);
                g2.fillRoundRect(x, height - barHeight + 20, barWidth, barHeight, 6, 6);

                g2.setColor(UITheme.TEXT_DARK);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.drawString(String.valueOf(qty), x + barWidth / 2 - 5, height - barHeight + 10);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                String shortName = name.length() > 10 ? name.substring(0, 9) + "." : name;
                g2.drawString(shortName, x, height + 35);

                x += barWidth + 20;
            }
        }
    }
}