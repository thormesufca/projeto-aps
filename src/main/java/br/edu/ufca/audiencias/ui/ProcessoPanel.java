package br.edu.ufca.audiencias.ui;

import br.edu.ufca.audiencias.models.*;
import br.edu.ufca.audiencias.models.enums.*;
import br.edu.ufca.audiencias.padroes.comportamentais.strategy.*;
import br.edu.ufca.audiencias.padroes.estruturais.facade.SistemaJuridicoFacade;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProcessoPanel extends JPanel {

    private final SistemaJuridicoFacade facade;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<Processo> lista;

    private final JTextField campoBusca = new JTextField(20);
    private final JComboBox<String> estrategiaBox = new JComboBox<>(
            new String[] { "Por Número", "Por Cliente", "Por Status" });

    private static final String[] COLS = {
            "ID", "Número", "Cliente", "Tipo", "Status", "Fase", "Órgão Julgador", "Resultado"
    };

    public ProcessoPanel(SistemaJuridicoFacade facade) {
        this.facade = facade;
        setBackground(Color.BLACK);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        tableModel = new DefaultTableModel(COLS, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = Ui.buildTable(tableModel);
        table.setAutoCreateRowSorter(true);

        add(buildTopBar(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildToolbar(), BorderLayout.SOUTH);
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        top.add(Ui.pageTitle("Processos"), BorderLayout.NORTH);

        JPanel busca = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        busca.setOpaque(false);
        JLabel lblEst = new JLabel("Buscar:");
        lblEst.setForeground(java.awt.Color.BLACK);
        JButton btnBuscar = Ui.btnPrimary("Buscar");
        JButton btnLimpar = Ui.btnSecondary("Todos");
        btnBuscar.addActionListener(e -> buscar());
        btnLimpar.addActionListener(e -> atualizar());
        busca.add(lblEst);
        busca.add(estrategiaBox);
        busca.add(campoBusca);
        busca.add(btnBuscar);
        busca.add(btnLimpar);
        top.add(busca, BorderLayout.CENTER);
        return top;
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 8));
        bar.setOpaque(false);
        JButton btnNovo = Ui.btnPrimary("Novo Processo");
        JButton btnEditar = Ui.btnSecondary("Editar");
        JButton btnExcluir = Ui.btnDanger("Excluir");
        JButton btnDetalhes = Ui.btnWarning("Detalhes");
        JButton btnAtual = Ui.btnSecondary("Atualizar");

        btnNovo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> {
            Processo p = selecionado();
            if (p != null)
                abrirFormulario(p);
        });
        btnExcluir.addActionListener(e -> excluir());
        btnDetalhes.addActionListener(e -> {
            Processo p = selecionado();
            if (p != null)
                abrirDetalhes(p);
        });
        btnAtual.addActionListener(e -> atualizar());

        bar.add(btnNovo);
        bar.add(btnEditar);
        bar.add(btnExcluir);
        bar.add(btnDetalhes);
        bar.add(Box.createHorizontalStrut(10));
        bar.add(btnAtual);
        return bar;
    }

    public void atualizar() {
        tableModel.setRowCount(0);
        try {
            lista = facade.listarProcessos();
            popularTabela(lista);
        } catch (Exception ex) {
            Ui.erro(this, ex.getMessage());
        }
    }

    private void buscar() {
        String termo = campoBusca.getText().trim();
        if (termo.isBlank()) {
            atualizar();
            return;
        }
        try {
            EstrategiaBuscaProcesso estrategia = switch (estrategiaBox.getSelectedIndex()) {
                case 1 -> new BuscaPorClienteStrategy();
                case 2 -> new BuscaPorStatusStrategy();
                default -> new BuscaPorNumeroStrategy();
            };
            lista = facade.buscarProcessos(estrategia, termo);
            tableModel.setRowCount(0);
            popularTabela(lista);
        } catch (Exception ex) {
            Ui.erro(this, ex.getMessage());
        }
    }

    private void popularTabela(List<Processo> processos) {
        for (Processo p : processos) {
            String resultado = p.getFavoravel() == null ? "—"
                    : p.getFavoravel() ? "Favorável" : "Desfavorável";
            String nomeCliente = p.getCliente() != null ? p.getCliente().getNome() : "—";
            tableModel.addRow(new Object[] {
                    p.getId(), MaskedField.formatarNumeroProcesso(p.getNumero()),
                    nomeCliente, p.getTipo(), p.getStatus(), p.getFase(), p.getOrgaoJulgador(), resultado
            });
        }
    }

    private void abrirFormulario(Processo existente) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                existente == null ? "Novo Processo" : "Editar Processo",
                Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(660, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        // ── Aba 1: Dados Principais ──────────────────────────────────────────
        JPanel formPrincipal = new JPanel(new GridBagLayout());
        formPrincipal.setBorder(new EmptyBorder(16, 16, 8, 16));
        GridBagConstraints gbc = Ui.gbc();

        JTextField fNum = MaskedField.numeroProcesso(existente != null ? existente.getNumero() : "");
        JTextField fOrgao = new JTextField(existente != null ? existente.getOrgaoJulgador() : "");
        JTextArea fDesc = new JTextArea(existente != null ? existente.getDescricao() : "", 3, 20);

        JComboBox<TipoProcesso> fTipo = new JComboBox<>(TipoProcesso.values());
        JComboBox<StatusProcesso> fStatus = new JComboBox<>(StatusProcesso.values());
        JComboBox<FaseProcesso> fFase = new JComboBox<>(FaseProcesso.values());

        if (existente != null) {
            fTipo.setSelectedItem(existente.getTipo());
            fStatus.setSelectedItem(existente.getStatus());
            fFase.setSelectedItem(existente.getFase());
        }

        int r = 0;
        Ui.addFormRow(formPrincipal, gbc, r++, "Número *:", fNum);
        Ui.addFormRow(formPrincipal, gbc, r++, "Tipo:", fTipo);
        Ui.addFormRow(formPrincipal, gbc, r++, "Status:", fStatus);
        Ui.addFormRow(formPrincipal, gbc, r++, "Fase:", fFase);
        Ui.addFormRow(formPrincipal, gbc, r++, "Órgão Julgador:", fOrgao);
        Ui.addFormRow(formPrincipal, gbc, r++, "Descrição:", new JScrollPane(fDesc));

        // ── Aba 2: Dados Financeiros ─────────────────────────────────────────
        JPanel formFinanceiro = new JPanel(new GridBagLayout());
        formFinanceiro.setBorder(new EmptyBorder(16, 16, 8, 16));
        GridBagConstraints gbc2 = Ui.gbc();

        JTextField fValorCausa = new JTextField(
                existente != null && existente.getValorCausa() != null
                        ? existente.getValorCausa().toPlainString()
                        : "");
        JTextField fValorCondenacao = new JTextField(
                existente != null && existente.getValorCondenacao() != null
                        ? existente.getValorCondenacao().toPlainString()
                        : "");
        JTextField fHonorarios = new JTextField(
                existente != null && existente.getHonorariosSucumbenciais() != null
                        ? existente.getHonorariosSucumbenciais().toPlainString()
                        : "");

        JComboBox<String> fResultado = new JComboBox<>(
                new String[] { "Não definido", "Favorável", "Desfavorável" });
        if (existente != null && existente.getFavoravel() != null)
            fResultado.setSelectedIndex(existente.getFavoravel() ? 1 : 2);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        JFormattedTextField fDataPagamento = new JFormattedTextField(sdf);
        if (existente != null && existente.getDataPagamento() != null)
            fDataPagamento.setValue(new java.util.Date(
                    java.sql.Date.valueOf(existente.getDataPagamento()).getTime()));
        fDataPagamento.setColumns(12);

        int r2 = 0;
        Ui.addFormRow(formFinanceiro, gbc2, r2++, "Valor da Causa (R$):", fValorCausa);
        Ui.addFormRow(formFinanceiro, gbc2, r2++, "Valor da Condenação (R$):", fValorCondenacao);
        Ui.addFormRow(formFinanceiro, gbc2, r2++, "Resultado:", fResultado);
        Ui.addFormRow(formFinanceiro, gbc2, r2++, "Hon. Sucumbenciais (R$):", fHonorarios);
        Ui.addFormRow(formFinanceiro, gbc2, r2++, "Data de Pagamento (dd/MM/yyyy):", fDataPagamento);

        // ── Aba 3: Assuntos ──────────────────────────────────────────────────
        DefaultTableModel assuntosModel = new DefaultTableModel(
                new String[] { "ID", "Código", "Nome", "Principal" }, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        List<Long> assuntosOriginaisIds = new ArrayList<>();
        if (existente != null) {
            facade.listarAssuntosDoProcesso(existente.getId()).forEach(ap -> {
                assuntosModel.addRow(new Object[] {
                        ap.getId(), ap.getAssunto().getCodItem(),
                        ap.getAssunto().getNome(), ap.isPrincipal() ? "Sim" : "—"
                });
                assuntosOriginaisIds.add(ap.getId());
            });
        }

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Dados Principais", new JScrollPane(formPrincipal));
        if (existente != null) {
            tabs.addTab("Dados Financeiros", new JScrollPane(formFinanceiro));
        }
        tabs.addTab("Assuntos", buildAssuntosFormTab(assuntosModel, dlg));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = Ui.btnSuccess("Salvar");
        JButton btnCancel = new JButton("Cancelar");
        btns.add(btnCancel);
        btns.add(btnSalvar);
        btnCancel.addActionListener(e -> dlg.dispose());
        btnSalvar.addActionListener(e -> {
            String num = MaskedField.valorSemMascara(fNum);
            if (num.isBlank()) {
                Ui.aviso(dlg, "Número é obrigatório.");
                return;
            }
            try {
                if (existente == null) {
                    Processo novo = facade.criarProcesso((TipoProcesso) fTipo.getSelectedItem(), num);
                    novo.setStatus((StatusProcesso) fStatus.getSelectedItem());
                    novo.setFase((FaseProcesso) fFase.getSelectedItem());
                    novo.setOrgaoJulgador(fOrgao.getText().trim());
                    novo.setDescricao(fDesc.getText().trim());
                    facade.atualizarProcesso(novo);
                    for (int i = 0; i < assuntosModel.getRowCount(); i++) {
                        Long codItem = (Long) assuntosModel.getValueAt(i, 1);
                        boolean principal = "Sim".equals(assuntosModel.getValueAt(i, 3));
                        facade.adicionarAssuntoAoProcesso(novo.getId(), codItem, principal);
                    }
                } else {
                    existente.setNumero(num);
                    existente.setTipo((TipoProcesso) fTipo.getSelectedItem());
                    existente.setStatus((StatusProcesso) fStatus.getSelectedItem());
                    existente.setFase((FaseProcesso) fFase.getSelectedItem());
                    existente.setOrgaoJulgador(fOrgao.getText().trim());
                    existente.setDescricao(fDesc.getText().trim());
                    existente.setValorCausa(fValorCausa.getText().isBlank() ? null
                            : new BigDecimal(fValorCausa.getText().trim().replace(",", ".")));
                    existente.setValorCondenacao(fValorCondenacao.getText().isBlank() ? null
                            : new BigDecimal(fValorCondenacao.getText().trim().replace(",", ".")));
                    existente.setHonorariosSucumbenciais(fHonorarios.getText().isBlank() ? null
                            : new BigDecimal(fHonorarios.getText().trim().replace(",", ".")));
                    existente.setFavoravel(switch (fResultado.getSelectedIndex()) {
                        case 1 -> true;
                        case 2 -> false;
                        default -> null;
                    });
                    existente.setDataPagamento(fDataPagamento.getValue() == null ? null
                            : new java.sql.Date(
                                    ((java.util.Date) fDataPagamento.getValue()).getTime()).toLocalDate());
                    facade.atualizarProcesso(existente);
                    for (Long apId : assuntosOriginaisIds) {
                        facade.removerAssuntoDoProcesso(apId);
                    }
                    for (int i = 0; i < assuntosModel.getRowCount(); i++) {
                        Long codItem = (Long) assuntosModel.getValueAt(i, 1);
                        boolean principal = "Sim".equals(assuntosModel.getValueAt(i, 3));
                        facade.adicionarAssuntoAoProcesso(existente.getId(), codItem, principal);
                    }
                }
                dlg.dispose();
                atualizar();
            } catch (NumberFormatException ex) {
                Ui.aviso(dlg, "Valor inválido. Use ponto como separador decimal.");
            } catch (Exception ex) {
                Ui.erro(dlg, "Erro ao salvar: " + ex.getMessage());
            }
        });

        dlg.add(tabs, BorderLayout.CENTER);
        dlg.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void abrirDetalhes(Processo p) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Detalhes — Processo " + MaskedField.formatarNumeroProcesso(p.getNumero()),
                Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(740, 540);
        dlg.setLocationRelativeTo(this);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Audiências", buildAudienciasTab(p, dlg));
        tabs.addTab("Interessados", buildInteressadosTab(p, dlg));
        tabs.addTab("Testemunhas", buildTestemunhasTab(p, dlg));
        tabs.addTab("Movimentações", buildMovimentacoesTab(p));
        tabs.addTab("Documentos", buildDocumentosTab(p));
        tabs.addTab("Financeiro", buildFinanceiroTab(p));
        tabs.addTab("Assuntos", buildAssuntosTab(p));

        dlg.add(tabs);
        dlg.setVisible(true);
    }

    private JPanel buildAudienciasTab(Processo p, JDialog owner) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        DefaultTableModel m = new DefaultTableModel(
                new String[] { "ID", "Tipo", "Status", "Data/Hora", "Local", "Descrição" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable t = Ui.buildTable(m);

        Runnable refresh = () -> {
            m.setRowCount(0);
            facade.listarAudienciasPorProcesso(p.getId()).forEach(a -> m.addRow(new Object[] {
                    a.getId(), a.getTipo(), a.getStatus(), a.getDataHora(), a.getLocal(), a.getDescricao()
            }));
        };
        refresh.run();

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        JButton btnAgendar = Ui.btnPrimary("Agendar");
        JButton btnCancelar = Ui.btnDanger("Cancelar");
        JButton btnRemarcar = Ui.btnWarning("Remarcar");
        JButton btnResult = Ui.btnSuccess("Registrar Resultado");
        JButton btnExcluir = Ui.btnDanger("Excluir");
        btns.add(btnAgendar);
        btns.add(btnCancelar);
        btns.add(btnRemarcar);
        btns.add(btnResult);
        btns.add(btnExcluir);

        btnAgendar.addActionListener(e -> {
            JDialog dlgAg = new JDialog(owner,
                    "Agendar Audiência — " + MaskedField.formatarNumeroProcesso(p.getNumero()),
                    Dialog.ModalityType.APPLICATION_MODAL);
            dlgAg.setSize(480, 380);
            dlgAg.setLocationRelativeTo(owner);
            dlgAg.setLayout(new BorderLayout());

            JPanel form = new JPanel(new GridBagLayout());
            form.setBorder(new EmptyBorder(16, 16, 8, 16));
            GridBagConstraints gbc = Ui.gbc();

            JComboBox<TipoAudiencia> fTipo = new JComboBox<>(TipoAudiencia.values());
            DateTimePicker fDataHora = new DateTimePicker();
            JTextField fLocal = new JTextField();
            JTextField fDesc = new JTextField();

            DefaultListModel<String> obsModel = new DefaultListModel<>();
            JList<String> obsList = new JList<>(obsModel);
            JTextField fObs = new JTextField();
            JButton btnAddObs = Ui.btnSecondary("+ Obs.");
            btnAddObs.addActionListener(ev -> {
                if (!fObs.getText().isBlank()) {
                    obsModel.addElement(fObs.getText().trim());
                    fObs.setText("");
                }
            });
            JPanel obsPanel = new JPanel(new BorderLayout(4, 4));
            JPanel obsAdd = new JPanel(new BorderLayout(4, 0));
            obsAdd.add(fObs, BorderLayout.CENTER);
            obsAdd.add(btnAddObs, BorderLayout.EAST);
            obsPanel.add(new JScrollPane(obsList), BorderLayout.CENTER);
            obsPanel.add(obsAdd, BorderLayout.SOUTH);

            int r = 0;
            Ui.addFormRow(form, gbc, r++, "Tipo:", fTipo);
            Ui.addFormRow(form, gbc, r++, "Data/Hora:", fDataHora);
            Ui.addFormRow(form, gbc, r++, "Local:", fLocal);
            Ui.addFormRow(form, gbc, r++, "Descrição:", fDesc);
            gbc.gridx = 0;
            gbc.gridy = r;
            gbc.weightx = 0;
            form.add(new JLabel("Observações:"), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1;
            form.add(obsPanel, gbc);

            JPanel bBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnSalvar = Ui.btnSuccess("Agendar");
            JButton btnCancel2 = new JButton("Cancelar");
            bBtns.add(btnCancel2);
            bBtns.add(btnSalvar);
            btnCancel2.addActionListener(ev -> dlgAg.dispose());
            btnSalvar.addActionListener(ev -> {
                try {
                    LocalDateTime dt = fDataHora.getLocalDateTime();
                    Audiencia ag = facade.agendarAudiencia(p.getId(), dt,
                            fLocal.getText().trim(),
                            (TipoAudiencia) fTipo.getSelectedItem(),
                            fDesc.getText().trim());
                    for (int i = 0; i < obsModel.size(); i++)
                        ag.adicionarObservacao(obsModel.get(i));
                    dlgAg.dispose();
                    refresh.run();
                } catch (Exception ex) {
                    Ui.erro(dlgAg, "Erro ao agendar: " + ex.getMessage());
                }
            });

            dlgAg.add(new JScrollPane(form), BorderLayout.CENTER);
            dlgAg.add(bBtns, BorderLayout.SOUTH);
            dlgAg.setVisible(true);
        });

        btnCancelar.addActionListener(e -> {
            int row = t.getSelectedRow();
            if (row < 0) {
                Ui.aviso(owner, "Selecione uma audiência.");
                return;
            }
            Long id = (Long) m.getValueAt(t.convertRowIndexToModel(row), 0);
            String motivo = JOptionPane.showInputDialog(owner, "Motivo do cancelamento:");
            if (motivo == null)
                return;
            try {
                facade.cancelarAudiencia(id, motivo);
                refresh.run();
            } catch (Exception ex) {
                Ui.erro(owner, ex.getMessage());
            }
        });

        btnRemarcar.addActionListener(e -> {
            int row = t.getSelectedRow();
            if (row < 0) {
                Ui.aviso(owner, "Selecione uma audiência.");
                return;
            }
            Long id = (Long) m.getValueAt(t.convertRowIndexToModel(row), 0);
            DateTimePicker picker = new DateTimePicker();
            int res = JOptionPane.showConfirmDialog(owner, picker,
                    "Nova data/hora da audiência", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res != JOptionPane.OK_OPTION)
                return;
            try {
                facade.remarcarAudiencia(id, picker.getLocalDateTime());
                refresh.run();
            } catch (Exception ex) {
                Ui.erro(owner, ex.getMessage());
            }
        });

        btnResult.addActionListener(e -> {
            int row = t.getSelectedRow();
            if (row < 0) {
                Ui.aviso(owner, "Selecione uma audiência.");
                return;
            }
            Long id = (Long) m.getValueAt(t.convertRowIndexToModel(row), 0);
            String resultado = JOptionPane.showInputDialog(owner, "Resultado da audiência:");
            if (resultado == null)
                return;
            try {
                facade.registrarResultadoAudiencia(id, resultado);
                refresh.run();
            } catch (Exception ex) {
                Ui.erro(owner, ex.getMessage());
            }
        });

        btnExcluir.addActionListener(e -> {
            int row = t.getSelectedRow();
            if (row < 0) {
                Ui.aviso(owner, "Selecione uma audiência.");
                return;
            }
            Long id = (Long) m.getValueAt(t.convertRowIndexToModel(row), 0);
            if (Ui.confirmar(owner, "Excluir audiência #" + id + "?")) {
                try {
                    facade.deletarAudiencia(id);
                    refresh.run();
                } catch (Exception ex) {
                    Ui.erro(owner, ex.getMessage());
                }
            }
        });

        panel.add(new JScrollPane(t), BorderLayout.CENTER);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildInteressadosTab(Processo p, JDialog owner) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        DefaultTableModel m = new DefaultTableModel(
                new String[] { "ID", "Nome", "Tipo Parte", "Identificador", "Advogado" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable t = Ui.buildTable(m);
        Runnable refresh = () -> {
            m.setRowCount(0);
            facade.listarInteressados(p.getId()).forEach(i -> m.addRow(new Object[] {
                    i.getId(), i.getNome(), i.getTipoParte(), i.getIdentificador(),
                    i.getAdvogado() != null ? i.getAdvogado().getNome() : "-"
            }));
        };
        refresh.run();

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        JButton btnAdd = Ui.btnPrimary("Adicionar");
        JButton btnRem = Ui.btnDanger("Remover");
        btns.add(btnAdd);
        btns.add(btnRem);

        btnAdd.addActionListener(e -> {
            List<Advogado> advogados = facade.listarAdvogados();
            JTextField fNome = new JTextField();
            JTextField fIdent = new JTextField();
            JComboBox<TipoParte> fTipo = new JComboBox<>(TipoParte.values());
            JComboBox<Advogado> fAdv = new JComboBox<>();
            fAdv.addItem(null);
            advogados.forEach(fAdv::addItem);
            JPanel f = new JPanel(new GridBagLayout());
            GridBagConstraints g = Ui.gbc();
            Ui.addFormRow(f, g, 0, "Nome:", fNome);
            Ui.addFormRow(f, g, 1, "Identificador:", fIdent);
            Ui.addFormRow(f, g, 2, "Tipo Parte:", fTipo);
            Ui.addFormRow(f, g, 3, "Advogado:", fAdv);
            int res = JOptionPane.showConfirmDialog(owner, f, "Novo Interessado", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION && !fNome.getText().isBlank()) {
                Interessado i = new Interessado();
                i.setNome(fNome.getText().trim());
                i.setIdentificador(fIdent.getText().trim());
                i.setTipoParte((TipoParte) fTipo.getSelectedItem());
                i.setTipoPessoa(TipoPessoa.FISICA);
                i.setProcesso(p);
                i.setAdvogado((Advogado) fAdv.getSelectedItem());
                try {
                    facade.adicionarInteressado(p.getId(), i);
                    refresh.run();
                } catch (Exception ex) {
                    Ui.erro(owner, ex.getMessage());
                }
            }
        });
        btnRem.addActionListener(e -> {
            int row = t.getSelectedRow();
            if (row < 0)
                return;
            Long id = (Long) m.getValueAt(t.convertRowIndexToModel(row), 0);
            try {
                facade.removerInteressado(id);
                refresh.run();
            } catch (Exception ex) {
                Ui.erro(owner, ex.getMessage());
            }
        });

        panel.add(new JScrollPane(t), BorderLayout.CENTER);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildTestemunhasTab(Processo p, JDialog owner) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        DefaultTableModel m = new DefaultTableModel(
                new String[] { "linkId", "Nome", "Identificador", "Interessado", "Depoimento" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable t = Ui.buildTable(m);
        t.getColumnModel().getColumn(0).setMinWidth(0);
        t.getColumnModel().getColumn(0).setMaxWidth(0);
        t.getColumnModel().getColumn(0).setWidth(0);

        Runnable refresh = () -> {
            m.setRowCount(0);
            facade.listarTestemunhas(p.getId()).forEach(ts -> m.addRow(new Object[] {
                    ts.getLinkId(),
                    ts.getNome(),
                    ts.getIdentificador() != null ? ts.getIdentificador() : "-",
                    ts.getInteressado() != null ? ts.getInteressado().toString() : "-",
                    ts.getDepoimento() != null ? ts.getDepoimento() : ""
            }));
        };
        refresh.run();

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        JButton btnAdd = Ui.btnPrimary("Adicionar");
        JButton btnRem = Ui.btnDanger("Remover Vínculo");
        btns.add(btnAdd);
        btns.add(btnRem);

        btnAdd.addActionListener(e -> abrirDialogAdicionarTestemunha(p, owner, refresh));

        btnRem.addActionListener(e -> {
            int row = t.getSelectedRow();
            if (row < 0) {
                Ui.aviso(owner, "Selecione um vínculo para remover.");
                return;
            }
            Long linkId = (Long) m.getValueAt(t.convertRowIndexToModel(row), 0);
            if (Ui.confirmar(owner, "Remover este vínculo de testemunha?")) {
                try {
                    facade.removerVinculoTestemunha(linkId);
                    refresh.run();
                } catch (Exception ex) {
                    Ui.erro(owner, ex.getMessage());
                }
            }
        });

        panel.add(new JScrollPane(t), BorderLayout.CENTER);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    private void abrirDialogAdicionarTestemunha(Processo p, JDialog owner, Runnable refresh) {
        List<Interessado> interessados = facade.listarInteressados(p.getId());
        if (interessados.isEmpty()) {
            Ui.aviso(owner, "Cadastre ao menos um interessado antes de adicionar testemunhas.");
            return;
        }

        JDialog dlg = new JDialog(owner, "Adicionar Testemunha — " + MaskedField.formatarNumeroProcesso(p.getNumero()),
                Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(620, 500);
        dlg.setLocationRelativeTo(owner);
        dlg.setLayout(new BorderLayout(0, 0));

        // ── Painel de busca de testemunha existente ──────────────────────────
        JPanel buscarPanel = new JPanel(new BorderLayout(0, 4));
        buscarPanel.setBorder(new EmptyBorder(10, 10, 4, 10));

        JPanel buscaBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        buscaBar.add(new JLabel("Buscar existente:"));
        JTextField fBusca = new JTextField(20);
        JButton btnBuscar = Ui.btnSecondary("Buscar");
        buscaBar.add(fBusca);
        buscaBar.add(btnBuscar);

        DefaultTableModel bm = new DefaultTableModel(
                new String[] { "ID", "Nome", "Identificador", "Depoimento" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable bt = Ui.buildTable(bm);
        bt.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        buscarPanel.add(buscaBar, BorderLayout.NORTH);
        buscarPanel.add(new JScrollPane(bt), BorderLayout.CENTER);

        JPanel dadosPanel = new JPanel(new GridBagLayout());
        dadosPanel.setBorder(new EmptyBorder(4, 10, 4, 10));
        GridBagConstraints gbc = Ui.gbc();

        JTextField fNome = new JTextField();
        JTextField fIdent = new JTextField();
        JTextField fDep = new JTextField();

        JLabel lblStatus = new JLabel(" ");
        lblStatus.setForeground(new Color(0, 120, 0));

        Ui.addFormRow(dadosPanel, gbc, 0, "Nome *:", fNome);
        Ui.addFormRow(dadosPanel, gbc, 1, "Identificador:", fIdent);
        Ui.addFormRow(dadosPanel, gbc, 2, "Depoimento:", fDep);
        Ui.addFormRow(dadosPanel, gbc, 3, "", lblStatus);

        JPanel interPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        interPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
        JComboBox<Interessado> cbInteressado = new JComboBox<>();
        interessados.forEach(cbInteressado::addItem);
        interPanel.add(new JLabel("Interessado *:"));
        interPanel.add(cbInteressado);

        Testemunha[] tsExistente = { null };

        Runnable buscarAction = () -> {
            String termo = fBusca.getText().trim();
            if (termo.isBlank())
                return;
            bm.setRowCount(0);
            facade.buscarTestemunhasPorNome(termo).forEach(ts -> bm.addRow(new Object[] {
                    ts.getId(), ts.getNome(),
                    ts.getIdentificador() != null ? ts.getIdentificador() : "-",
                    ts.getDepoimento() != null ? ts.getDepoimento() : ""
            }));
        };

        btnBuscar.addActionListener(e2 -> buscarAction.run());
        fBusca.addActionListener(e2 -> buscarAction.run());

        bt.getSelectionModel().addListSelectionListener(ev -> {
            if (ev.getValueIsAdjusting())
                return;
            int row = bt.getSelectedRow();
            if (row < 0) {
                tsExistente[0] = null;
                fNome.setEditable(true);
                fIdent.setEditable(true);
                fNome.setText("");
                fIdent.setText("");
                fDep.setText("");
                lblStatus.setText(" ");
                return;
            }
            row = bt.convertRowIndexToModel(row);
            Testemunha ts = new Testemunha();
            ts.setId((Long) bm.getValueAt(row, 0));
            ts.setNome((String) bm.getValueAt(row, 1));
            String ident = (String) bm.getValueAt(row, 2);
            ts.setIdentificador("-".equals(ident) ? null : ident);
            String dep = (String) bm.getValueAt(row, 3);
            ts.setDepoimento(dep.isBlank() ? null : dep);
            ts.setTipoPessoa(TipoPessoa.FISICA);
            tsExistente[0] = ts;

            fNome.setText(ts.getNome());
            fNome.setEditable(false);
            fIdent.setText(ts.getIdentificador() != null ? ts.getIdentificador() : "");
            fIdent.setEditable(false);
            fDep.setText(ts.getDepoimento() != null ? ts.getDepoimento() : "");
            fDep.setEditable(true); // depoimento pode ser atualizado
            lblStatus.setText("Testemunha existente selecionada — apenas vínculo será criado.");
        });

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 8));
        JButton btnVincular = Ui.btnSuccess("Vincular");
        JButton btnCancelar = new JButton("Cancelar");
        rodape.add(btnCancelar);
        rodape.add(btnVincular);
        btnCancelar.addActionListener(e2 -> dlg.dispose());

        btnVincular.addActionListener(e2 -> {
            Interessado interessado = (Interessado) cbInteressado.getSelectedItem();
            if (interessado == null) {
                Ui.aviso(dlg, "Selecione um interessado.");
                return;
            }

            Testemunha ts = tsExistente[0];
            if (ts == null) {
                if (fNome.getText().isBlank()) {
                    Ui.aviso(dlg, "Informe o nome da testemunha.");
                    return;
                }
                ts = new Testemunha();
                ts.setNome(fNome.getText().trim());
                ts.setIdentificador(fIdent.getText().isBlank() ? null : fIdent.getText().trim());
                ts.setDepoimento(fDep.getText().isBlank() ? null : fDep.getText().trim());
                ts.setTipoPessoa(TipoPessoa.FISICA);
            } else {
                if (!fDep.getText().isBlank())
                    ts.setDepoimento(fDep.getText().trim());
            }

            try {
                facade.adicionarTestemunha(ts, interessado.getId(), p.getId());
                dlg.dispose();
                refresh.run();
            } catch (Exception ex) {
                Ui.erro(dlg, "Erro ao vincular: " + ex.getMessage());
            }
        });

        JPanel centro = new JPanel(new BorderLayout(0, 0));
        centro.add(buscarPanel, BorderLayout.CENTER);
        centro.add(dadosPanel, BorderLayout.SOUTH);

        dlg.add(centro, BorderLayout.CENTER);
        dlg.add(interPanel, BorderLayout.NORTH);
        dlg.add(rodape, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private JPanel buildMovimentacoesTab(Processo p) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        DefaultTableModel m = new DefaultTableModel(
                new String[] { "ID", "Tipo", "Data/Hora", "Responsável", "Descrição" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable t = Ui.buildTable(m);

        facade.listarMovimentacoes(p.getId()).forEach(mv -> m.addRow(new Object[] {
                mv.getId(), mv.getTipo(), mv.getDataHora(), mv.getResponsavel(), mv.getDescricao()
        }));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        JButton btnAdd = Ui.btnPrimary("Registrar");
        btns.add(btnAdd);
        btnAdd.addActionListener(e -> {
            JTextField fDesc = new JTextField();
            JTextField fResp = new JTextField();
            JComboBox<TipoMovimentacao> fTipo = new JComboBox<>(TipoMovimentacao.values());
            JPanel f = new JPanel(new GridBagLayout());
            GridBagConstraints g = Ui.gbc();
            Ui.addFormRow(f, g, 0, "Descrição:", fDesc);
            Ui.addFormRow(f, g, 1, "Responsável:", fResp);
            Ui.addFormRow(f, g, 2, "Tipo:", fTipo);
            int res = JOptionPane.showConfirmDialog(panel, f, "Registrar Movimentação", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION && !fDesc.getText().isBlank()) {
                Movimentacao mv = new Movimentacao();
                mv.setDescricao(fDesc.getText().trim());
                mv.setResponsavel(fResp.getText().trim());
                mv.setTipo((TipoMovimentacao) fTipo.getSelectedItem());
                mv.setDataHora(java.time.LocalDateTime.now());
                try {
                    facade.registrarMovimentacao(p.getId(), mv);
                    m.setRowCount(0);
                    facade.listarMovimentacoes(p.getId()).forEach(v -> m.addRow(new Object[] {
                            v.getId(), v.getTipo(), v.getDataHora(), v.getResponsavel(), v.getDescricao()
                    }));
                } catch (Exception ex) {
                    Ui.erro(panel, ex.getMessage());
                }
            }
        });

        panel.add(new JScrollPane(t), BorderLayout.CENTER);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildDocumentosTab(Processo p) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        DefaultTableModel m = new DefaultTableModel(
                new String[] { "ID", "Título", "Tipo", "Data Upload", "Descrição Completa" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable t = Ui.buildTable(m);
        Runnable refresh = () -> {
            m.setRowCount(0);
            facade.listarDocumentos(p.getId()).forEach(d -> m.addRow(new Object[] {
                    d.getId(), d.getTitulo(), d.getTipo(), d.getDataUpload(), d.getDescricaoCompleta()
            }));
        };
        refresh.run();

        JTextField fTit = new JTextField(12);
        JTextField fDesc = new JTextField(12);
        JComboBox<TipoDocumento> fTipo = new JComboBox<>(TipoDocumento.values());
        JCheckBox chkAssinar = new JCheckBox("Assinar");
        JCheckBox chkProtocolar = new JCheckBox("Protocolar");
        JCheckBox chkUrgente = new JCheckBox("Urgente");
        JButton btnAdd = Ui.btnPrimary("Adicionar Doc.");
        JButton btnRem = Ui.btnDanger("Remover");

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        row1.add(new JLabel("Título:"));
        row1.add(fTit);
        row1.add(new JLabel("Descrição:"));
        row1.add(fDesc);
        row1.add(new JLabel("Tipo:"));
        row1.add(fTipo);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        row2.add(chkAssinar);
        row2.add(chkProtocolar);
        row2.add(chkUrgente);
        row2.add(btnAdd);
        row2.add(btnRem);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(row1);
        form.add(row2);

        btnAdd.addActionListener(e -> {
            if (fTit.getText().isBlank())
                return;
            Documento d = new Documento();
            d.setTitulo(fTit.getText().trim());
            d.setDescricao(fDesc.getText().trim());
            d.setTipo((TipoDocumento) fTipo.getSelectedItem());
            d.setDataUpload(java.time.LocalDate.now());
            try {
                facade.adicionarDocumento(p.getId(), d,
                        chkAssinar.isSelected(), chkProtocolar.isSelected(), chkUrgente.isSelected());
                refresh.run();
            } catch (Exception ex) {
                Ui.erro(panel, ex.getMessage());
            }
        });
        btnRem.addActionListener(e -> {
            int row = t.getSelectedRow();
            if (row < 0)
                return;
            Long id = (Long) m.getValueAt(t.convertRowIndexToModel(row), 0);
            try {
                facade.removerDocumento(id);
                refresh.run();
            } catch (Exception ex) {
                Ui.erro(panel, ex.getMessage());
            }
        });

        panel.add(new JScrollPane(t), BorderLayout.CENTER);
        panel.add(form, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildFinanceiroTab(Processo p) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(16, 16, 8, 16));
        GridBagConstraints gbc = Ui.gbc();

        String naoInf = "—";
        String valorCausa = p.getValorCausa() != null ? "R$ " + p.getValorCausa().toPlainString() : naoInf;
        String valorCondenacao = p.getValorCondenacao() != null ? "R$ " + p.getValorCondenacao().toPlainString()
                : naoInf;
        String honorSucumb = p.getHonorariosSucumbenciais() != null
                ? "R$ " + p.getHonorariosSucumbenciais().toPlainString()
                : naoInf;
        String resultado = p.getFavoravel() == null ? "Não definido"
                : p.getFavoravel() ? "Favorável" : "Desfavorável";

        int r = 0;
        Ui.addFormRow(panel, gbc, r++, "Valor da Causa:", new JLabel(valorCausa));
        Ui.addFormRow(panel, gbc, r++, "Valor da Condenação:", new JLabel(valorCondenacao));
        Ui.addFormRow(panel, gbc, r++, "Resultado:", new JLabel(resultado));
        Ui.addFormRow(panel, gbc, r++, "Hon. Sucumbenciais:", new JLabel(honorSucumb));

        return panel;
    }

    // ── Aba Assuntos (detalhes — somente leitura) ───────────────────────────
    private JPanel buildAssuntosTab(Processo p) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        DefaultTableModel m = new DefaultTableModel(
                new String[] { "Código", "Nome", "Principal" }, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        facade.listarAssuntosDoProcesso(p.getId()).forEach(ap -> m.addRow(new Object[] {
                ap.getAssunto().getCodItem(),
                ap.getAssunto().getNome(),
                ap.isPrincipal() ? "Sim" : "—"
        }));
        panel.add(new JScrollPane(Ui.buildTable(m)), BorderLayout.CENTER);
        return panel;
    }

    // ── Aba Assuntos (formulário — apenas modelo, sem persistência direta) ───
    private JPanel buildAssuntosFormTab(DefaultTableModel assuntosModel, JDialog dlg) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel buscaBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        JTextField fBusca = new JTextField(22);
        JButton btnBuscar = Ui.btnSecondary("Buscar");
        buscaBar.add(new JLabel("Buscar assunto CNJ:"));
        buscaBar.add(fBusca);
        buscaBar.add(btnBuscar);

        DefaultTableModel resultModel = new DefaultTableModel(
                new String[] { "Código", "Nome" }, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable tResult = Ui.buildTable(resultModel);

        JTable tAssuntos = Ui.buildTable(assuntosModel);
        tAssuntos.getColumnModel().getColumn(0).setMinWidth(0);
        tAssuntos.getColumnModel().getColumn(0).setMaxWidth(0);
        tAssuntos.getColumnModel().getColumn(0).setWidth(0);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(tResult), new JScrollPane(tAssuntos));
        split.setResizeWeight(0.5);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        JButton btnAdd = Ui.btnPrimary("Adicionar");
        JButton btnPrincipal = Ui.btnSuccess("Definir Como Principal");
        JButton btnRem = Ui.btnDanger("Remover");
        btns.add(btnAdd);
        btns.add(btnPrincipal);
        btns.add(btnRem);

        Runnable doBusca = () -> {
            String termo = fBusca.getText().trim();
            if (termo.isBlank())
                return;
            resultModel.setRowCount(0);
            facade.buscarAssuntosCNJ(termo)
                    .forEach(a -> resultModel.addRow(new Object[] { a.getCodItem(), a.getNome() }));
        };
        btnBuscar.addActionListener(e -> doBusca.run());
        fBusca.addActionListener(e -> doBusca.run());

        btnAdd.addActionListener(e -> {
            int row = tResult.getSelectedRow();
            if (row < 0) {
                Ui.aviso(dlg, "Selecione um assunto nos resultados.");
                return;
            }
            int mRow = tResult.convertRowIndexToModel(row);
            Long codItem = (Long) resultModel.getValueAt(mRow, 0);
            String nome = (String) resultModel.getValueAt(mRow, 1);
            for (int i = 0; i < assuntosModel.getRowCount(); i++) {
                if (codItem.equals(assuntosModel.getValueAt(i, 1))) {
                    Ui.aviso(dlg, "Este assunto já foi adicionado.");
                    return;
                }
            }
            boolean principal = assuntosModel.getRowCount() == 0;
            assuntosModel.addRow(new Object[] { null, codItem, nome, principal ? "Sim" : "Não" });
        });

        btnPrincipal.addActionListener(e -> {
            int row = tAssuntos.getSelectedRow();
            if (row < 0) {
                Ui.aviso(dlg, "Selecione um assunto.");
                return;
            }
            int mRow = tAssuntos.convertRowIndexToModel(row);
            for (int i = 0; i < assuntosModel.getRowCount(); i++)
                assuntosModel.setValueAt(i == mRow ? "Sim" : "—", i, 3);
        });

        btnRem.addActionListener(e -> {
            int row = tAssuntos.getSelectedRow();
            if (row < 0) {
                Ui.aviso(dlg, "Selecione um assunto.");
                return;
            }
            int mRow = tAssuntos.convertRowIndexToModel(row);
            assuntosModel.removeRow(mRow);
        });

        panel.add(buscaBar, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    private void excluir() {
        Processo p = selecionado();
        if (p == null)
            return;
        if (Ui.confirmar(this, "Excluir processo \"" + MaskedField.formatarNumeroProcesso(p.getNumero()) + "\"?")) {
            try {
                facade.deletarProcesso(p.getId());
                atualizar();
            } catch (Exception ex) {
                Ui.erro(this, ex.getMessage());
            }
        }
    }

    private Processo selecionado() {
        int row = table.getSelectedRow();
        if (row < 0) {
            Ui.aviso(this, "Selecione um processo.");
            return null;
        }
        return lista.get(table.convertRowIndexToModel(row));
    }
}
