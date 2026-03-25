package br.edu.ufca.audiencias.ui;

import br.edu.ufca.audiencias.models.Audiencia;
import br.edu.ufca.audiencias.models.enums.StatusAudiencia;
import br.edu.ufca.audiencias.padroes.estruturais.facade.SistemaJuridicoFacade;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AudienciaPanel extends JPanel {

    private final SistemaJuridicoFacade facade;
    private int mes = LocalDate.now().getMonthValue();
    private int ano = LocalDate.now().getYear();
    private Audiencia audienciaSelecionada;

    private final JLabel lblMesAno = new JLabel("", SwingConstants.CENTER);
    private final JPanel calendarGrid = new JPanel();

    private JButton btnCancelar, btnRemarcar, btnResult, btnExcluir, btnObs;
    private JLabel lblSelecionada;

    private static final String[] DIAS_SEMANA = { "Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb" };

    private static final Color COR_AGENDADA = new Color(21, 101, 192);
    private static final Color COR_REALIZADA = new Color(46, 125, 50);
    private static final Color COR_CANCELADA = new Color(198, 40, 40);
    private static final Color COR_VENCIDA = new Color(100, 60, 0); // AGENDADA com data passada

    private static final String[] NOMES_MES = {
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    };

    public AudienciaPanel(SistemaJuridicoFacade facade) {
        this.facade = facade;
        setBackground(Color.BLACK);
        setLayout(new BorderLayout(0, 8));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCalendarArea(), BorderLayout.CENTER);
        add(buildToolbar(), BorderLayout.SOUTH);

        atualizarBotoes();
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        top.add(Ui.pageTitle("Audiências — Calendário"), BorderLayout.NORTH);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        nav.setOpaque(false);

        JButton btnMesAnterior = Ui.btnSecondary("< Mês Anterior");
        JButton btnMesProximo = Ui.btnSecondary("Próximo Mês >");
        JButton btnAnoAnterior = Ui.btnSecondary("<< Ano Anterior");
        JButton btnAnoProximo = Ui.btnSecondary("Próximo Ano >>");
        btnMesAnterior.addActionListener(e -> navegarMes(-1));
        btnMesProximo.addActionListener(e -> navegarMes(1));
        btnAnoAnterior.addActionListener(e -> navegarAno(-1));
        btnAnoProximo.addActionListener(e -> navegarAno(1));

        lblMesAno.setFont(lblMesAno.getFont().deriveFont(Font.BOLD, 16f));
        lblMesAno.setForeground(MainWindow.TITLE_COLOR);
        lblMesAno.setPreferredSize(new Dimension(200, 28));
        nav.add(btnAnoAnterior);
        nav.add(btnMesAnterior);
        nav.add(lblMesAno);
        nav.add(btnMesProximo);
        nav.add(btnAnoProximo);

        JButton btnHoje = Ui.btnSecondary("Hoje");
        btnHoje.addActionListener(e -> {
            mes = LocalDate.now().getMonthValue();
            ano = LocalDate.now().getYear();
            atualizar();
        });
        nav.add(btnHoje);

        top.add(nav, BorderLayout.CENTER);
        return top;
    }

    private JScrollPane buildCalendarArea() {
        calendarGrid.setBackground(Color.BLACK);
        calendarGrid.setLayout(new GridLayout(0, 7, 2, 2));
        JScrollPane scroll = new JScrollPane(calendarGrid);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scroll.getViewport().setBackground(Color.BLACK);
        return scroll;
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 8));
        bar.setOpaque(false);

        lblSelecionada = new JLabel("Nenhuma audiência selecionada");
        lblSelecionada.setForeground(Color.GRAY);
        lblSelecionada.setFont(lblSelecionada.getFont().deriveFont(12f));

        btnCancelar = Ui.btnDanger("Cancelar");
        btnRemarcar = Ui.btnSecondary("Remarcar");
        btnResult = Ui.btnSuccess("Registrar Resultado");
        btnExcluir = Ui.btnDanger("Excluir");
        btnObs = Ui.btnSecondary("Observações");
        JButton btnAtual = Ui.btnSecondary("Atualizar");

        btnCancelar.addActionListener(e -> cancelar());
        btnRemarcar.addActionListener(e -> remarcar());
        btnResult.addActionListener(e -> registrarResultado());
        btnExcluir.addActionListener(e -> excluir());
        btnObs.addActionListener(e -> editarObservacoes());
        btnAtual.addActionListener(e -> atualizar());

        bar.add(lblSelecionada);
        bar.add(Box.createHorizontalStrut(10));
        bar.add(btnCancelar);
        bar.add(btnRemarcar);
        bar.add(btnResult);
        bar.add(btnExcluir);
        bar.add(btnObs);
        bar.add(Box.createHorizontalStrut(10));
        bar.add(btnAtual);
        return bar;
    }

    public void atualizar() {
        audienciaSelecionada = null;
        atualizarBotoes();
        renderizarCalendario();
    }

    private void navegarMes(int delta) {
        YearMonth ym = YearMonth.of(ano, mes).plusMonths(delta);
        mes = ym.getMonthValue();
        ano = ym.getYear();
        atualizar();
    }

    private void navegarAno(int delta) {
        YearMonth ym = YearMonth.of(ano, mes).plusYears(delta);
        mes = ym.getMonthValue();
        ano = ym.getYear();
        atualizar();
    }

    private void renderizarCalendario() {
        calendarGrid.removeAll();
        lblMesAno.setText(NOMES_MES[mes - 1] + "  " + ano);

        for (String d : DIAS_SEMANA) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 12f));
            lbl.setOpaque(true);
            lbl.setBackground(MainWindow.SIDEBAR_BG);
            lbl.setForeground(MainWindow.TEXT_LIGHT);
            lbl.setBorder(new EmptyBorder(6, 4, 6, 4));
            calendarGrid.add(lbl);
        }

        List<Audiencia> lista;
        try {
            lista = facade.listarAudienciasPorMes(mes, ano, true);
        } catch (Exception ex) {
            lista = List.of();
        }

        Map<Integer, List<Audiencia>> porDia = lista.stream()
                .filter(a -> a.getDataHora() != null)
                .collect(Collectors.groupingBy(a -> a.getDataHora().getDayOfMonth()));

        LocalDate primeiroDia = LocalDate.of(ano, mes, 1);
        int offset = primeiroDia.getDayOfWeek().getValue() % 7;
        for (int i = 0; i < offset; i++) {
            calendarGrid.add(celulaVazia());
        }

        int diasNoMes = YearMonth.of(ano, mes).lengthOfMonth();
        for (int dia = 1; dia <= diasNoMes; dia++) {
            calendarGrid.add(buildCelulaDia(dia, porDia.getOrDefault(dia, List.of())));
        }

        calendarGrid.revalidate();
        calendarGrid.repaint();
    }

    private JPanel celulaVazia() {
        JPanel p = new JPanel();
        p.setBackground(new Color(248, 248, 248));
        p.setBorder(new LineBorder(new Color(220, 220, 220)));
        return p;
    }

    private JPanel buildCelulaDia(int dia, List<Audiencia> audiencias) {
        JPanel cell = new JPanel();
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        cell.setBackground(Color.DARK_GRAY);
        cell.setBorder(new LineBorder(new Color(210, 210, 210)));
        cell.setMinimumSize(new Dimension(80, 80));
        cell.setPreferredSize(new Dimension(120, 90));

        JLabel dayLbl = new JLabel(String.valueOf(dia));
        dayLbl.setFont(dayLbl.getFont().deriveFont(Font.BOLD, 12f));
        dayLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        dayLbl.setBorder(new EmptyBorder(3, 5, 2, 3));

        LocalDate hoje = LocalDate.now();
        if (dia == hoje.getDayOfMonth() && mes == hoje.getMonthValue() && ano == hoje.getYear()) {
            cell.setBackground(Color.LIGHT_GRAY);
            dayLbl.setForeground(MainWindow.ACCENT);
        }

        cell.add(dayLbl);

        for (Audiencia a : audiencias) {
            cell.add(buildChipAudiencia(a));
        }

        cell.add(Box.createVerticalGlue());
        return cell;
    }

    private JButton buildChipAudiencia(Audiencia a) {
        String hora = "";
        if (a.getDataHora() != null) {
            hora = String.format("%02d:%02d ", a.getDataHora().getHour(), a.getDataHora().getMinute());
        }
        String tipoStr = a.getTipo() != null ? a.getTipo().toString() : "";
        JButton btn = new JButton("<html><b>" + hora + "</b>" + tipoStr + "</html>");

        Color bg = COR_AGENDADA;
        if (a.getStatus() != null) {
            bg = switch (a.getStatus()) {
                case REALIZADA -> COR_REALIZADA;
                case CANCELADA -> COR_CANCELADA;
                default -> COR_AGENDADA;
            };
        }
        if (bg == COR_AGENDADA && a.getDataHora() != null && a.getDataHora().isBefore(LocalDateTime.now())) {
            bg = COR_VENCIDA;
        }

        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(btn.getFont().deriveFont(10f));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMargin(new Insets(1, 4, 1, 4));

        String processo = a.getProcesso() != null ? MaskedField.formatarNumeroProcesso(a.getProcesso().getNumero())
                : "-";
        btn.setToolTipText("Processo: " + processo + " | " + a.getStatus() + " | " + a.getDescricao());
        btn.addActionListener(e -> selecionarAudiencia(a));
        return btn;
    }

    private void selecionarAudiencia(Audiencia a) {
        audienciaSelecionada = a;
        atualizarBotoes();
    }

    private void atualizarBotoes() {
        boolean sel = audienciaSelecionada != null;
        boolean editavel = sel
                && audienciaSelecionada.getStatus() != StatusAudiencia.CANCELADA
                && audienciaSelecionada.getStatus() != StatusAudiencia.REALIZADA;
        boolean vencida = editavel
                && audienciaSelecionada.getStatus() == StatusAudiencia.AGENDADA
                && audienciaSelecionada.getDataHora() != null
                && audienciaSelecionada.getDataHora().isBefore(LocalDateTime.now());

        if (!sel) {
            lblSelecionada.setText("Nenhuma audiência selecionada");
            lblSelecionada.setForeground(Color.GRAY);
        } else {
            String proc = audienciaSelecionada.getProcesso() != null
                    ? MaskedField.formatarNumeroProcesso(audienciaSelecionada.getProcesso().getNumero())
                    : "-";
            if (vencida) {
                lblSelecionada.setText("[" + proc + "] " + audienciaSelecionada.getTipo()
                        + " — data vencida, finalize o registro");
                lblSelecionada.setForeground(COR_VENCIDA);
            } else {
                lblSelecionada.setText("Selecionada: [" + proc + "] " + audienciaSelecionada.getTipo()
                        + " — " + audienciaSelecionada.getStatus());
                lblSelecionada.setForeground(MainWindow.ACCENT);
            }
        }
        btnCancelar.setEnabled(editavel);
        btnRemarcar.setEnabled(editavel);
        btnResult.setEnabled(editavel);
        btnExcluir.setEnabled(sel);
        btnObs.setEnabled(sel);
    }

    // ── Ações ─────────────────────────────────────────────────────────────────

    private void cancelar() {
        if (audienciaSelecionada == null)
            return;
        String motivo = JOptionPane.showInputDialog(this, "Motivo do cancelamento:");
        if (motivo == null)
            return;
        try {
            facade.cancelarAudiencia(audienciaSelecionada.getId(), motivo);
            atualizar();
        } catch (Exception ex) {
            Ui.erro(this, ex.getMessage());
        }
    }

    private void remarcar() {
        if (audienciaSelecionada == null)
            return;
        DateTimePicker picker = new DateTimePicker();
        int res = JOptionPane.showConfirmDialog(this, picker,
                "Nova data/hora da audiência", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION)
            return;
        try {
            facade.remarcarAudiencia(audienciaSelecionada.getId(), picker.getLocalDateTime());
            atualizar();
        } catch (Exception ex) {
            Ui.erro(this, ex.getMessage());
        }
    }

    private void registrarResultado() {
        if (audienciaSelecionada == null)
            return;
        String resultado = JOptionPane.showInputDialog(this, "Resultado da audiência:");
        if (resultado == null)
            return;
        try {
            facade.registrarResultadoAudiencia(audienciaSelecionada.getId(), resultado);
            atualizar();
        } catch (Exception ex) {
            Ui.erro(this, ex.getMessage());
        }
    }

    private void editarObservacoes() {
        if (audienciaSelecionada == null)
            return;
        String atual = String.join("\n", audienciaSelecionada.getObservacoes());
        JTextArea ta = new JTextArea(atual, 8, 40);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        int opt = JOptionPane.showConfirmDialog(this, new JScrollPane(ta),
                "Observações (uma por linha)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opt != JOptionPane.OK_OPTION)
            return;
        List<String> obs = new ArrayList<>();
        for (String linha : ta.getText().split("\n")) {
            if (!linha.isBlank())
                obs.add(linha);
        }
        try {
            facade.editarObservacoesAudiencia(audienciaSelecionada.getId(), obs);
            atualizar();
        } catch (Exception ex) {
            Ui.erro(this, ex.getMessage());
        }
    }

    private void excluir() {
        if (audienciaSelecionada == null)
            return;
        if (Ui.confirmar(this, "Excluir audiência #" + audienciaSelecionada.getId() + "?")) {
            try {
                facade.deletarAudiencia(audienciaSelecionada.getId());
                atualizar();
            } catch (Exception ex) {
                Ui.erro(this, ex.getMessage());
            }
        }
    }
}
