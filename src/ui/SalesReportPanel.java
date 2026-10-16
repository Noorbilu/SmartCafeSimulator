package ui;

import dao.InventoryDAO;
import dao.OrderDAO;
import model.Inventory;
import util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SalesReportPanel extends JPanel {
    private JLabel totalSalesLabel;
    private JLabel totalOrdersLabel;
    private DefaultTableModel bestSellingModel;
    private DefaultTableModel lowStockModel;
    private OrderDAO orderDAO = new OrderDAO();
    private InventoryDAO inventoryDAO = new InventoryDAO();

    public SalesReportPanel() {
        setBackground(UITheme.BACKGROUND);
        setLayout(new BorderLayout(0, 15));

        JLabel pageTitle = new JLabel("Sales Reports");
        pageTitle.setFont(UITheme.FONT_HEADER);
        pageTitle.setForeground(UITheme.TEXT_DARK);
        pageTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JPanel summaryPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        summaryPanel.setBackground(UITheme.BACKGROUND);

        totalSalesLabel = new JLabel();
        totalOrdersLabel = new JLabel();

        summaryPanel.add(buildStatCard("TOTAL REVENUE", totalSalesLabel, UITheme.SUCCESS));
        summaryPanel.add(buildStatCard("TOTAL ORDERS", totalOrdersLabel, UITheme.ACCENT));

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        tablesPanel.setBackground(UITheme.BACKGROUND);

        JPanel bestCard = new JPanel(new BorderLayout(0, 10));
        bestCard.setBackground(UITheme.CARD_BG);
        bestCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 225, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        JLabel bestTitle = new JLabel("Best Selling Items");
        bestTitle.setFont(UITheme.FONT_BOLD);

        String[] bestCols = {"Item Name", "Qty Sold", "Revenue"};
        bestSellingModel = new DefaultTableModel(bestCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable bestTable = new JTable(bestSellingModel);
        UITheme.styleTable(bestTable);
        JScrollPane bestScroll = new JScrollPane(bestTable);
        bestScroll.setBorder(null);
        bestCard.add(bestTitle, BorderLayout.NORTH);
        bestCard.add(bestScroll, BorderLayout.CENTER);

        JPanel lowCard = new JPanel(new BorderLayout(0, 10));
        lowCard.setBackground(UITheme.CARD_BG);
        lowCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 225, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        JLabel lowTitle = new JLabel("Low Stock Alert (≤ 10)");
        lowTitle.setFont(UITheme.FONT_BOLD);

        String[] lowCols = {"Item Name", "Stock Qty"};
        lowStockModel = new DefaultTableModel(lowCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable lowTable = new JTable(lowStockModel);
        UITheme.styleTable(lowTable);
        JScrollPane lowScroll = new JScrollPane(lowTable);
        lowScroll.setBorder(null);
        lowCard.add(lowTitle, BorderLayout.NORTH);
        lowCard.add(lowScroll, BorderLayout.CENTER);

        tablesPanel.add(bestCard);
        tablesPanel.add(lowCard);

        JButton refreshBtn = UITheme.createPrimaryButton("Refresh Report");
        refreshBtn.addActionListener(e -> loadData());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setBackground(UITheme.BACKGROUND);
        bottom.add(refreshBtn);

        JPanel topWrap = new JPanel(new BorderLayout(0, 10));
        topWrap.setBackground(UITheme.BACKGROUND);
        topWrap.add(pageTitle, BorderLayout.NORTH);
        topWrap.add(summaryPanel, BorderLayout.CENTER);

        add(topWrap, BorderLayout.NORTH);
        add(tablesPanel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        loadData();
    }

    private JPanel buildStatCard(String labelText, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 225, 220)),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(accentColor);
        accentBar.setPreferredSize(new Dimension(5, 0));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(UITheme.CARD_BG);
        textPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        valueLabel.setFont(UITheme.FONT_STAT_VALUE);
        valueLabel.setForeground(UITheme.TEXT_DARK);

        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.FONT_STAT_LABEL);
        label.setForeground(UITheme.TEXT_MUTED);

        textPanel.add(valueLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(label);

        card.add(accentBar, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private void loadData() {
        totalSalesLabel.setText(String.format("$%.2f", orderDAO.getTotalSales()));
        totalOrdersLabel.setText(String.valueOf(orderDAO.getTotalOrders()));

        bestSellingModel.setRowCount(0);
        List<Object[]> best = orderDAO.getBestSellingItems(10);
        for (Object[] row : best) {
            bestSellingModel.addRow(new Object[]{row[0], row[1], String.format("$%.2f", (double) row[2])});
        }

        lowStockModel.setRowCount(0);
        List<Inventory> lowStock = inventoryDAO.getLowStockItems(10);
        for (Inventory inv : lowStock) {
            lowStockModel.addRow(new Object[]{inv.getItemName(), inv.getStockQuantity()});
        }
    }
}