package br.edu.ufca.audiencias.ui;

import br.edu.ufca.audiencias.models.Advogado;
import br.edu.ufca.audiencias.models.InscricaoOab;
import br.edu.ufca.audiencias.models.enums.TipoPessoa;
import br.edu.ufca.audiencias.padroes.estruturais.facade.SistemaJuridicoFacade;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class AdvogadoPanel extends JPanel {

    private final SistemaJuridicoFacade facade;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final String[] estados = { "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MS", "MT", "MG", "PA", "PB", "PR",
            "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO" };
    private List<Advogado> lista;

    private static final String[] COLS = {"ID", "Nome", "OAB(s)", "Especialidades", "Identificador", "Telefone", "E-mail"};

    public AdvogadoPanel(SistemaJuridicoFacade facade) {
        this.facade = facade;
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        tableModel = new DefaultTableModel(COLS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = Ui.buildTable(tableModel);

        add(Ui.pageTitle("Advogados"), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildToolbar(), BorderLayout.SOUTH);
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 8));
        bar.setOpaque(false);
        JButton btnNovo    = Ui.btnPrimary("Novo");
        JButton btnEditar  = Ui.btnSecondary("Editar");
        JButton btnExcluir = Ui.btnDanger("Excluir");
        JButton btnAtual   = Ui.btnSecondary("Atualizar");

        btnNovo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> { Advogado a = selecionado(); if (a != null) abrirFormulario(a); });
        btnExcluir.addActionListener(e -> excluir());
        btnAtual.addActionListener(e -> atualizar());

        bar.add(btnNovo); bar.add(btnEditar); bar.add(btnExcluir);
        bar.add(Box.createHorizontalStrut(10)); bar.add(btnAtual);
        return bar;
    }

    public void atualizar() {
        tableModel.setRowCount(0);
        try {
            lista = facade.listarAdvogados();
            for (Advogado a : lista) {
                String oabsStr = a.getInscricoesOab().stream()
                        .map(InscricaoOab::toString)
                        .collect(Collectors.joining(", "));
                tableModel.addRow(new Object[]{
                    a.getId(), a.getNome(), oabsStr, String.join(", ", a.getEspecialidades()),
                    a.getIdentificador(), a.getTelefone(), a.getEmail()
                });
            }
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }

    private void abrirFormulario(Advogado existente) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
            existente == null ? "Novo Advogado" : "Editar Advogado",
            Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(520, 500);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 16, 8, 16));
        GridBagConstraints gbc = Ui.gbc();

        JTextField fNome  = new JTextField(existente != null ? existente.getNome() : "");
        JTextField fEspec = new JTextField(existente != null ? String.join(", ", existente.getEspecialidades()) : "");
        JTextField fIdent = MaskedField.cpfCnpj(existente != null ? existente.getIdentificador() : "");
        JTextField fTel   = MaskedField.telefone(existente != null ? existente.getTelefone() : "");
        JTextField fEmail = new JTextField(existente != null ? existente.getEmail() : "");

        // --- Painel de inscrições OAB ---
        DefaultListModel<InscricaoOab> oabListModel = new DefaultListModel<>();
        if (existente != null) existente.getInscricoesOab().forEach(oabListModel::addElement);

        JList<InscricaoOab> jListOab = new JList<>(oabListModel);
        jListOab.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane oabScroll = new JScrollPane(jListOab);
        oabScroll.setPreferredSize(new Dimension(0, 80));

        JComboBox<String> cEstadoOab = new JComboBox<>(estados);
        cEstadoOab.setSelectedIndex(5); // CE por padrão
        JTextField fNumOab = new JTextField(8);
        JButton btnAddOab = Ui.btnPrimary("+ Adicionar");
        JButton btnRemoveOab = Ui.btnDanger("- Remover");

        btnAddOab.addActionListener(e -> {
            String num = fNumOab.getText().trim();
            if (!num.matches("\\d{4,9}")) {
                Ui.aviso(dlg, "Número OAB deve conter entre 4 e 9 dígitos numéricos."); return;
            }
            String est = cEstadoOab.getSelectedItem().toString();
            oabListModel.addElement(new InscricaoOab(est, num));
            fNumOab.setText("");
        });

        btnRemoveOab.addActionListener(e -> {
            int idx = jListOab.getSelectedIndex();
            if (idx >= 0) oabListModel.remove(idx);
        });

        JPanel addOabRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        addOabRow.setOpaque(false);
        addOabRow.add(new JLabel("Estado:"));
        addOabRow.add(cEstadoOab);
        addOabRow.add(new JLabel("Número:"));
        addOabRow.add(fNumOab);
        addOabRow.add(btnAddOab);
        addOabRow.add(btnRemoveOab);

        JPanel oabPanel = new JPanel(new BorderLayout(0, 4));
        oabPanel.setOpaque(false);
        oabPanel.setBorder(new TitledBorder("Inscrições OAB *"));
        oabPanel.add(oabScroll, BorderLayout.CENTER);
        oabPanel.add(addOabRow, BorderLayout.SOUTH);

        int r = 0;
        Ui.addFormRow(form, gbc, r++, "Nome *:", fNome);

        // OAB panel ocupa as 2 colunas
        gbc.gridx = 0; gbc.gridy = r++; gbc.gridwidth = 2; gbc.weightx = 1.0;
        form.add(oabPanel, gbc);
        gbc.gridwidth = 1;

        Ui.addFormRow(form, gbc, r++, "Especialidades:", fEspec);
        Ui.addFormRow(form, gbc, r++, "CPF/CNPJ:", fIdent);
        Ui.addFormRow(form, gbc, r++, "Telefone:", fTel);
        Ui.addFormRow(form, gbc, r++, "E-mail:", fEmail);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = Ui.btnSuccess("Salvar");
        JButton btnCancel = new JButton("Cancelar");
        btns.add(btnCancel); btns.add(btnSalvar);
        btnCancel.addActionListener(e -> dlg.dispose());
        btnSalvar.addActionListener(e -> {
            if (fNome.getText().isBlank()) {
                Ui.aviso(dlg, "Nome é obrigatório."); return;
            }
            if (oabListModel.isEmpty()) {
                Ui.aviso(dlg, "Adicione pelo menos uma inscrição OAB."); return;
            }
            if (!MaskedField.validarEmail(fEmail.getText())) {
                Ui.aviso(dlg, "E-mail inválido."); return;
            }
            try {
                if (existente == null) {
                    Advogado a = new Advogado();
                    preencher(a, fNome, oabListModel, fEspec, fIdent, fTel, fEmail);
                    facade.salvarAdvogado(a);
                } else {
                    existente.getInscricoesOab().clear();
                    preencher(existente, fNome, oabListModel, fEspec, fIdent, fTel, fEmail);
                    facade.atualizarAdvogado(existente);
                }
                dlg.dispose(); atualizar();
            } catch (Exception ex) { Ui.erro(dlg, ex.getMessage()); }
        });

        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void preencher(Advogado a, JTextField fNome, DefaultListModel<InscricaoOab> oabListModel,
                            JTextField fEspec, JTextField fIdent, JTextField fTel, JTextField fEmail) {
        a.setNome(fNome.getText().trim());
        List<InscricaoOab> oabs = new ArrayList<>();
        for (int i = 0; i < oabListModel.size(); i++) oabs.add(oabListModel.get(i));
        a.setInscricoesOab(oabs);
        String[] especialidades = fEspec.getText().trim().split(",\\s*");
        for (String esp : especialidades) {
            if (!esp.isBlank()) a.addEspecialidade(esp.trim());
        }
        a.setIdentificador(fIdent.getText().trim());
        a.setTelefone(fTel.getText().trim());
        a.setEmail(fEmail.getText().trim());
        String identificador = fIdent.getText().trim();
        if (identificador.length() == 11) {
            a.setTipoPessoa(TipoPessoa.FISICA);
        } else if (identificador.length() == 14) {
            a.setTipoPessoa(TipoPessoa.JURIDICA);
        }
    }

    private void excluir() {
        Advogado a = selecionado();
        if (a == null) return;
        if (Ui.confirmar(this, "Excluir advogado \"" + a.getNome() + "\"?")) {
            try { facade.deletarAdvogado(a.getId()); atualizar(); }
            catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
        }
    }

    private Advogado selecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { Ui.aviso(this, "Selecione um advogado."); return null; }
        return lista.get(table.convertRowIndexToModel(row));
    }
}
