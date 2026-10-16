package ui;

import dao.OrderDAO;
import util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderHistoryPanel extends JPanel {
    public OrderHistoryPanel() {
        setLayout(new BorderLayout(0, 10));
        setBackground(UITheme.CARD_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 225, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel title = new JLabel("Order History");
        title.setFont(UITheme.FONT_BOLD);

        String[] cols = {"Order ID", "Date", "Type", "Total", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        OrderDAO orderDAO = new OrderDAO();
        List<Object[]> orders = orderDAO.getRecentOrdersDetailed(100);
        for (Object[] row : orders) model.addRow(row);

        JTable table = new JTable(model);
        UITheme.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);

        JButton refreshBtn = UITheme.createSecondaryButton("Refresh");
        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            for (Object[] row : orderDAO.getRecentOrdersDetailed(100)) model.addRow(row);
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setBackground(UITheme.CARD_BG);
        bottom.add(refreshBtn);

        add(title, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }
}