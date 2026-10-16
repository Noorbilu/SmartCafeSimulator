package ui;

import model.OrderItem;
import util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class ReceiptDialog extends JDialog {
    public ReceiptDialog(Frame parent, int orderId, String cashierName, String orderType,
                          String paymentMethod, List<OrderItem> items, double total) {
        super(parent, "Order Receipt", true);
        setSize(400, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel success = new JLabel("✓ PAYMENT SUCCESSFUL");
        success.setFont(new Font("Segoe UI", Font.BOLD, 16));
        success.setForeground(UITheme.SUCCESS);
        success.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("SMART CAFÉ SIMULATOR");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        JTextArea receiptText = new JTextArea();
        receiptText.setEditable(false);
        receiptText.setFont(new Font("Consolas", Font.PLAIN, 12));
        receiptText.setBackground(new Color(250, 250, 250));

        StringBuilder sb = new StringBuilder();
        sb.append("Order ID   : #").append(orderId).append("\n");
        sb.append("Date       : ").append(LocalDateTime.now().toString().substring(0, 16)).append("\n");
        sb.append("Cashier    : ").append(cashierName).append("\n");
        sb.append("Order Type : ").append(orderType).append("\n");
        sb.append("Payment    : ").append(paymentMethod).append("\n");
        sb.append("------------------------------------\n");
        for (OrderItem oi : items) {
            sb.append(String.format("%-18s x%-3d $%7.2f%n", oi.getItemName(), oi.getQuantity(), oi.getSubtotal()));
        }
        sb.append("------------------------------------\n");
        sb.append(String.format("%-22s $%7.2f%n", "TOTAL", total));
        sb.append("\nThank you for visiting Smart Café!");

        receiptText.setText(sb.toString());

        JScrollPane scrollPane = new JScrollPane(receiptText);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 225, 220)));

        JButton closeBtn = UITheme.createPrimaryButton("Close & Print Receipt");
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.addActionListener(e -> dispose());

        content.add(success);
        content.add(title);
        content.add(scrollPane);
        content.add(Box.createVerticalStrut(15));
        content.add(closeBtn);

        add(content, BorderLayout.CENTER);
    }
}