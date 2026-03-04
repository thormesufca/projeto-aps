package br.edu.ufca.audiencias.ui;

import br.edu.ufca.audiencias.models.Advogado;
import br.edu.ufca.audiencias.models.Escritorio;
import br.edu.ufca.audiencias.models.MembroEscritorio;
import br.edu.ufca.audiencias.models.enums.TipoVinculoAdvogado;
import br.edu.ufca.audiencias.padroes.estruturais.facade.SistemaJuridicoFacade;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EscritorioPanel extends JPanel {

    private final SistemaJuridicoFacade facade;

    // ── estado ────────────────────────────────────────────────────────────────
    private Escritorio escritorio;
    private List<MembroEscritorio> membros;

    // ── card do escritório ────────────────────────────────────────────────────
    private final JLabel lblNome      = cardLabel("—");
    private final JLabel lblCnpj      = cardLabel("—");
    private final JLabel lblTelefone  = cardLabel("—");
    private final JLabel lblEmail     = cardLabel("—");
    private final JLabel lblEndereco  = cardLabel("—");
    private final JLabel lblFundacao  = cardLabel("—");
    private final JButton btnEditarEsc = Ui.btnSecondary("Editar Escritório");
    private final JButton btnCriarEsc  = Ui.btnPrimary("Cadastrar Escritório");

    // ── tabela de membros ─────────────────────────────────────────────────────
    private static final String[] COLS_MEMBROS = {
            "ID", "Advogado", "Tipo Vínculo", "Participação / Salário",
            "Ingresso", "Desligamento", "Situação"
    };
    private final DefaultTableModel tableModel;
    private final JTable table;

    // ── percentual disponível (exibido no diálogo de membro) ─────────────────
    private BigDecimal percentualDisponivel = BigDecimal.valueOf(100);

    public EscritorioPanel(SistemaJuridicoFacade facade) {
        this.facade = facade;
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        tableModel = new DefaultTableModel(COLS_MEMBROS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = Ui.buildTable(tableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        add(Ui.pageTitle("Escritório"), BorderLayout.NORTH);
        add(buildCardEscritorio(), BorderLayout.CENTER);
        add(buildMembrosSection(), BorderLayout.SOUTH);
    }

    // ── card do escritório ────────────────────────────────────────────────────

    private JPanel buildCardEscritorio() {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 71, 79), 1),
                new EmptyBorder(14, 16, 14, 16)));
        card.setBackground(new Color(37, 50, 57));

        JPanel info = new JPanel(new GridLayout(3, 4, 16, 6));
        info.setOpaque(false);
        info.add(cardFieldSet("Nome", lblNome));
        info.add(cardFieldSet("CNPJ", lblCnpj));
        info.add(cardFieldSet("Telefone", lblTelefone));
        info.add(cardFieldSet("E-mail", lblEmail));
        info.add(cardFieldSet("Endereço", lblEndereco));
        info.add(cardFieldSet("Fundação", lblFundacao));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btns.setOpaque(false);
        btnCriarEsc.addActionListener(e -> abrirFormularioEscritorio(escritorio));
        btnEditarEsc.addActionListener(e -> abrirFormularioEscritorio(escritorio));
        btns.add(btnCriarEsc);
        btns.add(btnEditarEsc);

        card.add(info, BorderLayout.CENTER);
        card.add(btns, BorderLayout.EAST);
        return card;
    }

    private JPanel buildMembrosSection() {
        JPanel section = new JPanel(new BorderLayout(0, 8));
        section.setOpaque(false);
        section.setPreferredSize(new Dimension(0, 340));

        section.setBorder(new TitledBorder(
                BorderFactory.createLineBorder(new Color(55, 71, 79)),
                "Advogados Vinculados"));

        section.add(new JScrollPane(table), BorderLayout.CENTER);
        section.add(buildToolbar(), BorderLayout.SOUTH);
        return section;
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        bar.setOpaque(false);

        JButton btnAdicionar = Ui.btnPrimary("Adicionar");
        JButton btnEditar    = Ui.btnSecondary("Editar");
        JButton btnDesligar  = Ui.btnWarning("Desligar");
        JButton btnRemover   = Ui.btnDanger("Remover");
        JButton btnAtualizar = Ui.btnSecondary("Atualizar");

        btnAdicionar.addActionListener(e -> {
            if (escritorio.getNome().isBlank()) {
                Ui.aviso(this, "Configure o escritório antes de adicionar advogados.");
                return;
            }
            abrirFormularioMembro(null);
        });
        btnEditar.addActionListener(e -> {
            MembroEscritorio m = membroSelecionado();
            if (m != null) abrirFormularioMembro(m);
        });
        btnDesligar.addActionListener(e -> desligar());
        btnRemover.addActionListener(e -> remover());
        btnAtualizar.addActionListener(e -> atualizar());

        bar.add(btnAdicionar);
        bar.add(btnEditar);
        bar.add(btnDesligar);
        bar.add(Box.createHorizontalStrut(8));
        bar.add(btnRemover);
        bar.add(Box.createHorizontalStrut(8));
        bar.add(btnAtualizar);
        return bar;
    }

    // ── atualizar painel ──────────────────────────────────────────────────────

    public void atualizar() {
        escritorio = facade.getEscritorio();
        preencherCard();

        tableModel.setRowCount(0);
        boolean configurado = !escritorio.getNome().isBlank();
        if (configurado) {
            membros = facade.listarMembrosEscritorio();
            for (MembroEscritorio m : membros) {
                tableModel.addRow(membroParaLinha(m));
            }
        } else {
            membros = List.of();
        }

        btnCriarEsc.setVisible(!configurado);
        btnEditarEsc.setVisible(configurado);
    }

    private void preencherCard() {
        if (escritorio.getNome().isBlank()) {
            lblNome.setText("Não configurado");
            lblCnpj.setText("—");
            lblTelefone.setText("—");
            lblEmail.setText("—");
            lblEndereco.setText("—");
            lblFundacao.setText("—");
        } else {
            lblNome.setText(escritorio.getNome());
            lblCnpj.setText(formatarCnpj(escritorio.getCnpj()));
            lblTelefone.setText(escritorio.getTelefone() != null ? escritorio.getTelefone() : "—");
            lblEmail.setText(escritorio.getEmail() != null ? escritorio.getEmail() : "—");
            lblEndereco.setText(escritorio.getEndereco() != null ? escritorio.getEndereco() : "—");
            lblFundacao.setText(escritorio.getDataFundacao() != null
                    ? escritorio.getDataFundacao().toString() : "—");
        }
    }

    private Object[] membroParaLinha(MembroEscritorio m) {
        boolean socio = m.isSocio();
        String participacao = socio
                ? (m.getPercentualSociedade() != null
                        ? String.format("%.2f%%", m.getPercentualSociedade().doubleValue()) : "—")
                : (m.getSalarioMensal() != null
                        ? String.format("R$ %.2f", m.getSalarioMensal().doubleValue()) : "—");
        return new Object[]{
                m.getId(),
                m.getAdvogado() != null ? m.getAdvogado().getNome() : "—",
                m.getTipoVinculo().getDescricao(),
                participacao,
                m.getDataIngresso() != null ? m.getDataIngresso().toString() : "—",
                m.getDataDesligamento() != null ? m.getDataDesligamento().toString() : "—",
                m.isAtivo() ? "Ativo" : "Desligado"
        };
    }

    // ── diálogo do escritório ─────────────────────────────────────────────────

    private void abrirFormularioEscritorio(Escritorio existente) {
        boolean criando = existente.getNome().isBlank();
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                criando ? "Cadastrar Escritório" : "Editar Escritório",
                Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(500, 380);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 16, 8, 16));
        GridBagConstraints gbc = Ui.gbc();

        JTextField fNome     = new JTextField(existente != null ? existente.getNome() : "");
        JTextField fCnpj     = MaskedField.cpfCnpj(existente != null ? existente.getCnpj() : "");
        JTextField fTel      = MaskedField.telefone(existente != null ? existente.getTelefone() : "");
        JTextField fEmail    = new JTextField(existente != null && existente.getEmail() != null ? existente.getEmail() : "");
        JTextField fEndereco = new JTextField(existente != null && existente.getEndereco() != null ? existente.getEndereco() : "");
        JTextField fFundacao = new JTextField(existente != null && existente.getDataFundacao() != null
                ? existente.getDataFundacao().toString() : "", 12);

        int r = 0;
        Ui.addFormRow(form, gbc, r++, "Nome *:", fNome);
        Ui.addFormRow(form, gbc, r++, "CNPJ:", fCnpj);
        Ui.addFormRow(form, gbc, r++, "Telefone:", fTel);
        Ui.addFormRow(form, gbc, r++, "E-mail:", fEmail);
        Ui.addFormRow(form, gbc, r++, "Endereço:", fEndereco);
        Ui.addFormRow(form, gbc, r++, "Fundação (AAAA-MM-DD):", fFundacao);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = Ui.btnSuccess("Salvar");
        JButton btnCancel = new JButton("Cancelar");
        btns.add(btnCancel); btns.add(btnSalvar);
        btnCancel.addActionListener(e -> dlg.dispose());
        btnSalvar.addActionListener(ev -> {
            if (fNome.getText().isBlank()) { Ui.aviso(dlg, "Nome é obrigatório."); return; }
            if (!fEmail.getText().isBlank() && !MaskedField.validarEmail(fEmail.getText())) {
                Ui.aviso(dlg, "E-mail inválido."); return;
            }
            LocalDate fundacao = null;
            if (!fFundacao.getText().isBlank()) {
                try { fundacao = LocalDate.parse(fFundacao.getText().trim()); }
                catch (DateTimeParseException ex) { Ui.aviso(dlg, "Data de fundação inválida. Use AAAA-MM-DD."); return; }
            }
            try {
                Escritorio esc = criando ? new Escritorio() : existente;
                esc.setNome(fNome.getText().trim());
                String cnpj = MaskedField.valorSemMascara(fCnpj);
                if (!cnpj.isBlank()) esc.setCnpj(cnpj);
                else esc.setCnpj(null);
                esc.setTelefone(fTel.getText().trim());
                esc.setEmail(fEmail.getText().trim());
                esc.setEndereco(fEndereco.getText().trim());
                esc.setDataFundacao(fundacao);
                facade.salvarEscritorio(esc);
                dlg.dispose();
                atualizar();
            } catch (Exception ex) { Ui.erro(dlg, ex.getMessage()); }
        });

        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ── diálogo do membro ─────────────────────────────────────────────────────

    private void abrirFormularioMembro(MembroEscritorio existente) {
        boolean criando = existente == null;

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                criando ? "Adicionar Advogado ao Escritório" : "Editar Vínculo",
                Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(480, 380);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 16, 8, 16));
        GridBagConstraints gbc = Ui.gbc();

        // ── advogado ──────────────────────────────────────────────────────────
        List<Advogado> advogados = facade.listarAdvogados();
        JComboBox<Advogado> cAdvogado = new JComboBox<>(advogados.toArray(new Advogado[0]));
        if (existente != null && existente.getAdvogado() != null) {
            advogados.stream()
                    .filter(a -> a.getId().equals(existente.getAdvogado().getId()))
                    .findFirst().ifPresent(cAdvogado::setSelectedItem);
            cAdvogado.setEnabled(false); // advogado não pode mudar em edição
        }

        // ── tipo vínculo ──────────────────────────────────────────────────────
        JRadioButton rbSocio     = new JRadioButton("Sócio");
        JRadioButton rbEmpregado = new JRadioButton("Empregado");
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbSocio); grupo.add(rbEmpregado);
        JPanel pTipo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pTipo.setOpaque(false);
        pTipo.add(rbSocio); pTipo.add(Box.createHorizontalStrut(12)); pTipo.add(rbEmpregado);

        // ── campos condicionais ────────────────────────────────────────────────
        JTextField fPercentual = new JTextField(8);
        JTextField fSalario    = new JTextField(12);

        // Label de percentual disponível (informativo)
        JLabel lblDisponivel = new JLabel();
        lblDisponivel.setFont(lblDisponivel.getFont().deriveFont(Font.ITALIC, 11f));
        lblDisponivel.setForeground(new Color(144, 202, 249));

        JLabel lblPercentual = new JLabel("Participação (%) *:");
        JLabel lblSalario    = new JLabel("Salário Mensal (R$) *:");

        Runnable atualizarDisponivel = () -> {
            Long excluir = criando ? null : existente.getId();
            BigDecimal soma = facade.somarPercentuaisSocios(excluir);
            percentualDisponivel = BigDecimal.valueOf(100).subtract(soma);
            lblDisponivel.setText(String.format("Disponível: %.2f%%", percentualDisponivel.doubleValue()));
        };

        Runnable toggleCampos = () -> {
            boolean isSocio = rbSocio.isSelected();
            lblPercentual.setVisible(isSocio);
            fPercentual.setVisible(isSocio);
            lblDisponivel.setVisible(isSocio);
            lblSalario.setVisible(!isSocio);
            fSalario.setVisible(!isSocio);
            if (isSocio) atualizarDisponivel.run();
            dlg.revalidate();
            dlg.repaint();
        };

        rbSocio.addActionListener(e -> toggleCampos.run());
        rbEmpregado.addActionListener(e -> toggleCampos.run());

        // valor inicial
        if (existente != null && TipoVinculoAdvogado.EMPREGADO.equals(existente.getTipoVinculo())) {
            rbEmpregado.setSelected(true);
            if (existente.getSalarioMensal() != null)
                fSalario.setText(existente.getSalarioMensal().toPlainString());
        } else {
            rbSocio.setSelected(true);
            if (existente != null && existente.getPercentualSociedade() != null)
                fPercentual.setText(existente.getPercentualSociedade().toPlainString());
        }

        // ── datas ─────────────────────────────────────────────────────────────
        JTextField fIngresso     = new JTextField(existente != null && existente.getDataIngresso() != null
                ? existente.getDataIngresso().toString() : LocalDate.now().toString(), 12);
        JTextField fDesligamento = new JTextField(existente != null && existente.getDataDesligamento() != null
                ? existente.getDataDesligamento().toString() : "", 12);

        // ── montar form ───────────────────────────────────────────────────────
        int r = 0;
        Ui.addFormRow(form, gbc, r++, "Advogado *:", cAdvogado);
        Ui.addFormRow(form, gbc, r++, "Tipo de Vínculo *:", pTipo);
        Ui.addFormRow(form, gbc, r++, lblPercentual.getText(), fPercentual);

        // linha do disponível (largura total)
        gbc.gridx = 1; gbc.gridy = r++;
        form.add(lblDisponivel, gbc);

        Ui.addFormRow(form, gbc, r++, lblSalario.getText(), fSalario);
        Ui.addFormRow(form, gbc, r++, "Ingresso (AAAA-MM-DD):", fIngresso);
        Ui.addFormRow(form, gbc, r++, "Desligamento (AAAA-MM-DD):", fDesligamento);

        // ── referenciar labels dinâmicos para toggleCampos ────────────────────
        // usar as labels no form requer abordagem diferente; substituímos as
        // colunas 0 de cada linha por labels armazenadas e controlamos visibilidade
        // buscando os componentes da linha via getComponents()
        // Para simplicidade, obtemos diretamente os JLabels do GridBag:
        removerLabelFormRow(form, "Participação (%) *:");
        removerLabelFormRow(form, "Salário Mensal (R$) *:");

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        form.add(lblPercentual, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        form.add(lblSalario, gbc);

        // disparo inicial
        toggleCampos.run();

        // ── botões ────────────────────────────────────────────────────────────
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = Ui.btnSuccess("Salvar");
        JButton btnCancel = new JButton("Cancelar");
        btns.add(btnCancel); btns.add(btnSalvar);
        btnCancel.addActionListener(e -> dlg.dispose());

        btnSalvar.addActionListener(e -> {
            Advogado adv = (Advogado) cAdvogado.getSelectedItem();
            if (adv == null) { Ui.aviso(dlg, "Selecione um advogado."); return; }

            TipoVinculoAdvogado tipo = rbSocio.isSelected()
                    ? TipoVinculoAdvogado.SOCIO : TipoVinculoAdvogado.EMPREGADO;

            LocalDate ingresso = null;
            if (!fIngresso.getText().isBlank()) {
                try { ingresso = LocalDate.parse(fIngresso.getText().trim()); }
                catch (DateTimeParseException ex) { Ui.aviso(dlg, "Data de ingresso inválida. Use AAAA-MM-DD."); return; }
            }
            LocalDate desligamento = null;
            if (!fDesligamento.getText().isBlank()) {
                try { desligamento = LocalDate.parse(fDesligamento.getText().trim()); }
                catch (DateTimeParseException ex) { Ui.aviso(dlg, "Data de desligamento inválida. Use AAAA-MM-DD."); return; }
            }

            try {
                MembroEscritorio m = criando ? new MembroEscritorio() : existente;
                m.setAdvogado(adv);
                m.setTipoVinculo(tipo);
                m.setDataIngresso(ingresso);
                m.setDataDesligamento(desligamento);

                if (tipo == TipoVinculoAdvogado.SOCIO) {
                    m.setSalarioMensal(null);
                    BigDecimal perc;
                    try { perc = new BigDecimal(fPercentual.getText().trim().replace(',', '.')); }
                    catch (NumberFormatException ex) { Ui.aviso(dlg, "Percentual inválido."); return; }
                    m.setPercentualSociedade(perc);
                } else {
                    m.setPercentualSociedade(null);
                    BigDecimal sal;
                    try { sal = new BigDecimal(fSalario.getText().trim().replace(',', '.')); }
                    catch (NumberFormatException ex) { Ui.aviso(dlg, "Salário inválido."); return; }
                    m.setSalarioMensal(sal);
                }

                if (criando) facade.adicionarMembroEscritorio(m);
                else         facade.atualizarMembroEscritorio(m);

                dlg.dispose();
                atualizar();
            } catch (IllegalArgumentException ex) {
                Ui.aviso(dlg, ex.getMessage());
            } catch (Exception ex) {
                Ui.erro(dlg, ex.getMessage());
            }
        });

        dlg.add(new JScrollPane(form), BorderLayout.CENTER);
        dlg.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    /**
     * Remove do panel o JLabel que contenha o texto especificado
     * (linhas adicionadas provisoriamente por addFormRow que serão reposicionadas).
     */
    private void removerLabelFormRow(JPanel form, String texto) {
        for (Component c : form.getComponents()) {
            if (c instanceof JLabel lbl && lbl.getText().equals(texto)) {
                form.remove(c);
                return;
            }
        }
    }

    // ── ações de membros ──────────────────────────────────────────────────────

    private void desligar() {
        MembroEscritorio m = membroSelecionado();
        if (m == null) return;
        if (!m.isAtivo()) { Ui.aviso(this, "Este advogado já está desligado."); return; }
        if (!Ui.confirmar(this, "Registrar desligamento de \"" + nomeDoMembro(m) + "\" hoje?")) return;
        m.setDataDesligamento(LocalDate.now());
        try {
            facade.atualizarMembroEscritorio(m);
            atualizar();
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }

    private void remover() {
        MembroEscritorio m = membroSelecionado();
        if (m == null) return;
        if (!Ui.confirmar(this, "Remover permanentemente o vínculo de \"" + nomeDoMembro(m) + "\"?")) return;
        try {
            facade.removerMembroEscritorio(m.getId());
            atualizar();
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private MembroEscritorio membroSelecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { Ui.aviso(this, "Selecione um advogado na tabela."); return null; }
        return membros.get(table.convertRowIndexToModel(row));
    }

    private String nomeDoMembro(MembroEscritorio m) {
        return m.getAdvogado() != null ? m.getAdvogado().getNome() : "#" + m.getId();
    }

    private static JLabel cardLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(lbl.getFont().deriveFont(13f));
        return lbl;
    }

    private static JPanel cardFieldSet(String title, JLabel value) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);
        JLabel lbTit = new JLabel(title);
        lbTit.setForeground(new Color(144, 202, 249));
        lbTit.setFont(lbTit.getFont().deriveFont(Font.BOLD, 10f));
        p.add(lbTit, BorderLayout.NORTH);
        p.add(value, BorderLayout.CENTER);
        return p;
    }

    private static String formatarCnpj(String cnpj) {
        if (cnpj == null || cnpj.length() != 14) return cnpj != null ? cnpj : "—";
        return cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "." +
               cnpj.substring(5, 8) + "/" + cnpj.substring(8, 12) + "-" + cnpj.substring(12);
    }
}
