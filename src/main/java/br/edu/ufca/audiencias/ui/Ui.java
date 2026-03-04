package br.edu.ufca.audiencias.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Utilitários de UI compartilhados entre os painéis.
 */
public final class Ui {

    private Ui() {}

    // ─── Botões ──────────────────────────────────────────────────────────────

    public static JButton btnPrimary(String label) {
        return styledBtn(label, new Color(21, 101, 192), Color.WHITE);
    }

    public static JButton btnSuccess(String label) {
        return styledBtn(label, new Color(46, 125, 50), Color.WHITE);
    }

    public static JButton btnSecondary(String label) {
        return styledBtn(label, new Color(84, 110, 122), Color.WHITE);
    }

    public static JButton btnDanger(String label) {
        return styledBtn(label, new Color(198, 40, 40), Color.WHITE);
    }

    public static JButton btnWarning(String label) {
        return styledBtn(label, new Color(230, 81, 0), Color.WHITE);
    }

    private static JButton styledBtn(String label, Color bg, Color fg) {
        JButton btn = new JButton(label);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 12f));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return btn;
    }

    // ─── Diálogos ────────────────────────────────────────────────────────────

    public static void erro(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public static void aviso(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirmar(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirmação",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // ─── Formulário ──────────────────────────────────────────────────────────

    public static GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 4, 5, 4);
        g.fill = GridBagConstraints.HORIZONTAL;
        return g;
    }

    public static void addFormRow(JPanel panel, GridBagConstraints gbc, int row,
                                   String labelText, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    public static JTable buildTable(javax.swing.table.DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(24);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.getTableHeader().setBackground(new Color(55, 71, 79));   // #37474f — cinza azulado escuro
        t.getTableHeader().setForeground(new Color(144, 202, 249)); // #90CAF9 — azul claro
        t.setFillsViewportHeight(true);
        t.setAutoCreateRowSorter(true);
        return t;
    }

    public static JLabel pageTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 22f));
        lbl.setForeground(MainWindow.TITLE_COLOR);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        return lbl;
    }
}
