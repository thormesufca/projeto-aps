package br.edu.ufca.audiencias.ui;

import br.edu.ufca.audiencias.models.Cliente;
import br.edu.ufca.audiencias.models.enums.TipoPessoa;
import br.edu.ufca.audiencias.padroes.estruturais.facade.SistemaJuridicoFacade;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Painel de CRUD de Clientes.
 */
public class ClientePanel extends JPanel {

    private final SistemaJuridicoFacade facade;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<Cliente> clienteList;

    private static final String[] COLS = {"ID", "Nome", "Identificador", "Telefone", "Tipo", "E-mail", "Endereço"};

    public ClientePanel(SistemaJuridicoFacade facade) {
        this.facade = facade;
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        tableModel = new DefaultTableModel(COLS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = buildTable(tableModel);

        add(buildHeader(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildToolbar(), BorderLayout.SOUTH);
    }

    private JLabel buildHeader() {
        JLabel lbl = new JLabel("Clientes");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 22f));
        lbl.setForeground(MainWindow.TITLE_COLOR);
        lbl.setBorder(new EmptyBorder(0, 0, 12, 0));
        return lbl;
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 8));
        bar.setOpaque(false);
        JButton btnNovo    = Ui.btnPrimary("Novo");
        JButton btnEditar  = Ui.btnSecondary("Editar");
        JButton btnExcluir = Ui.btnDanger("Excluir");
        JButton btnAtual   = Ui.btnSecondary("Atualizar");

        btnNovo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> {
            Cliente c = selecionado();
            if (c != null) abrirFormulario(c);
        });
        btnExcluir.addActionListener(e -> excluir());
        btnAtual.addActionListener(e -> atualizar());

        bar.add(btnNovo); bar.add(btnEditar); bar.add(btnExcluir);
        bar.add(Box.createHorizontalStrut(10)); bar.add(btnAtual);
        return bar;
    }

    public void atualizar() {
        tableModel.setRowCount(0);
        try {
            clienteList = facade.listarClientes();
            for (Cliente c : clienteList) {
                tableModel.addRow(new Object[]{
                    c.getId(), c.getNome(), c.getIdentificador(), c.getTelefone(),
                    c.getTipoPessoa(), c.getEmail(), c.getEndereco()
                });
            }
        } catch (Exception ex) {
            Ui.erro(this, "Erro ao carregar clientes: " + ex.getMessage());
        }
    }

    private void abrirFormulario(Cliente existente) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
            existente == null ? "Novo Cliente" : "Editar Cliente",
            Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(460, 400);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 16, 8, 16));
        GridBagConstraints gbc = Ui.gbc();

        JTextField fNome  = new JTextField(existente != null ? existente.getNome() : "");
        JTextField fIdent = MaskedField.cpfCnpj(existente != null ? existente.getIdentificador() : "");
        JTextField fTel   = MaskedField.telefone(existente != null ? existente.getTelefone() : "");
        JTextField fEmail = new JTextField(existente != null ? existente.getEmail() : "");
        JTextField fEnder = new JTextField(existente != null ? existente.getEndereco() : "");
        JComboBox<TipoPessoa> fTipo = new JComboBox<>(TipoPessoa.values());
        if (existente != null && existente.getTipoPessoa() != null)
            fTipo.setSelectedItem(existente.getTipoPessoa());

        int r = 0;
        Ui.addFormRow(form, gbc, r++, "Nome *:",        fNome);
        Ui.addFormRow(form, gbc, r++, "CPF/CNPJ *:",    fIdent);
        Ui.addFormRow(form, gbc, r++, "Telefone:",       fTel);
        Ui.addFormRow(form, gbc, r++, "Tipo Pessoa:",    fTipo);
        Ui.addFormRow(form, gbc, r++, "E-mail:",         fEmail);
        Ui.addFormRow(form, gbc, r++, "Endereço:",       fEnder);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar  = Ui.btnSuccess("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        btns.add(btnCancelar); btns.add(btnSalvar);

        btnCancelar.addActionListener(e -> dlg.dispose());
        btnSalvar.addActionListener(e -> {
            if (fNome.getText().isBlank() || MaskedField.valorSemMascara(fIdent).isBlank()) {
                Ui.aviso(dlg, "Nome e CPF/CNPJ são obrigatórios.");
                return;
            }
            if (!MaskedField.validarEmail(fEmail.getText())) {
                Ui.aviso(dlg, "E-mail inválido."); return;
            }
            try {
                if (existente == null) {
                    Cliente c = new Cliente();
                    preencherCliente(c, fNome, fIdent, fTel, fEmail, fEnder, fTipo);
                    facade.salvarCliente(c);
                } else {
                    preencherCliente(existente, fNome, fIdent, fTel, fEmail, fEnder, fTipo);
                    facade.atualizarCliente(existente);
                }
                dlg.dispose();
                atualizar();
            } catch (Exception ex) {
                Ui.erro(dlg, "Erro ao salvar: " + ex.getMessage());
            }
        });

        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void preencherCliente(Cliente c, JTextField fNome, JTextField fIdent,
                                   JTextField fTel, JTextField fEmail, JTextField fEnder,
                                   JComboBox<TipoPessoa> fTipo) {
        c.setNome(fNome.getText().trim());
        c.setIdentificador(fIdent.getText().trim());
        c.setTelefone(fTel.getText().trim());
        c.setEmail(fEmail.getText().trim());
        c.setEndereco(fEnder.getText().trim());
        c.setTipoPessoa((TipoPessoa) fTipo.getSelectedItem());
    }

    private void excluir() {
        Cliente c = selecionado();
        if (c == null) return;
        if (Ui.confirmar(this, "Excluir cliente \"" + c.getNome() + "\"?")) {
            try {
                facade.deletarCliente(c.getId());
                atualizar();
            } catch (Exception ex) {
                Ui.erro(this, "Erro ao excluir: " + ex.getMessage());
            }
        }
    }

    private Cliente selecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { Ui.aviso(this, "Selecione um cliente na tabela."); return null; }
        return clienteList.get(table.convertRowIndexToModel(row));
    }

    private static JTable buildTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(24);
        t.getTableHeader().setBackground(new Color(55, 71, 79));
        t.getTableHeader().setForeground(MainWindow.TITLE_COLOR);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.getColumnModel().getColumn(0).setMaxWidth(60);
        t.setAutoCreateRowSorter(true);
        return t;
    }
}
