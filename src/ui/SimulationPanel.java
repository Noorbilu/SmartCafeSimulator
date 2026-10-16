package ui;

import dao.InventoryDAO;
import dao.ItemDAO;
import dao.OrderDAO;
import dao.SimEventDAO;
import model.Item;
import model.Order;
import model.OrderItem;
import model.SimEvent;
import model.User;
import util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationPanel extends JPanel {
    private JSpinner customerSpinner;
    private JTextArea logArea;
    private User currentUser;
    private Random random = new Random();

    public SimulationPanel(User user) {
        this.currentUser = user;
        setBackground(UITheme.BACKGROUND);
        setLayout(new BorderLayout(0, 15));

        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 225, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel title = new JLabel("Customer Simulation");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_DARK);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.setBackground(UITheme.CARD_BG);

        JLabel spinnerLabel = new JLabel("Number of Customers:");
        spinnerLabel.setFont(UITheme.FONT_LABEL);

        customerSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 500, 1));

        JButton runBtn = UITheme.createPrimaryButton("▶ Run Simulation");
        runBtn.addActionListener(e -> runSimulation());

        JButton clearBtn = UITheme.createSecondaryButton("Clear Log");
        clearBtn.addActionListener(e -> logArea.setText(""));

        topPanel.add(spinnerLabel);
        topPanel.add(customerSpinner);
        topPanel.add(runBtn);
        topPanel.add(clearBtn);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBackground(new Color(250, 248, 245));
        logArea.setForeground(UITheme.TEXT_DARK);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 225, 220)));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.CARD_BG);
        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(topPanel, BorderLayout.CENTER);

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }

    private void runSimulation() {
        int numCustomers = (Integer) customerSpinner.getValue();
        logArea.append("===== STARTING SIMULATION: " + numCustomers + " CUSTOMERS =====\n");

        ItemDAO itemDAO = new ItemDAO();
        InventoryDAO inventoryDAO = new InventoryDAO();
        OrderDAO orderDAO = new OrderDAO();
        SimEventDAO simEventDAO = new SimEventDAO();

        int successCount = 0;
        int failCount = 0;

        for (int i = 1; i <= numCustomers; i++) {
            List<Item> availableItems = itemDAO.getAvailableItemsInStock();
            if (availableItems.isEmpty()) {
                logArea.append("Customer #" + i + ": SKIPPED - No items in stock!\n");
                failCount++;
                continue;
            }

            int numItems = 1 + random.nextInt(3);
            List<OrderItem> items = new ArrayList<>();
            double total = 0;

            for (int j = 0; j < numItems; j++) {
                Item it = availableItems.get(random.nextInt(availableItems.size()));
                int stock = inventoryDAO.getStock(it.getItemId());
                if (stock <= 0) continue;

                boolean alreadyAdded = false;
                for (OrderItem oi : items) {
                    if (oi.getItemId() == it.getItemId()) { alreadyAdded = true; break; }
                }
                if (alreadyAdded) continue;

                int qty = Math.min(1 + random.nextInt(3), stock);

                OrderItem oi = new OrderItem();
                oi.setItemId(it.getItemId());
                oi.setItemName(it.getItemName());
                oi.setQuantity(qty);
                oi.setUnitPrice(it.getPrice());
                oi.setSubtotal(qty * it.getPrice());
                items.add(oi);
                total += oi.getSubtotal();
            }

            if (items.isEmpty()) {
                logArea.append("Customer #" + i + ": SKIPPED - No stock available!\n");
                failCount++;
                continue;
            }

            Order order = new Order();
            order.setUserId(currentUser.getUserId());
            order.setOrderDate(LocalDateTime.now().toString());
            order.setOrderType(random.nextBoolean() ? "Dine-in" : "Takeaway");
            order.setTotalAmount(total);
            order.setPaymentStatus("PAID");

            int orderId = orderDAO.placeOrderTransactional(order, items);
            if (orderId == -1) {
                logArea.append("Customer #" + i + ": FAILED to create order!\n");
                failCount++;
                continue;
            }

            StringBuilder itemsDesc = new StringBuilder();
            for (OrderItem oi : items) {
                itemsDesc.append(oi.getItemName()).append("x").append(oi.getQuantity()).append(" ");
            }

            boolean isPeakHour = random.nextInt(100) < 30;
            SimEvent event = new SimEvent();
            event.setOrderId(orderId);
            event.setSimTime(LocalDateTime.now().toString());
            event.setStatus("SUCCESS");
            event.setEventType("ORDER_PLACED");
            event.setPeakHour(isPeakHour);
            event.setCustomerCount(i);
            simEventDAO.addSimEvent(event);

            logArea.append(String.format("Customer #%d: Order #%d | %s | Total: $%.2f | %s%n",
                    i, orderId, itemsDesc.toString().trim(), total, isPeakHour ? "[PEAK HOUR]" : ""));
            successCount++;
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }

        logArea.append("===== SIMULATION COMPLETE =====\n");
        logArea.append("Successful Orders: " + successCount + " | Failed/Skipped: " + failCount + "\n\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());

        JOptionPane.showMessageDialog(this,
                "Simulation finished!\nSuccessful: " + successCount + "\nSkipped: " + failCount,
                "Simulation Complete", JOptionPane.INFORMATION_MESSAGE);
    }
}