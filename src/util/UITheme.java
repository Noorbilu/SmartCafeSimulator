package util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UITheme {
    // Café Color Palette
    public static final Color SIDEBAR_BG = new Color(62, 39, 35);      // Dark coffee brown
    public static final Color SIDEBAR_HOVER = new Color(85, 55, 48);
    public static final Color SIDEBAR_ACTIVE = new Color(255, 143, 0);  // Orange accent
    public static final Color PRIMARY = new Color(109, 76, 65);         // Medium brown
    public static final Color ACCENT = new Color(255, 143, 0);          // Orange
    public static final Color ACCENT_DARK = new Color(230, 120, 0);
    public static final Color BACKGROUND = new Color(250, 247, 242);    // Cream
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_DARK = new Color(40, 30, 28);
    public static final Color TEXT_MUTED = new Color(120, 110, 105);
    public static final Color TEXT_LIGHT = Color.WHITE;
    public static final Color SUCCESS = new Color(56, 142, 60);
    public static final Color DANGER = new Color(211, 47, 47);
    public static final Color WARNING = new Color(255, 160, 0);

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_STAT_VALUE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_STAT_LABEL = new Font("Segoe UI", Font.PLAIN, 12);

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(ACCENT_DARK); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(ACCENT); }
        });
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(new Color(230, 225, 220));
        btn.setForeground(TEXT_DARK);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(DANGER);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(SIDEBAR_BG);
        btn.setBorder(new EmptyBorder(14, 24, 14, 24));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(220, 48));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn.getBackground() != SIDEBAR_ACTIVE) btn.setBackground(SIDEBAR_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn.getBackground() != SIDEBAR_ACTIVE) btn.setBackground(SIDEBAR_BG);
            }
        });
        return btn;
    }

    public static JPanel createStatCard(String label, String value, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 225, 220), 1),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(accentColor);
        accentBar.setPreferredSize(new Dimension(5, 0));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(CARD_BG);
        textPanel.setBorder(new EmptyBorder(0, 15, 0, 0));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(FONT_STAT_VALUE);
        valueLabel.setForeground(TEXT_DARK);

        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(FONT_STAT_LABEL);
        labelLabel.setForeground(TEXT_MUTED);

        textPanel.add(valueLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(labelLabel);

        card.add(accentBar, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(32);
        table.setFont(FONT_LABEL);
        table.setForeground(TEXT_DARK);
        table.setSelectionBackground(new Color(255, 224, 178));
        table.setSelectionForeground(TEXT_DARK);
        table.setGridColor(new Color(235, 230, 225));
        table.setShowGrid(true);
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setBackground(new Color(245, 240, 235));
        table.getTableHeader().setForeground(TEXT_DARK);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
    }

    public static JPanel createHeaderBar(String title, String userInfo, Runnable onLogout) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 225, 220)),
                new EmptyBorder(15, 25, 15, 25)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_HEADER);
        titleLabel.setForeground(TEXT_DARK);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        right.setBackground(CARD_BG);

        JLabel userLabel = new JLabel(userInfo);
        userLabel.setFont(FONT_LABEL);
        userLabel.setForeground(TEXT_MUTED);

        JButton logoutBtn = createDangerButton("Logout");
        logoutBtn.addActionListener(e -> onLogout.run());

        right.add(userLabel);
        right.add(logoutBtn);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }
}