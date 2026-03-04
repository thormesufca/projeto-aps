package br.edu.ufca.audiencias.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import br.edu.ufca.audiencias.padroes.estruturais.facade.SistemaJuridicoFacade;

import java.awt.*;
import java.time.Year;

public class MainWindow extends JFrame {

    public static final Color SIDEBAR_BG = new Color(26, 35, 126); // #1a237e
    public static final Color SIDEBAR_HOVER = new Color(40, 53, 147); // #283593
    public static final Color ACCENT = new Color(21, 101, 192); // #1565c0
    public static final Color TEXT_LIGHT = new Color(232, 234, 246); // #e8eaf6
    public static final Color TEXT_DARK = new Color(16, 18, 20); // #0F1214
    public static final Color BG_MAIN = null; // usar cor do tema (FlatLaf)
    public static final Color TITLE_COLOR = new Color(144, 202, 249); // #90CAF9 — azul claro legível no tema escuro

    private final SistemaJuridicoFacade facade = new SistemaJuridicoFacade();

    private final JPanel contentArea = new JPanel();
    private final CardLayout cardLayout = new CardLayout();

    
    private DashboardPanel dashboardPanel;
    private ClientePanel clientePanel;
    private AdvogadoPanel advogadoPanel;
    private ContratoPanel contratoPanel;
    private ProcessoPanel processoPanel;
    private AudienciaPanel audienciaPanel;
    private RelatorioPanel relatorioPanel;
    private EscritorioPanel escritorioPanel;

    private final JLabel statusLabel = new JLabel("Pronto");

    public MainWindow() {
        super("Sistema de Gestão de Audiências Jurídicas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 780);
        setMinimumSize(new Dimension(1000, 640));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));

        initPanels();
        add(buildMenuBar(), BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        add(buildContentArea(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        showPanel("Dashboard");
    }

    private void initPanels() {
        dashboardPanel   = new DashboardPanel(facade);
        clientePanel     = new ClientePanel(facade);
        advogadoPanel    = new AdvogadoPanel(facade);
        contratoPanel    = new ContratoPanel(facade);
        processoPanel    = new ProcessoPanel(facade);
        audienciaPanel   = new AudienciaPanel(facade);
        relatorioPanel   = new RelatorioPanel(facade);
        escritorioPanel  = new EscritorioPanel(facade);
    }

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        bar.setBackground(SIDEBAR_HOVER);
        bar.setBorder(new EmptyBorder(2, 8, 2, 8));

        JMenu mArq = menu("Arquivo");
        JMenu mCad = menu("Cadastros");
        JMenu mJur = menu("Jurídico");
        JMenu mRel = menu("Relatórios");
        JMenu mAjuda = menu("Ajuda");

        addItem(mArq, "Sair", () -> System.exit(0));
        addItem(mCad, "Escritório", () -> showPanel("Escritório"));
        addItem(mCad, "Clientes", () -> showPanel("Clientes"));
        addItem(mCad, "Advogados", () -> showPanel("Advogados"));
        addItem(mCad, "Contratos", () -> showPanel("Contratos"));
        addItem(mJur, "Processos", () -> showPanel("Processos"));
        addItem(mJur, "Audiências", () -> showPanel("Audiências"));
        addItem(mRel, "Gerar Relatório", () -> showPanel("Relatórios"));
        addItem(mAjuda, "Sobre", this::showAbout);

        bar.add(mArq);
        bar.add(mCad);
        bar.add(mJur);
        bar.add(mRel);
        bar.add(mAjuda);
        return bar;
    }

    private JMenu menu(String text) {
        JMenu m = new JMenu(text);
        m.setForeground(TEXT_LIGHT);
        return m;
    }

    private void addItem(JMenu menu, String label, Runnable action) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(e -> action.run());
        menu.add(item);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(10, 0, 10, 0));
        sidebar.setPreferredSize(new Dimension(190, 0));

        
        JLabel titleLbl = new JLabel("<html><center>SISTEMA<br>JURÍDICO</center></html>");
        titleLbl.setForeground(new Color(159, 168, 218));
        titleLbl.setFont(titleLbl.getFont().deriveFont(Font.BOLD, 11f));
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLbl.setBorder(new EmptyBorder(0, 5, 15, 5));
        sidebar.add(titleLbl);

        sidebar.add(sidebarSeparator("CADASTROS"));
        sidebar.add(navBtn("Dashboard", "Dashboard"));
        sidebar.add(navBtn("Escritório", "Escritório"));
        sidebar.add(navBtn("Clientes", "Clientes"));
        sidebar.add(navBtn("Advogados", "Advogados"));
        sidebar.add(navBtn("Contratos", "Contratos"));

        sidebar.add(sidebarSeparator("JURÍDICO"));
        sidebar.add(navBtn("Processos", "Processos"));
        sidebar.add(navBtn("Audiências", "Audiências"));

        sidebar.add(sidebarSeparator("FERRAMENTAS"));
        sidebar.add(navBtn("Relatórios", "Relatórios"));

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JLabel sidebarSeparator(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(159, 168, 218));
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 10f));
        lbl.setBorder(new EmptyBorder(10, 8, 4, 8));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JButton navBtn(String label, String panelName) {
        JButton btn = new JButton(label);
        btn.setForeground(TEXT_LIGHT);
        btn.setBackground(SIDEBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(9, 18, 9, 18));
        btn.setFont(btn.getFont().deriveFont(13f));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(190, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(SIDEBAR_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(SIDEBAR_BG);
            }
        });
        btn.addActionListener(e -> showPanel(panelName));
        return btn;
    }

    private JPanel buildContentArea() {
        contentArea.setLayout(cardLayout);
        contentArea.setBackground(BG_MAIN);
        contentArea.add(dashboardPanel,  "Dashboard");
        contentArea.add(escritorioPanel, "Escritório");
        contentArea.add(clientePanel,    "Clientes");
        contentArea.add(advogadoPanel,   "Advogados");
        contentArea.add(contratoPanel,   "Contratos");
        contentArea.add(processoPanel,   "Processos");
        contentArea.add(audienciaPanel,  "Audiências");
        contentArea.add(relatorioPanel,  "Relatórios");
        return contentArea;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(SIDEBAR_HOVER);
        bar.setBorder(new EmptyBorder(3, 10, 3, 10));
        statusLabel.setForeground(new Color(159, 168, 218));
        statusLabel.setFont(statusLabel.getFont().deriveFont(11f));
        bar.add(statusLabel, BorderLayout.WEST);
        JLabel ver = new JLabel("v1.0  |  UFCA APS " + Year.now());
        ver.setForeground(new Color(159, 168, 218));
        ver.setFont(ver.getFont().deriveFont(11f));
        bar.add(ver, BorderLayout.EAST);
        return bar;
    }

    public void showPanel(String name) {
        cardLayout.show(contentArea, name);
        statusLabel.setText(name);
        switch (name) {
            case "Dashboard"  -> dashboardPanel.atualizar();
            case "Escritório" -> escritorioPanel.atualizar();
            case "Clientes"   -> clientePanel.atualizar();
            case "Advogados"  -> advogadoPanel.atualizar();
            case "Contratos"  -> contratoPanel.atualizar();
            case "Processos"  -> processoPanel.atualizar();
            case "Audiências" -> audienciaPanel.atualizar();
        }
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "Sistema de Gestão de Processos e Audiências Jurídicas\n" +
                        "UFCA — Análise e Projeto de Sistemas\n",
                "Sobre", JOptionPane.INFORMATION_MESSAGE);
    }
}
