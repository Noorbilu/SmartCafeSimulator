package ui;

import dao.ItemDAO;
import dao.OrderDAO;
import model.Item;
import model.Order;
import model.OrderItem;
import model.User;
import util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class POSPanel extends JPanel {
    private User currentUser;
    private DefaultTableModel menuModel;
    private DefaultTableModel orderModel;
    private JTable menuTable;
    private JTable orderTable;
    private JLabel totalLabel;
    private JLabel comboSuggestionLabel;
    private JComboBox<String> orderTypeCombo;
    private JSpinner qtySpinner;

    private List<Item> currentMenuItems;
    private List<OrderItem> cart = new ArrayList<>();

    public POSPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BACKGROUND);

        // LEFT: Menu
        JPanel leftCard = new JPanel(new BorderLayout(0, 10));
        leftCard.setBackground(UITheme.CARD_BG);
        leftCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 225, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel menuTitle = new JLabel("Available Menu Items");
        menuTitle.setFont(UITheme.FONT_BOLD);

        String[] menuCols = {"ID", "Item", "Category", "Price", "Stock"};
        menuModel = new DefaultTableModel(menuCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        menuTable = new JTable(menuModel);
        UITheme.styleTable(menuTable);
        JScrollPane menuScroll = new JScrollPane(menuTable);
        menuScroll.setBorder(null);

        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.setBackground(UITheme.CARD_BG);
        qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JButton addBtn = UITheme.createPrimaryButton("+ Add to Order");
        addBtn.addActionListener(e -> addSelectedToCart());
        addPanel.add(new JLabel("Qty: "));
        addPanel.add(qtySpinner);
        addPanel.add(addBtn);

        leftCard.add(menuTitle, BorderLayout.NORTH);
        leftCard.add(menuScroll, BorderLayout.CENTER);
        leftCard.add(addPanel, BorderLayout.SOUTH);

        // RIGHT: Cart
        JPanel rightCard = new JPanel(new BorderLayout(0, 10));
        rightCard.setBackground(UITheme.CARD_BG);
        rightCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 225, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        rightCard.setPreferredSize(new Dimension(420, 0));

        JLabel cartTitle = new JLabel("Current Order");
        cartTitle.setFont(UITheme.FONT_BOLD);

        String[] orderCols = {"Item", "Qty", "Price", "Subtotal"};
        orderModel = new DefaultTableModel(orderCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        orderTable = new JTable(orderModel);
        UITheme.styleTable(orderTable);
        JScrollPane orderScroll = new JScrollPane(orderTable);
        orderScroll.setBorder(null);

        comboSuggestionLabel = new JLabel(" ");
        comboSuggestionLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        comboSuggestionLabel.setForeground(UITheme.ACCENT_DARK);

        JButton removeBtn = UITheme.createSecondaryButton("Remove Selected");
        removeBtn.addActionListener(e -> removeSelectedFromCart());

        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalLabel.setForeground(UITheme.TEXT_DARK);

        orderTypeCombo = new JComboBox<>(new String[]{"Dine-in", "Takeaway"});

        JButton checkoutBtn = UITheme.createPrimaryButton("GENERATE BILL");
        checkoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        checkoutBtn.addActionListener(e -> checkout());

        JButton clearBtn = UITheme.createSecondaryButton("Clear");
        clearBtn.addActionListener(e -> clearCart());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(UITheme.CARD_BG);

        JPanel row1 = new JPanel(new BorderLayout());
        row1.setBackground(UITheme.CARD_BG);
        row1.add(removeBtn, BorderLayout.WEST);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.setBackground(UITheme.CARD_BG);
        row2.add(new JLabel("Order Type: "));
        row2.add(orderTypeCombo);

        JPanel row3 = new JPanel(new BorderLayout());
        row3.setBackground(UITheme.CARD_BG);
        row3.add(totalLabel, BorderLayout.CENTER);

        JPanel row4 = new JPanel(new GridLayout(1, 2, 10, 0));
        row4.setBackground(UITheme.CARD_BG);
        row4.add(checkoutBtn);
        row4.add(clearBtn);

        bottomPanel.add(comboSuggestionLabel);
        bottomPanel.add(Box.createVerticalStrut(8));
        bottomPanel.add(row1);
        bottomPanel.add(row2);
        bottomPanel.add(Box.createVerticalStrut(8));
        bottomPanel.add(row3);
        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(row4);

        rightCard.add(cartTitle, BorderLayout.NORTH);
        rightCard.add(orderScroll, BorderLayout.CENTER);
        rightCard.add(bottomPanel, BorderLayout.SOUTH);

        add(leftCard, BorderLayout.CENTER);
        add(rightCard, BorderLayout.EAST);

        loadMenu();
    }

    private void loadMenu() {
        menuModel.setRowCount(0);
        ItemDAO dao = new ItemDAO();
        currentMenuItems = dao.getAvailableItemsInStock();
        for (Item it : currentMenuItems) {
            menuModel.addRow(new Object[]{
                    it.getItemId(), it.getItemName(), it.getCategoryName(),
                    String.format("$%.2f", it.getPrice()), it.getStock()
            });
        }
    }

    private void addSelectedToCart() {
        int row = menuTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item from the menu.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Item selected = currentMenuItems.get(row);
        int qty = (Integer) qtySpinner.getValue();

        if (qty > selected.getStock()) {
            JOptionPane.showMessageDialog(this, "Not enough stock! Available: " + selected.getStock(), "Stock Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (OrderItem oi : cart) {
            if (oi.getItemId() == selected.getItemId()) {
                int newQty = oi.getQuantity() + qty;
                if (newQty > selected.getStock()) {
                    JOptionPane.showMessageDialog(this, "Not enough stock for total quantity!", "Stock Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                oi.setQuantity(newQty);
                oi.setSubtotal(newQty * oi.getUnitPrice());
                refreshOrderTable();
                return;
            }
        }

        OrderItem oi = new OrderItem();
        oi.setItemId(selected.getItemId());
        oi.setItemName(selected.getItemName());
        oi.setQuantity(qty);
        oi.setUnitPrice(selected.getPrice());
        oi.setSubtotal(qty * selected.getPrice());
        cart.add(oi);
        refreshOrderTable();
    }

    private void removeSelectedFromCart() {
        int row = orderTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an order item to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cart.remove(row);
        refreshOrderTable();
    }

    private void refreshOrderTable() {
        orderModel.setRowCount(0);
        double total = 0;
        for (OrderItem oi : cart) {
            orderModel.addRow(new Object[]{
                    oi.getItemName(), oi.getQuantity(),
                    String.format("$%.2f", oi.getUnitPrice()),
                    String.format("$%.2f", oi.getSubtotal())
            });
            total += oi.getSubtotal();
        }
        totalLabel.setText(String.format("Total: $%.2f", total));
        updateComboSuggestion();
    }

    private void updateComboSuggestion() {
        boolean hasCombo = false;
        for (OrderItem oi : cart) {
            Item it = findItemById(oi.getItemId());
            if (it != null && it.isCombo()) hasCombo = true;
        }
        if (!cart.isEmpty() && !hasCombo) {
            for (Item it : currentMenuItems) {
                if (it.isCombo()) {
                    comboSuggestionLabel.setText("💡 Suggestion: Add \"" + it.getItemName() + "\" for great value!");
                    return;
                }
            }
        }
        comboSuggestionLabel.setText(" ");
    }

    private Item findItemById(int id) {
        for (Item it : currentMenuItems) if (it.getItemId() == id) return it;
        return null;
    }

    private void clearCart() {
        cart.clear();
        refreshOrderTable();
    }

    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Order is empty! Add items first.", "Empty Order", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = 0;
        for (OrderItem oi : cart) total += oi.getSubtotal();

        StringBuilder sb = new StringBuilder();
        sb.append("---- ORDER SUMMARY ----\n");
        for (OrderItem oi : cart) {
            sb.append(String.format("%-15s x%d = $%.2f%n", oi.getItemName(), oi.getQuantity(), oi.getSubtotal()));
        }
        sb.append(String.format("%nTOTAL: $%.2f%n", total));

        Object[] options = {"Pay with Cash", "Pay with Card", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this, sb.toString(), "Confirm Payment",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) {
            JOptionPane.showMessageDialog(this, "Payment cancelled. You can try again.", "Payment Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String paymentMethod = (choice == 0) ? "Cash" : "Card";
        processOrder(total, paymentMethod);
    }

    private void processOrder(double total, String paymentMethod) {
        OrderDAO orderDAO = new OrderDAO();

        Order order = new Order();
        order.setUserId(currentUser.getUserId());
        order.setOrderDate(LocalDateTime.now().toString());
        order.setOrderType((String) orderTypeCombo.getSelectedItem());
        order.setTotalAmount(total);
        order.setPaymentStatus("PAID");

        int orderId = orderDAO.placeOrderTransactional(order, cart);
        if (orderId == -1) {
            JOptionPane.showMessageDialog(this, "Transaction failed! Order was not saved. Please try again.", "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new ReceiptDialog((Frame) SwingUtilities.getWindowAncestor(this),
                orderId, currentUser.getFullName(), (String) orderTypeCombo.getSelectedItem(),
                paymentMethod, new ArrayList<>(cart), total).setVisible(true);

        clearCart();
        loadMenu();
    }
}