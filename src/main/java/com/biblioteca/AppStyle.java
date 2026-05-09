package com.biblioteca;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class AppStyle {

    public static final Color BG = new Color(238, 243, 252);
    public static final Color SURFACE = new Color(255, 255, 255);
    public static final Color PRIMARY = new Color(26, 77, 140);
    public static final Color PRIMARY_SOFT = new Color(219, 232, 249);
    public static final Color SUCCESS = new Color(28, 125, 84);
    public static final Color WARNING = new Color(180, 54, 54);
    public static final Color TEXT_MAIN = new Color(27, 37, 52);
    public static final Color TEXT_MUTED = new Color(92, 104, 126);

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);

    private AppStyle() {
    }

    public static Border cardBorder() {
        return new CompoundBorder(new LineBorder(new Color(208, 220, 239), 1, true), new EmptyBorder(12, 12, 12, 12));
    }

    public static JPanel buildRootPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));
        return panel;
    }

    public static JPanel buildSurfacePanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(SURFACE);
        panel.setBorder(cardBorder());
        return panel;
    }

    public static void styleTitle(JLabel label) {
        label.setFont(FONT_TITLE);
        label.setForeground(PRIMARY);
    }

    public static void styleSubtitle(JLabel label) {
        label.setFont(FONT_SUBTITLE);
        label.setForeground(TEXT_MUTED);
    }

    public static void styleBodyLabel(JLabel label) {
        label.setFont(FONT_BODY);
        label.setForeground(TEXT_MAIN);
    }

    public static void styleTextField(JTextField field) {
        field.setFont(FONT_BODY);
        field.setBorder(new CompoundBorder(new LineBorder(new Color(193, 208, 231), 1, true), new EmptyBorder(6, 8, 6, 8)));
    }

    public static void stylePrimaryButton(JButton button) {
        button.setFont(FONT_BUTTON);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBackground(PRIMARY);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(new LineBorder(new Color(18, 63, 120), 1, true), new EmptyBorder(6, 12, 6, 12)));
    }

    public static void styleSecondaryButton(JButton button) {
        button.setFont(FONT_BUTTON);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBackground(PRIMARY_SOFT);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(new LineBorder(new Color(168, 194, 227), 1, true), new EmptyBorder(6, 12, 6, 12)));
    }

    public static void styleDangerButton(JButton button) {
        button.setFont(FONT_BUTTON);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBackground(new Color(252, 232, 232));
        button.setForeground(WARNING);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(new LineBorder(new Color(232, 174, 174), 1, true), new EmptyBorder(6, 12, 6, 12)));
    }

    public static void styleToolbar(JToolBar toolBar) {
        toolBar.setFloatable(false);
        toolBar.setBorder(new CompoundBorder(new LineBorder(new Color(204, 218, 239), 1, true), new EmptyBorder(6, 8, 6, 8)));
        toolBar.setBackground(new Color(248, 251, 255));
    }

    public static JButton buildToolbarButton(String text) {
        JButton button = new JButton(text);
        styleSecondaryButton(button);
        return button;
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setForeground(TEXT_MAIN);
        table.setRowHeight(26);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(226, 234, 245));
        table.setSelectionBackground(new Color(220, 236, 255));
        table.setSelectionForeground(TEXT_MAIN);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(233, 241, 252));
        header.setForeground(new Color(33, 56, 93));
    }

    public static void styleTitledBorder(JScrollPane scroll, String title) {
        scroll.setBorder(BorderFactory.createTitledBorder(cardBorder(), title));
    }
}
