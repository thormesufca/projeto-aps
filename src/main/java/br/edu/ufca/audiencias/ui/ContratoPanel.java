package br.edu.ufca.audiencias.ui;

import br.edu.ufca.audiencias.models.Cliente;
import br.edu.ufca.audiencias.models.Contrato;
import br.edu.ufca.audiencias.models.Processo;
import br.edu.ufca.audiencias.models.enums.TipoValorContrato;
import br.edu.ufca.audiencias.padroes.estruturais.facade.SistemaJuridicoFacade;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

public class ContratoPanel extends JPanel {

    private final SistemaJuridicoFacade facade;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<Contrato> lista;

    private static final String[] COLS = {
            "ID", "Cliente", "Processo", "Tipo Valor", "Valor", "Data Contrat.", "Data Encerr.", "Descrição"
    };
    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ContratoPanel(SistemaJuridicoFacade facade) {
        this.facade = facade;
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        tableModel = new DefaultTableModel(COLS, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = Ui.buildTable(tableModel);

        add(Ui.pageTitle("Contratos"), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildToolbar(), BorderLayout.SOUTH);
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 8));
        bar.setOpaque(false);
        JButton btnNovo = Ui.btnPrimary("Novo");
        JButton btnEditar = Ui.btnSecondary("Editar");
        JButton btnExcluir = Ui.btnDanger("Excluir");
        JButton btnAtual = Ui.btnSecondary("Atualizar");

        btnNovo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> {
            Contrato c = selecionado();
            if (c != null)
                abrirFormulario(c);
        });
        btnExcluir.addActionListener(e -> excluir());
        btnAtual.addActionListener(e -> atualizar());

        bar.add(btnNovo);
        bar.add(btnEditar);
        bar.add(btnExcluir);
        bar.add(Box.createHorizontalStrut(10));
        bar.add(btnAtual);
        return bar;
    }

    public void atualizar() {
        tableModel.setRowCount(0);
        try {
            lista = facade.listarContratos();
            for (Contrato c : lista) {
                String cliente = c.getCliente() != null ? c.getCliente().getNome() : "-";
                String processo = c.getProcesso() != null ? MaskedField.formatarNumeroProcesso(c.getProcesso().getNumero()) : "-";
                String tipoValorLabel = c.getTipoValor() == TipoValorContrato.PERCENTUAL
                        ? "Percentual" : "Fixo";
                tableModel.addRow(new Object[] {
                        c.getId(), cliente, processo,
                        tipoValorLabel,
                        c.getTipoValor() == TipoValorContrato.PERCENTUAL
                                ? (c.getValor() != null ? c.getValor().toPlainString() + "%" : "-")
                                : (c.getValor() != null ? CURRENCY.format(c.getValor()) : "-"),
                        c.getDataContratacao() != null ? c.getDataContratacao().format(DATE_FMT) : "-",
                        c.getDataEncerramento() != null ? c.getDataEncerramento().format(DATE_FMT) : "-",
                        c.getDescricao()
                });
            }
        } catch (Exception ex) {
            Ui.erro(this, ex.getMessage());
        }
    }

    private void abrirFormulario(Contrato existente) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                existente == null ? "Novo Contrato" : "Editar Contrato",
                Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(840, 460);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 16, 8, 16));
        GridBagConstraints gbc = Ui.gbc();

        // ── Cliente (campo de busca) ─────────────────────────────────────────
        Cliente[] clienteSelecionado = { existente != null ? existente.getCliente() : null };
        JTextField fClienteDisplay = new JTextField(
                clienteSelecionado[0] != null
                        ? clienteSelecionado[0].getNome() + " — " + clienteSelecionado[0].getIdentificador()
                        : "");
        fClienteDisplay.setEditable(false);
        fClienteDisplay.setPreferredSize(new Dimension(260, 26));
        JButton btnBuscarCliente = Ui.btnSecondary("Buscar...");

        JPanel clientePanel = new JPanel(new BorderLayout(4, 0));
        clientePanel.add(fClienteDisplay, BorderLayout.CENTER);
        clientePanel.add(btnBuscarCliente, BorderLayout.EAST);

        btnBuscarCliente.addActionListener(e -> {
            Cliente escolhido = abrirSeletorCliente(dlg);
            if (escolhido != null) {
                clienteSelecionado[0] = escolhido;
                fClienteDisplay.setText(escolhido.getNome() + " — " + escolhido.getIdentificador());
            }
        });

        // ── Processo (campo de busca) ────────────────────────────────────────
        Processo[] processoSelecionado = { existente != null ? existente.getProcesso() : null };
        JTextField fProcessoDisplay = new JTextField(
                processoSelecionado[0] != null
                        ? MaskedField.formatarNumeroProcesso(processoSelecionado[0].getNumero())
                        : "");
        fProcessoDisplay.setEditable(false);
        fProcessoDisplay.setPreferredSize(new Dimension(260, 26));
        JButton btnBuscarProcesso = Ui.btnSecondary("Buscar...");

        JPanel processoPanel = new JPanel(new BorderLayout(4, 0));
        processoPanel.add(fProcessoDisplay, BorderLayout.CENTER);
        processoPanel.add(btnBuscarProcesso, BorderLayout.EAST);

        btnBuscarProcesso.addActionListener(e -> {
            Processo escolhido = abrirSeletorProcesso(dlg);
            if (escolhido != null) {
                processoSelecionado[0] = escolhido;
                fProcessoDisplay.setText(MaskedField.formatarNumeroProcesso(escolhido.getNumero()));
            }
        });

        // ── Tipo de valor ────────────────────────────────────────────────────
        boolean isPercentual = existente != null
                && existente.getTipoValor() == TipoValorContrato.PERCENTUAL;
        JRadioButton rbFixo       = new JRadioButton("Valor fixo (R$)", !isPercentual);
        JRadioButton rbPercentual = new JRadioButton("Percentual da condenação (%)", isPercentual);
        ButtonGroup bgTipo = new ButtonGroup();
        bgTipo.add(rbFixo);
        bgTipo.add(rbPercentual);
        JPanel tipoValorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tipoValorPanel.setOpaque(false);
        tipoValorPanel.add(rbFixo);
        tipoValorPanel.add(rbPercentual);

        // ── Demais campos ───────────────────────────────────────────────────
        JTextField fValor = new JTextField(existente != null && existente.getValor() != null
                ? existente.getValor().toPlainString()
                : "");

        // Campos de data com formato dd/MM/yyyy
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        JFormattedTextField fDataContratacao = new JFormattedTextField(sdf);
        fDataContratacao.setValue(existente != null && existente.getDataContratacao() != null
                ? new java.util.Date(java.sql.Date.valueOf(existente.getDataContratacao()).getTime())
                : new java.util.Date());
        fDataContratacao.setColumns(12);

        JFormattedTextField fDataEncerramento = new JFormattedTextField(sdf);
        if (existente != null && existente.getDataEncerramento() != null) {
            fDataEncerramento
                    .setValue(new java.util.Date(java.sql.Date.valueOf(existente.getDataEncerramento()).getTime()));
        }
        fDataEncerramento.setColumns(12);

        JTextField fDescricao = new JTextField(existente != null ? existente.getDescricao() : "");
        JTextArea fObservacao = new JTextArea(existente != null ? existente.getObservacoes() : "", 3, 20);

        JLabel lblValor = new JLabel(isPercentual ? "Percentual (%):" : "Valor (R$):");
        rbFixo.addActionListener(e       -> lblValor.setText("Valor (R$):"));
        rbPercentual.addActionListener(e -> lblValor.setText("Percentual (%):"));

        int r = 0;
        Ui.addFormRow(form, gbc, r++, "Cliente *:", clientePanel);
        Ui.addFormRow(form, gbc, r++, "Processo:", processoPanel);
        Ui.addFormRow(form, gbc, r++, "Tipo de cobrança:", tipoValorPanel);
        gbc.gridy = r++; gbc.gridx = 0; form.add(lblValor, gbc);
        gbc.gridx = 1; form.add(fValor, gbc);
        Ui.addFormRow(form, gbc, r++, "Data Contratação (dd/MM/yyyy) *:", fDataContratacao);
        Ui.addFormRow(form, gbc, r++, "Data Encerramento (dd/MM/yyyy):", fDataEncerramento);
        Ui.addFormRow(form, gbc, r++, "Descrição:", fDescricao);
        Ui.addFormRow(form, gbc, r++, "Observações:", new JScrollPane(fObservacao));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = Ui.btnSuccess("Salvar");
        JButton btnCancel = new JButton("Cancelar");
        btns.add(btnCancel);
        btns.add(btnSalvar);
        btnCancel.addActionListener(e -> dlg.dispose());
        btnSalvar.addActionListener(e -> {
            if (fDataContratacao.getText().isBlank()) {
                Ui.aviso(dlg, "Data de contratação é obrigatória.");
                return;
            }
            try {
                SimpleDateFormat sdfParse = new SimpleDateFormat("dd/MM/yyyy");
                java.util.Date dateC = sdfParse.parse(fDataContratacao.getText().trim());
                LocalDate dataC = new java.sql.Date(dateC.getTime()).toLocalDate();

                LocalDate dataE = null;
                if (fDataEncerramento.getValue() != null) {
                    java.util.Date dateE = (java.util.Date) fDataEncerramento.getValue();
                    dataE = new java.sql.Date(dateE.getTime()).toLocalDate();
                }
                BigDecimal valor = fValor.getText().isBlank() ? BigDecimal.ZERO
                        : new BigDecimal(fValor.getText().trim().replace(",", "."));
                Cliente cliente = clienteSelecionado[0];
                if (cliente == null) {
                    Ui.aviso(dlg, "Selecione um cliente.");
                    return;
                }

                Contrato c = existente != null ? existente : new Contrato();
                c.setCliente(cliente);
                c.setProcesso(processoSelecionado[0]);
                c.setTipoValor(rbPercentual.isSelected()
                        ? TipoValorContrato.PERCENTUAL : TipoValorContrato.FIXO);
                c.setValor(valor);
                c.setDataContratacao(dataC);
                c.setDataEncerramento(dataE);
                c.setDescricao(fDescricao.getText().trim());
                c.setObservacoes(fObservacao.getText().trim());

                if (existente == null)
                    facade.salvarContrato(c);
                else
                    facade.atualizarContrato(c);
                dlg.dispose();
                atualizar();
            } catch (ParseException ex) {
                Ui.aviso(dlg, "Data inválida. Use o formato dd/MM/yyyy.");
            } catch (NumberFormatException ex) {
                Ui.aviso(dlg, "Valor inválido. Use ponto como separador decimal.");
            } catch (Exception ex) {
                Ui.erro(dlg, "Erro ao salvar: " + ex.getMessage());
            }
        });

        dlg.add(new JScrollPane(form), BorderLayout.CENTER);
        dlg.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private Processo abrirSeletorProcesso(JDialog owner) {
        JDialog dlg = new JDialog(owner, "Selecionar Processo", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(600, 400);
        dlg.setLocationRelativeTo(owner);
        dlg.setLayout(new BorderLayout(0, 0));

        // Barra de busca
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 8));
        JTextField fBusca = new JTextField(24);
        JButton btnBuscar = Ui.btnPrimary("Buscar");
        JButton btnTodos = Ui.btnSecondary("Listar Todos");
        topBar.add(new JLabel("Número ou título:"));
        topBar.add(fBusca);
        topBar.add(btnBuscar);
        topBar.add(btnTodos);

        DefaultTableModel m = new DefaultTableModel(
                new String[] { "ID", "Número", "Tipo", "Status", "Título" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable t = Ui.buildTable(m);

        Runnable buscarTodos = () -> {
            m.setRowCount(0);
            facade.listarProcessos().forEach(p -> m.addRow(new Object[] {
                    p.getId(), MaskedField.formatarNumeroProcesso(p.getNumero()), p.getTipo(), p.getStatus()}));
        };

        Runnable buscarPorTermo = () -> {
            String termo = fBusca.getText().trim();
            if (termo.isBlank()) {
                buscarTodos.run();
                return;
            }
            m.setRowCount(0);
            facade.buscarProcessosPorNumero(termo).forEach(p -> m.addRow(new Object[] {
                    p.getId(), MaskedField.formatarNumeroProcesso(p.getNumero()), p.getTipo(), p.getStatus()
            }));
        };

        btnBuscar.addActionListener(e -> buscarPorTermo.run());
        btnTodos.addActionListener(e -> buscarTodos.run());
        fBusca.addActionListener(e -> buscarPorTermo.run());
        buscarTodos.run();

        // Resultado final
        Processo[] resultado = { null };

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 8));
        JButton btnSelecionar = Ui.btnSuccess("Selecionar");
        JButton btnCancelar = new JButton("Cancelar");
        botoes.add(btnCancelar);
        botoes.add(btnSelecionar);

        btnSelecionar.addActionListener(e -> {
            int row = t.getSelectedRow();
            if (row < 0) {
                Ui.aviso(dlg, "Selecione um processo.");
                return;
            }
            Long id = (Long) m.getValueAt(t.convertRowIndexToModel(row), 0);
            resultado[0] = facade.buscarProcessoPorId(id);
            dlg.dispose();
        });
        btnCancelar.addActionListener(e -> dlg.dispose());

        // Duplo clique também seleciona
        t.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent ev) {
                if (ev.getClickCount() == 2 && t.getSelectedRow() >= 0) {
                    btnSelecionar.doClick();
                }
            }
        });

        dlg.add(topBar, BorderLayout.NORTH);
        dlg.add(new JScrollPane(t), BorderLayout.CENTER);
        dlg.add(botoes, BorderLayout.SOUTH);
        dlg.setVisible(true);

        return resultado[0];
    }

    private Cliente abrirSeletorCliente(JDialog owner) {
        JDialog dlg = new JDialog(owner, "Selecionar Cliente", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(620, 420);
        dlg.setLocationRelativeTo(owner);
        dlg.setLayout(new BorderLayout(0, 0));

        // Barra de busca
        JPanel topBar = new JPanel(new java.awt.GridBagLayout());
        topBar.setBorder(new EmptyBorder(8, 8, 4, 8));
        java.awt.GridBagConstraints g = new java.awt.GridBagConstraints();
        g.insets = new java.awt.Insets(2, 4, 2, 4);
        g.fill = java.awt.GridBagConstraints.HORIZONTAL;

        JTextField fBuscaNome = new JTextField(16);
        JTextField fBuscaCpfCnpj = MaskedField.cpfCnpj("");

        g.gridx = 0; g.gridy = 0; g.weightx = 0; topBar.add(new JLabel("Nome:"), g);
        g.gridx = 1; g.weightx = 1; topBar.add(fBuscaNome, g);
        g.gridx = 2; g.weightx = 0; topBar.add(new JLabel("CPF/CNPJ:"), g);
        g.gridx = 3; g.weightx = 1; topBar.add(fBuscaCpfCnpj, g);

        JButton btnBuscar = Ui.btnPrimary("Buscar");
        JButton btnTodos  = Ui.btnSecondary("Listar Todos");
        g.gridx = 4; g.weightx = 0; topBar.add(btnBuscar, g);
        g.gridx = 5; topBar.add(btnTodos, g);

        DefaultTableModel m = new DefaultTableModel(
                new String[] { "ID", "Nome", "CPF/CNPJ", "Tipo", "Telefone" }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = Ui.buildTable(m);

        Runnable buscarTodos = () -> {
            m.setRowCount(0);
            facade.listarClientes().forEach(c2 -> m.addRow(new Object[] {
                    c2.getId(), c2.getNome(), c2.getIdentificador(), c2.getTipoPessoa(), c2.getTelefone()
            }));
        };

        Runnable buscar = () -> {
            String nome    = fBuscaNome.getText().trim();
            String cpfCnpj = MaskedField.valorSemMascara(fBuscaCpfCnpj);
            m.setRowCount(0);
            if (!cpfCnpj.isBlank()) {
                // Filtro por CPF/CNPJ (client-side)
                facade.listarClientes().stream()
                        .filter(c2 -> MaskedField.soDigitos(c2.getIdentificador()).contains(cpfCnpj))
                        .forEach(c2 -> m.addRow(new Object[] {
                                c2.getId(), c2.getNome(), c2.getIdentificador(),
                                c2.getTipoPessoa(), c2.getTelefone()
                        }));
            } else if (!nome.isBlank()) {
                facade.buscarClientesPorNome(nome).forEach(c2 -> m.addRow(new Object[] {
                        c2.getId(), c2.getNome(), c2.getIdentificador(),
                        c2.getTipoPessoa(), c2.getTelefone()
                }));
            } else {
                buscarTodos.run();
            }
        };

        btnBuscar.addActionListener(e -> buscar.run());
        btnTodos.addActionListener(e -> buscarTodos.run());
        fBuscaNome.addActionListener(e -> buscar.run());
        fBuscaCpfCnpj.addActionListener(e -> buscar.run());
        buscarTodos.run();

        Cliente[] resultado = { null };

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 8));
        JButton btnSelecionar = Ui.btnSuccess("Selecionar");
        JButton btnCancelar   = new JButton("Cancelar");
        botoes.add(btnCancelar);
        botoes.add(btnSelecionar);

        btnSelecionar.addActionListener(e -> {
            int row = t.getSelectedRow();
            if (row < 0) { Ui.aviso(dlg, "Selecione um cliente."); return; }
            Long id = (Long) m.getValueAt(t.convertRowIndexToModel(row), 0);
            resultado[0] = facade.buscarClientePorId(id);
            dlg.dispose();
        });
        btnCancelar.addActionListener(e -> dlg.dispose());

        t.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent ev) {
                if (ev.getClickCount() == 2 && t.getSelectedRow() >= 0)
                    btnSelecionar.doClick();
            }
        });

        dlg.add(topBar, BorderLayout.NORTH);
        dlg.add(new JScrollPane(t), BorderLayout.CENTER);
        dlg.add(botoes, BorderLayout.SOUTH);
        dlg.setVisible(true);

        return resultado[0];
    }

    private void excluir() {
        Contrato c = selecionado();
        if (c == null)
            return;
        if (Ui.confirmar(this, "Excluir contrato #" + c.getId() + "?")) {
            try {
                facade.deletarContrato(c.getId());
                atualizar();
            } catch (Exception ex) {
                Ui.erro(this, ex.getMessage());
            }
        }
    }

    private Contrato selecionado() {
        int row = table.getSelectedRow();
        if (row < 0) {
            Ui.aviso(this, "Selecione um contrato.");
            return null;
        }
        return lista.get(table.convertRowIndexToModel(row));
    }
}
