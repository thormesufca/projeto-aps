package br.edu.ufca.audiencias.ui;

import br.edu.ufca.audiencias.models.Cliente;
import br.edu.ufca.audiencias.padroes.estruturais.facade.SistemaJuridicoFacade;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

public class RelatorioPanel extends JPanel {

    private final SistemaJuridicoFacade facade;

    private final JComboBox<String> tipoBox = new JComboBox<>(new String[]{
        "Processos por Cliente", "Audiências Mensais", "Valores Recebidos Mensais"
    });

    private final JComboBox<Cliente> clienteBox = new JComboBox<>();
    private final JLabel clienteLbl = new JLabel("Cliente:");

    private final JSpinner spMes = new JSpinner(new SpinnerNumberModel(
        LocalDate.now().getMonthValue(), 1, 12, 1));
    private final JSpinner spAno = new JSpinner(new SpinnerNumberModel(
        LocalDate.now().getYear(), 2020, 2100, 1));
    private final JLabel mesLbl  = new JLabel("Mês:");
    private final JLabel anoLbl  = new JLabel("Ano:");

    private final JTextArea resultado = new JTextArea();

    public RelatorioPanel(SistemaJuridicoFacade facade) {
        this.facade = facade;
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(Ui.pageTitle("Relatórios"), BorderLayout.NORTH);
        add(buildControles(), BorderLayout.CENTER);

        tipoBox.addActionListener(e -> atualizarVisibilidade());
        atualizarVisibilidade();
        carregarClientes();
    }

    private JPanel buildControles() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel params = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        params.setOpaque(false);
        params.setBorder(BorderFactory.createTitledBorder("Parâmetros"));

        JButton btnGerar = Ui.btnSuccess("Gerar Relatório");
        btnGerar.addActionListener(e -> gerar());

        params.add(new JLabel("Tipo:"));
        params.add(tipoBox);
        params.add(clienteLbl); params.add(clienteBox);
        params.add(mesLbl);     params.add(spMes);
        params.add(anoLbl);     params.add(spAno);
        params.add(btnGerar);

        resultado.setEditable(false);
        resultado.setFont(new Font("Consolas", Font.PLAIN, 12));
        resultado.setBackground(new Color(250, 250, 250));
        JScrollPane scroll = new JScrollPane(resultado);
        scroll.setBorder(BorderFactory.createTitledBorder("Resultado"));

        panel.add(params, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void atualizarVisibilidade() {
        boolean processosPorCliente = tipoBox.getSelectedIndex() == 0;
        clienteLbl.setVisible(processosPorCliente);
        clienteBox.setVisible(processosPorCliente);
        mesLbl.setVisible(!processosPorCliente);
        spMes.setVisible(!processosPorCliente);
        anoLbl.setVisible(!processosPorCliente);
        spAno.setVisible(!processosPorCliente);
    }

    private void carregarClientes() {
        clienteBox.removeAllItems();
        try { facade.listarClientes().forEach(clienteBox::addItem); }
        catch (Exception ignored) {}
    }

    private void gerar() {
        try {
            String rel;
            if (tipoBox.getSelectedIndex() == 0) {
                Cliente c = (Cliente) clienteBox.getSelectedItem();
                if (c == null) { Ui.aviso(this, "Selecione um cliente."); return; }
                rel = facade.gerarRelatorioProcessosPorCliente(c.getId());
            } else if(tipoBox.getSelectedIndex() == 1) {
                int mes = (int) spMes.getValue();
                int ano = (int) spAno.getValue();
                rel = facade.gerarRelatorioAudienciasMensais(mes, ano);
            } else {
                int mes = (int) spMes.getValue();
                int ano = (int) spAno.getValue();
                rel = facade.gerarRelatorioValoresRecebidosMensais(mes, ano);
            }
            resultado.setText(rel);
            resultado.setCaretPosition(0);
        } catch (Exception ex) {
            Ui.erro(this, "Erro ao gerar relatório: " + ex.getMessage());
        }
    }
}
