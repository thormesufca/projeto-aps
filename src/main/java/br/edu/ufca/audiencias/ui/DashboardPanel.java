package br.edu.ufca.audiencias.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import br.edu.ufca.audiencias.padroes.estruturais.facade.SistemaJuridicoFacade;

import java.awt.*;

public class DashboardPanel extends JPanel {

    private final SistemaJuridicoFacade facade;

    private final JLabel lblClientes   = stat("0");
    private final JLabel lblAdvogados  = stat("0");
    private final JLabel lblProcessos  = stat("0");
    private final JLabel lblAudiencias = stat("0");
    private final JLabel lblContratos  = stat("0");

    public DashboardPanel(SistemaJuridicoFacade facade) {
        this.facade = facade;
        setBackground(MainWindow.BG_MAIN);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(buildTitle(), BorderLayout.NORTH);
        add(buildCards(), BorderLayout.CENTER);
    }

    private JLabel buildTitle() {
        JLabel lbl = new JLabel("Dashboard");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 22f));
        lbl.setForeground(MainWindow.TITLE_COLOR);
        lbl.setBorder(new EmptyBorder(0, 0, 20, 0));
        return lbl;
    }

    private JPanel buildCards() {
        JPanel cards = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
        cards.setOpaque(false);
        cards.add(card("Clientes",    lblClientes,   new Color(21, 101, 192)));
        cards.add(card("Advogados",   lblAdvogados,  new Color(46, 125, 50)));
        cards.add(card("Contratos",   lblContratos,  new Color(123, 31, 162)));
        cards.add(card("Processos",   lblProcessos,  new Color(230, 81, 0)));
        cards.add(card("Audiências",  lblAudiencias, new Color(198, 40, 40)));
        return cards;
    }

    private JPanel card(String titulo, JLabel valorLabel, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(197, 202, 233), 1, true),
            new EmptyBorder(20, 28, 20, 28)
        ));
        card.setPreferredSize(new Dimension(170, 120));

        valorLabel.setForeground(accentColor);
        valorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tituloLbl = new JLabel(titulo);
        tituloLbl.setForeground(new Color(84, 110, 122));
        tituloLbl.setFont(tituloLbl.getFont().deriveFont(12f));
        tituloLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(valorLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(tituloLbl);
        card.add(Box.createVerticalGlue());
        return card;
    }

    private static JLabel stat(String value) {
        JLabel lbl = new JLabel(value, SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 36f));
        return lbl;
    }

    public void atualizar() {
        try {
            lblClientes.setText(String.valueOf(facade.contarClientes()));
            lblAdvogados.setText(String.valueOf(facade.contarAdvogados()));
            lblContratos.setText(String.valueOf(facade.contarContratos()));
            lblProcessos.setText(String.valueOf(facade.contarProcessos()));
            lblAudiencias.setText(String.valueOf(facade.contarAudiencias()));
        } catch (Exception ex) {
        }
    }
}
