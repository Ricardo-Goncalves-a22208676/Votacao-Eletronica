package com.votacao.ui;

import com.votacao.model.*;
import com.votacao.service.VotacaoService;
import com.votacao.dao.*;
import com.votacao.utils.PasswordUtils;
import com.votacao.utils.NIFUtils;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
// import java.awt.event.MouseAdapter; // Not explicitly used, can be removed if no direct mouse listeners
// import java.awt.event.MouseEvent; // Not explicitly used
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class AdminGUI extends JFrame {
    // Modern baby blue color palette
    private static final Color PRIMARY_COLOR = new Color(122, 184, 253); // #7AB8FD
    private static final Color PRIMARY_DARK = new Color(96, 165, 250);
    private static final Color PRIMARY_LIGHT = new Color(147, 197, 253);
    private static final Color ACCENT_COLOR = new Color(59, 130, 246);
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);
    private static final Color WARNING_COLOR = new Color(245, 158, 11);
    private static final Color ERROR_COLOR = new Color(239, 68, 68); // Used for Sair
    private static final Color SECONDARY_COLOR = new Color(148, 163, 184);
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(30, 41, 59);
    private static final Color TEXT_LIGHT = new Color(71, 85, 105);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color HOVER_COLOR = new Color(241, 245, 249);

    // Corner Radii & Thicknesses
    private static final int PANEL_CORNER_RADIUS = 20;
    private static final int INPUT_CORNER_RADIUS = 10;
    private static final int INPUT_BORDER_THICKNESS = 1;
    private static final int SCROLLPANE_BORDER_THICKNESS = 1;
    private static final int BUTTON_CORNER_RADIUS = 12;
    private static final int TAB_CORNER_RADIUS = 12;
    private static final int CARD_RESULT_CORNER_RADIUS = 15;

    private JTabbedPane tabbedPane;
    private JTable eleitoresTable;
    private JTable candidatosTable;
    private JPanel resultadosDisplayPanel;
    private EleitorDAO eleitorDAO;
    private CandidatoDAO candidatoDAO;
    private VotoDAO votoDAO;
    private VotacaoService votacaoService;

    public AdminGUI() {
        this.eleitorDAO = new EleitorDAO();
        this.candidatoDAO = new CandidatoDAO();
        this.votoDAO = new VotoDAO();
        this.votacaoService = new VotacaoService();
        initializeUI();
        carregarDados();
    }

    private void initializeUI() {
        setTitle("Administração - Sistema de Votação");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BACKGROUND_COLOR);

        JPanel headerPanel = createHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                GradientPaint gradient = new GradientPaint(
                        0,
                        0,
                        PRIMARY_COLOR,
                        getWidth(),
                        getHeight(),
                        PRIMARY_DARK
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(0, 100));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        leftPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Painel de Administração");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        JLabel subtitleLabel = new JLabel("Sistema de Votação Eletrónica");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(new EmptyBorder(25, 0, 0, 0));
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);
        leftPanel.add(textPanel);

        JPanel rightPanel =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 30));
        rightPanel.setOpaque(false);

        JButton startButton = createModernButton(
                "Iniciar Votação",
                SUCCESS_COLOR,
                SUCCESS_COLOR.darker(),
                SUCCESS_COLOR.brighter().brighter() // Brighter hover
        );
        startButton.setPreferredSize(new Dimension(130, 40));

        JButton stopButton = createModernButton(
                "Parar Votação",
                WARNING_COLOR,
                WARNING_COLOR.darker(),
                WARNING_COLOR.brighter().brighter() // Brighter hover
        );
        stopButton.setPreferredSize(new Dimension(130, 40));

        JButton logoutButton = createModernButton(
                "Sair",
                ERROR_COLOR,
                ERROR_COLOR.darker(),
                ERROR_COLOR.brighter() // Standard brighter hover for error
        );
        logoutButton.setPreferredSize(new Dimension(130, 40));

        startButton.addActionListener(e -> iniciarVotacao());
        stopButton.addActionListener(e -> encerrarVotacao());
        logoutButton.addActionListener(e -> fazerLogout());

        rightPanel.add(startButton);
        rightPanel.add(stopButton);
        rightPanel.add(logoutButton);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        tabbedPane = new JTabbedPane();
        tabbedPane.setUI(new ModernTabbedPaneUI());
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());

        tabbedPane.addTab(
                "👥 Eleitores",
                null,
                createEleitoresPanel(),
                "Gerir eleitores do sistema"
        );
        tabbedPane.addTab(
                "🗳 Candidatos",
                null,
                createCandidatosPanel(),
                "Gerir candidatos da eleição"
        );
        tabbedPane.addTab(
                "📊 Resultados",
                null,
                createResultadosPanel(),
                "Visualizar resultados da votação"
        );

        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        return contentPanel;
    }

    static class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color backgroundColor;

        public RoundedPanel(LayoutManager layout, int radius, Color bgColor) {
            super(layout);
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(backgroundColor);
            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    cornerRadius,
                    cornerRadius
            );
            g2.dispose();
        }
    }

    static class RoundedBorder extends AbstractBorder {
        private Color color;
        private int thickness;
        private int radius;
        private Insets insets;

        public RoundedBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
            int pad = thickness + radius / 3;
            this.insets = new Insets(pad, pad, pad, pad);
        }

        @Override
        public void paintBorder(
                Component c,
                Graphics g,
                int x,
                int y,
                int width,
                int height
        ) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(
                    x + thickness / 2,
                    y + thickness / 2,
                    width - thickness,
                    height - thickness,
                    radius,
                    radius
            );
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return (Insets) insets.clone();
        }

        @Override
        public Insets getBorderInsets(Component c, Insets newInsets) {
            newInsets.left = insets.left;
            newInsets.top = insets.top;
            newInsets.right = insets.right;
            newInsets.bottom = insets.bottom;
            return newInsets;
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    private class ModernTabbedPaneUI extends BasicTabbedPaneUI {
        @Override
        protected void installDefaults() {
            super.installDefaults();
            tabAreaInsets = new Insets(10, 20, 0, 20);
            tabInsets = new Insets(12, 25, 12, 25);
            selectedTabPadInsets = new Insets(0, 0, 0, 0);
            contentBorderInsets = new Insets(0,0,0,0);
        }

        @Override
        protected LayoutManager createLayoutManager() {
            return new TabbedPaneLayout();
        }

        protected class TabbedPaneLayout extends BasicTabbedPaneUI.TabbedPaneLayout {
            @Override
            protected void calculateTabRects(int tabPlacement, int tabCount) {
                super.calculateTabRects(tabPlacement, tabCount);
                FontMetrics metrics = getFontMetrics();
                int minWidth = 160;
                for (int i = 0; i < tabCount; i++) {
                    if (rects[i].width < minWidth) {
                        rects[i].width = minWidth;
                    }
                }
                int x = tabAreaInsets.left;
                for (int i = 0; i < tabCount; i++) {
                    rects[i].x = x;
                    x += rects[i].width;
                }
            }
        }

        @Override
        protected void paintTabArea(
                Graphics g,
                int tabPlacement,
                int selectedIndex
        ) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(BACKGROUND_COLOR);
            g2.fillRect(
                    0,
                    0,
                    tabPane.getWidth(),
                    calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight)
            );
            super.paintTabArea(g, tabPlacement, selectedIndex);
            g2.dispose();
        }

        @Override
        protected void paintTab(
                Graphics g,
                int tabPlacement,
                Rectangle[] rects,
                int tabIndex,
                Rectangle iconRect,
                Rectangle textRect
        ) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            Rectangle tabRect = rects[tabIndex];
            boolean isSelected = tabIndex == tabPane.getSelectedIndex();
            Color bgColor;
            Color textColor;

            if (isSelected) {
                bgColor = PRIMARY_COLOR;
                textColor = Color.WHITE;
            } else {
                bgColor = HOVER_COLOR;
                textColor = TEXT_COLOR;
            }

            g2.setColor(bgColor);
            g2.fillRoundRect(
                    tabRect.x,
                    tabRect.y,
                    tabRect.width,
                    tabRect.height,
                    TAB_CORNER_RADIUS,
                    TAB_CORNER_RADIUS
            );

            g2.setColor(textColor);
            g2.setFont(tabPane.getFont().deriveFont(Font.BOLD));

            String title = tabPane.getTitleAt(tabIndex);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(title);
            int x = tabRect.x + (tabRect.width - textWidth) / 2;
            int y =
                    tabRect.y +
                            (tabRect.height - fm.getHeight()) / 2 +
                            fm.getAscent();
            g2.drawString(title, x, y);

            if (isSelected) {
                g2.setColor(PRIMARY_DARK);
                g2.fillRect(
                        tabRect.x + 5,
                        tabRect.y + tabRect.height - 4,
                        tabRect.width - 10,
                        4
                );
            }
            g2.dispose();
        }

        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            // Do not paint default content border
        }

        @Override
        protected int calculateTabHeight(
                int tabPlacement,
                int tabIndex,
                int fontHeight
        ) {
            return Math.max(fontHeight + 28, 55);
        }

        @Override
        protected int calculateTabWidth(
                int tabPlacement,
                int tabIndex,
                FontMetrics metrics
        ) {
            String title = tabPane.getTitleAt(tabIndex);
            return Math.max(metrics.stringWidth(title) + 50, 160);
        }
    }

    private JPanel createEleitoresPanel() {
        JPanel panel = new RoundedPanel(
                new BorderLayout(20, 20),
                PANEL_CORNER_RADIUS,
                CARD_COLOR
        );
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        JPanel toolbar = createToolbar(
                new String[] { "Adicionar", "Editar", "Remover", "Atualizar" },
                new Runnable[] {
                        this::adicionarEleitor,
                        this::editarEleitor,
                        this::removerEleitor,
                        this::atualizarTabelaEleitores,
                }
        );
        panel.add(toolbar, BorderLayout.NORTH);
        String[] columns = { "ID", "Nome", "NIF", "Status" };
        eleitoresTable = createModernTable(columns);
        JScrollPane scrollPane = createModernScrollPane(eleitoresTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCandidatosPanel() {
        JPanel panel = new RoundedPanel(
                new BorderLayout(20, 20),
                PANEL_CORNER_RADIUS,
                CARD_COLOR
        );
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        JPanel toolbar = createToolbar(
                new String[] { "Adicionar", "Editar", "Remover", "Atualizar" },
                new Runnable[] {
                        this::adicionarCandidato,
                        this::editarCandidato,
                        this::removerCandidato,
                        this::atualizarTabelaCandidatos,
                }
        );
        panel.add(toolbar, BorderLayout.NORTH);
        String[] columns = { "ID", "Nome", "Partido" };
        candidatosTable = createModernTable(columns);
        JScrollPane scrollPane = createModernScrollPane(candidatosTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createResultadosPanel() {
        JPanel panel = new RoundedPanel(
                new BorderLayout(20, 20),
                PANEL_CORNER_RADIUS,
                CARD_COLOR
        );
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        JPanel toolbar = createToolbar(
                new String[] { "Atualizar", "Exportar" },
                new Runnable[] {
                        this::atualizarDisplayResultados,
                        this::exportarResultados,
                }
        );
        panel.add(toolbar, BorderLayout.NORTH);
        resultadosDisplayPanel = new JPanel();
        resultadosDisplayPanel.setLayout(
                new BoxLayout(resultadosDisplayPanel, BoxLayout.Y_AXIS)
        );
        resultadosDisplayPanel.setBackground(CARD_COLOR);
        JScrollPane scrollPane = new JScrollPane(resultadosDisplayPanel);
        scrollPane.setBorder(
                new RoundedBorder(
                        BORDER_COLOR,
                        SCROLLPANE_BORDER_THICKNESS,
                        PANEL_CORNER_RADIUS
                )
        );
        scrollPane.getViewport().setBackground(CARD_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createToolbar(String[] buttonTexts, Runnable[] actions) {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                        new EmptyBorder(15, 0, 15, 0)
                )
        );
        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = createModernButton(buttonTexts[i]); // Uses the simple version
            int finalI = i;
            button.addActionListener(e -> actions[finalI].run());
            toolbar.add(button);
        }
        return toolbar;
    }

    // Simple version for default themed buttons (e.g., toolbar)
    private JButton createModernButton(String text) {
        return createModernButton(
                text,
                PRIMARY_COLOR,
                PRIMARY_DARK,
                PRIMARY_LIGHT
        );
    }

    // Detailed version for custom colored buttons (e.g., header)
    private JButton createModernButton(
            String text,
            Color normalBgColor,
            Color pressedBgColor,
            Color hoverBgColor
    ) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                Color currentBgColor;
                Color textColor = Color.WHITE;

                if (getModel().isPressed()) {
                    currentBgColor = pressedBgColor;
                } else if (getModel().isRollover()) {
                    currentBgColor = hoverBgColor;
                } else {
                    currentBgColor = normalBgColor;
                }
                g2.setColor(currentBgColor);
                g2.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        BUTTON_CORNER_RADIUS,
                        BUTTON_CORNER_RADIUS
                );
                g2.setColor(textColor);
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int x = (getWidth() - textWidth) / 2;
                int y =
                        (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(110, 38)); // Default size
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JTable createModernTable(String[] columns) {
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(50);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(CARD_COLOR);
        table.setSelectionBackground(PRIMARY_COLOR.brighter());
        table.setSelectionForeground(TEXT_COLOR);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(BACKGROUND_COLOR);
        header.setForeground(TEXT_COLOR);
        header.setPreferredSize(new Dimension(0, 55));
        header.setBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_LIGHT)
        );
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(
                SwingConstants.CENTER
        );
        table.setDefaultRenderer(Object.class, new ModernTableCellRenderer());
        return table;
    }

    private JScrollPane createModernScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(
                new RoundedBorder(
                        BORDER_COLOR,
                        SCROLLPANE_BORDER_THICKNESS,
                        PANEL_CORNER_RADIUS
                )
        );
        scrollPane.getViewport().setBackground(CARD_COLOR);
        scrollPane.setBackground(CARD_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    class ModernTableCellRenderer extends DefaultTableCellRenderer {
        public ModernTableCellRenderer() {
            super();
            setHorizontalAlignment(SwingConstants.CENTER);
        }
        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            Component c = super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column
            );
            if (isSelected) {
                c.setBackground(PRIMARY_LIGHT.brighter());
                c.setForeground(TEXT_COLOR);
            } else {
                if (row % 2 == 0) {
                    c.setBackground(CARD_COLOR);
                } else {
                    c.setBackground(HOVER_COLOR);
                }
                c.setForeground(TEXT_COLOR);
            }
            setBorder(new EmptyBorder(15, 20, 15, 20));
            if (table == eleitoresTable && column == 3 && value != null) {
                if (value.toString().equals("Votou")) {
                    setForeground(SUCCESS_COLOR);
                } else {
                    setForeground(WARNING_COLOR);
                }
                setFont(getFont().deriveFont(Font.BOLD));
            } else {
                setFont(getFont().deriveFont(Font.PLAIN));
            }
            return c;
        }
    }

    private void carregarDados() {
        atualizarTabelaEleitores();
        atualizarTabelaCandidatos();
        atualizarDisplayResultados();
    }

    private void atualizarTabelaEleitores() {
        DefaultTableModel model = (DefaultTableModel) eleitoresTable.getModel();
        model.setRowCount(0);
        List<Eleitor> eleitores = eleitorDAO.listarTodos();
        for (Eleitor eleitor : eleitores) {
            model.addRow(
                    new Object[] {
                            eleitor.getId(),
                            eleitor.getNome(),
                            eleitor.getNumeroEleitor(),
                            eleitor.isVotou() ? "Votou" : "Não Votou",
                    }
            );
        }
    }

    private void atualizarTabelaCandidatos() {
        DefaultTableModel model = (DefaultTableModel) candidatosTable.getModel();
        model.setRowCount(0);
        List<Candidato> candidatos = candidatoDAO.listarTodos();
        for (Candidato candidato : candidatos) {
            model.addRow(
                    new Object[] {
                            candidato.getId(),
                            candidato.getNome(),
                            candidato.getPartido(),
                    }
            );
        }
    }

    private void atualizarDisplayResultados() {
        resultadosDisplayPanel.removeAll();
        List<Candidato> candidatos = votacaoService.listarCandidatos();
        int totalVotos = votoDAO.contarTotalVotos();
        List<ResultadoData> resultadosDataList = new ArrayList<>();
        for (Candidato candidato : candidatos) {
            int votos = votoDAO.contarVotosPorCandidato(candidato.getId());
            double percentagem = totalVotos > 0
                    ? (votos * 100.0) / totalVotos
                    : 0;
            resultadosDataList.add(
                    new ResultadoData(
                            candidato.getNome(),
                            candidato.getPartido(),
                            votos,
                            percentagem
                    )
            );
        }
        resultadosDataList.sort(
                Comparator.comparingInt(ResultadoData::getVotos).reversed()
        );
        for (ResultadoData resData : resultadosDataList) {
            resultadosDisplayPanel.add(new ResultadoCardPanel(resData));
            resultadosDisplayPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        resultadosDisplayPanel.revalidate();
        resultadosDisplayPanel.repaint();
    }

    private static class ResultadoData {
        String nomeCandidato;
        String partido;
        int votos;
        double percentagem;
        public ResultadoData(
                String nomeCandidato,
                String partido,
                int votos,
                double percentagem
        ) {
            this.nomeCandidato = nomeCandidato;
            this.partido = partido;
            this.votos = votos;
            this.percentagem = percentagem;
        }
        public int getVotos() { return votos; }
    }

    private class CustomProgressBar extends JPanel {
        private double percentage;
        private int voteCount;
        public CustomProgressBar(double percentage, int voteCount) {
            this.percentage = percentage;
            this.voteCount = voteCount;
            setOpaque(false);
            setPreferredSize(new Dimension(100, 40));
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            int width = getWidth();
            int height = getHeight();
            int cornerRadius = height;
            g2.setColor(HOVER_COLOR);
            g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);
            int progressWidth = (int) (width * (percentage / 100.0));
            g2.setColor(SUCCESS_COLOR);
            if (progressWidth > 0) {
                if (progressWidth < cornerRadius) {
                    g2.fillOval(0,0, progressWidth, height);
                } else {
                    g2.fillRoundRect(
                            0,
                            0,
                            progressWidth,
                            height,
                            cornerRadius,
                            cornerRadius
                    );
                }
            }
            String text = String.format(
                    "%d Votos (%.1f%%)",
                    voteCount,
                    percentage
            );
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textX;
            int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
            if (progressWidth < width * 0.4 && progressWidth + 15 + textWidth < width) {
                textX = progressWidth + 15;
                g2.setColor(TEXT_COLOR);
            } else {
                textX = (width - textWidth) / 2;
                if (textX > progressWidth || textX + textWidth < progressWidth/2 ) {
                    g2.setColor(TEXT_COLOR);
                } else {
                    g2.setColor(Color.WHITE);
                }
            }
            if (textX < 10) textX = 10;
            g2.drawString(text, textX, textY);
            g2.dispose();
        }
    }

    private class ResultadoCardPanel extends JPanel {
        private ResultadoData data;
        public ResultadoCardPanel(ResultadoData data) {
            this.data = data;
            setLayout(new BorderLayout(10, 5));
            setOpaque(false);
            setBackground(CARD_COLOR);
            setBorder(
                    BorderFactory.createCompoundBorder(
                            new RoundedBorder(BORDER_COLOR, 1, CARD_RESULT_CORNER_RADIUS),
                            new EmptyBorder(15, 20, 15, 20)
                    )
            );
            setPreferredSize(new Dimension(getWidth(), 145));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 145));

            JLabel partyNameLabel = new JLabel(data.partido);
            partyNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
            partyNameLabel.setForeground(TEXT_COLOR);
            JLabel candidateNameLabel = new JLabel(data.nomeCandidato);
            candidateNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            candidateNameLabel.setForeground(TEXT_LIGHT);
            JPanel textInfoPanel = new JPanel();
            textInfoPanel.setOpaque(false);
            textInfoPanel.setLayout(new BoxLayout(textInfoPanel, BoxLayout.Y_AXIS));
            partyNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            candidateNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            textInfoPanel.add(partyNameLabel);
            textInfoPanel.add(Box.createVerticalStrut(4));
            textInfoPanel.add(candidateNameLabel);
            add(textInfoPanel, BorderLayout.NORTH);

            CustomProgressBar progressBarWithText = new CustomProgressBar(data.percentagem, data.votos);
            JPanel progressWrapperPanel = new JPanel(new BorderLayout());
            progressWrapperPanel.setOpaque(false);
            progressWrapperPanel.setBorder(new EmptyBorder(20, 0, 10, 0));
            progressWrapperPanel.add(progressBarWithText, BorderLayout.CENTER);
            add(progressWrapperPanel, BorderLayout.CENTER);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), CARD_RESULT_CORNER_RADIUS, CARD_RESULT_CORNER_RADIUS);
            g2.dispose();
        }
    }

    private void adicionarEleitor() {
        JPanel panel = createModernFormPanel();
        JTextField nomeField = createModernTextField();
        JTextField nifField = createModernTextField();
        JTextField usernameField = createModernTextField();
        JPasswordField passwordField = createModernPasswordField();
        panel.add(createFormRow("Nome:", nomeField));
        panel.add(createFormRow("NIF:", nifField));
        panel.add(createFormRow("Username:", usernameField));
        panel.add(createFormRow("Password:", passwordField));
        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Adicionar Eleitor",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (result == JOptionPane.OK_OPTION) {
            String nome = nomeField.getText().trim();
            String nif = nifField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (
                    nome.isEmpty() ||
                            nif.isEmpty() ||
                            username.isEmpty() ||
                            password.isEmpty()
            ) {
                showModernMessage(
                        "Todos os campos são obrigatórios!",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            if (!NIFUtils.isValidNIF(nif)) {
                showModernMessage(
                        "NIF inválido!",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            String passwordHash = PasswordUtils.hashPassword(password);
            Eleitor eleitor = new Eleitor(nome, username, passwordHash, nif);
            eleitor.setVotou(false);
            if (eleitorDAO.inserir(eleitor)) {
                showModernMessage(
                        "Eleitor adicionado com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE
                );
                atualizarTabelaEleitores();
            } else {
                showModernMessage(
                        "Erro ao adicionar eleitor!",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void editarEleitor() {
        int row = eleitoresTable.getSelectedRow();
        if (row == -1) {
            showModernMessage(
                    "Selecione um eleitor para editar!",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        int id = (int) eleitoresTable.getValueAt(row, 0);
        Eleitor eleitor = eleitorDAO.buscarPorId(id);
        if (eleitor == null) {
            showModernMessage(
                    "Eleitor não encontrado!",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        JPanel panel = createModernFormPanel();
        JTextField nomeField = createModernTextField();
        nomeField.setText(eleitor.getNome());
        JTextField nifField = createModernTextField();
        nifField.setText(eleitor.getNumeroEleitor());
        panel.add(createFormRow("Nome:", nomeField));
        panel.add(createFormRow("NIF:", nifField));
        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Editar Eleitor",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (result == JOptionPane.OK_OPTION) {
            String nome = nomeField.getText().trim();
            String nif = nifField.getText().trim();
            if (nome.isEmpty() || nif.isEmpty()) {
                showModernMessage(
                        "Todos os campos são obrigatórios!",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            if (!NIFUtils.isValidNIF(nif)) {
                showModernMessage(
                        "NIF inválido!",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            eleitor.setNome(nome);
            eleitor.setNumeroEleitor(nif);
            eleitorDAO.atualizar(eleitor);
            atualizarTabelaEleitores();
            showModernMessage(
                    "Eleitor atualizado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void removerEleitor() {
        int row = eleitoresTable.getSelectedRow();
        if (row == -1) {
            showModernMessage(
                    "Selecione um eleitor para remover!",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        String nome = (String) eleitoresTable.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja remover o eleitor:\n" + nome + "?",
                "Confirmar Remoção",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) eleitoresTable.getValueAt(row, 0);
            eleitorDAO.remover(id);
            atualizarTabelaEleitores();
            showModernMessage(
                    "Eleitor removido com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void adicionarCandidato() {
        JPanel panel = createModernFormPanel();
        JTextField nomeField = createModernTextField();
        JTextField partidoField = createModernTextField();
        panel.add(createFormRow("Nome:", nomeField));
        panel.add(createFormRow("Partido:", partidoField));
        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Adicionar Candidato",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (result == JOptionPane.OK_OPTION) {
            String nome = nomeField.getText().trim();
            String partido = partidoField.getText().trim();
            if (nome.isEmpty() || partido.isEmpty()) {
                showModernMessage(
                        "Todos os campos são obrigatórios!",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            Candidato candidato = new Candidato();
            candidato.setNome(nome);
            candidato.setPartido(partido);
            if (candidatoDAO.inserir(candidato)) {
                showModernMessage(
                        "Candidato adicionado com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE
                );
                atualizarTabelaCandidatos();
                atualizarDisplayResultados();
            } else {
                showModernMessage(
                        "Erro ao adicionar candidato!",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void editarCandidato() {
        int row = candidatosTable.getSelectedRow();
        if (row == -1) {
            showModernMessage(
                    "Selecione um candidato para editar!",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        int id = (int) candidatosTable.getValueAt(row, 0);
        Candidato candidato = candidatoDAO.buscarPorId(id);
        if (candidato == null) {
            showModernMessage(
                    "Candidato não encontrado!",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        JPanel panel = createModernFormPanel();
        JTextField nomeField = createModernTextField();
        nomeField.setText(candidato.getNome());
        JTextField partidoField = createModernTextField();
        partidoField.setText(candidato.getPartido());
        panel.add(createFormRow("Nome:", nomeField));
        panel.add(createFormRow("Partido:", partidoField));
        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Editar Candidato",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (result == JOptionPane.OK_OPTION) {
            String nome = nomeField.getText().trim();
            String partido = partidoField.getText().trim();
            if (nome.isEmpty() || partido.isEmpty()) {
                showModernMessage(
                        "Todos os campos são obrigatórios!",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            candidato.setNome(nome);
            candidato.setPartido(partido);
            candidatoDAO.atualizar(candidato);
            atualizarTabelaCandidatos();
            atualizarDisplayResultados();
            showModernMessage(
                    "Candidato atualizado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void removerCandidato() {
        int row = candidatosTable.getSelectedRow();
        if (row == -1) {
            showModernMessage(
                    "Selecione um candidato para remover!",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        String nome = (String) candidatosTable.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja remover o candidato:\n" + nome + "?",
                "Confirmar Remoção",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) candidatosTable.getValueAt(row, 0);
            candidatoDAO.remover(id);
            atualizarTabelaCandidatos();
            atualizarDisplayResultados();
            showModernMessage(
                    "Candidato removido com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private JPanel createModernFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        return panel;
    }

    private JPanel createFormRow(String labelText, JComponent component) {
        JPanel row = new JPanel(new BorderLayout(15, 10));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
        label.setPreferredSize(new Dimension(100, 0));
        row.add(label, BorderLayout.WEST);
        row.add(component, BorderLayout.CENTER);
        row.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);
        return row;
    }

    private JTextField createModernTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(280, 42));
        field.setBackground(CARD_COLOR);
        field.setForeground(TEXT_COLOR);
        field.setBorder(
                BorderFactory.createCompoundBorder(
                        new RoundedBorder(
                                BORDER_COLOR,
                                INPUT_BORDER_THICKNESS,
                                INPUT_CORNER_RADIUS
                        ),
                        new EmptyBorder(10, 15, 10, 15)
                )
        );
        field.addFocusListener(
                new FocusAdapter() {
                    public void focusGained(FocusEvent evt) {
                        field.setBorder(
                                BorderFactory.createCompoundBorder(
                                        new RoundedBorder(
                                                PRIMARY_COLOR,
                                                INPUT_BORDER_THICKNESS + 1,
                                                INPUT_CORNER_RADIUS
                                        ),
                                        new EmptyBorder(10, 15, 10, 15)
                                )
                        );
                    }
                    public void focusLost(FocusEvent evt) {
                        field.setBorder(
                                BorderFactory.createCompoundBorder(
                                        new RoundedBorder(
                                                BORDER_COLOR,
                                                INPUT_BORDER_THICKNESS,
                                                INPUT_CORNER_RADIUS
                                        ),
                                        new EmptyBorder(10, 15, 10, 15)
                                )
                        );
                    }
                }
        );
        return field;
    }

    private JPasswordField createModernPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(280, 42));
        field.setBackground(CARD_COLOR);
        field.setForeground(TEXT_COLOR);
        field.setBorder(
                BorderFactory.createCompoundBorder(
                        new RoundedBorder(
                                BORDER_COLOR,
                                INPUT_BORDER_THICKNESS,
                                INPUT_CORNER_RADIUS
                        ),
                        new EmptyBorder(10, 15, 10, 15)
                )
        );
        field.addFocusListener(
                new FocusAdapter() {
                    public void focusGained(FocusEvent evt) {
                        field.setBorder(
                                BorderFactory.createCompoundBorder(
                                        new RoundedBorder(
                                                PRIMARY_COLOR,
                                                INPUT_BORDER_THICKNESS + 1,
                                                INPUT_CORNER_RADIUS
                                        ),
                                        new EmptyBorder(10, 15, 10, 15)
                                )
                        );
                    }
                    public void focusLost(FocusEvent evt) {
                        field.setBorder(
                                BorderFactory.createCompoundBorder(
                                        new RoundedBorder(
                                                BORDER_COLOR,
                                                INPUT_BORDER_THICKNESS,
                                                INPUT_CORNER_RADIUS
                                        ),
                                        new EmptyBorder(10, 15, 10, 15)
                                )
                        );
                    }
                }
        );
        return field;
    }

    private void showModernMessage(String message, String title, int type) {
        JLabel label = new JLabel(message);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JOptionPane.showMessageDialog(this, label, title, type);
    }

    private void exportarResultados() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exportar Resultados");
        chooser.setFileFilter(
                new FileNameExtensionFilter("Arquivo de Texto (*.txt)", "txt")
        );
        UIManager.put("FileChooser.cancelButtonText", "Cancelar");
        UIManager.put("FileChooser.saveButtonText", "Salvar");
        SwingUtilities.updateComponentTreeUI(chooser);

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getPath() + ".txt");
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                List<Candidato> candidatos = votacaoService.listarCandidatos();
                int totalVotos = votoDAO.contarTotalVotos();
                List<ResultadoData> exportDataList = new ArrayList<>();
                for (Candidato candidato : candidatos) {
                    int votos = votoDAO.contarVotosPorCandidato(candidato.getId());
                    double percentagem = totalVotos > 0
                            ? (votos * 100.0) / totalVotos
                            : 0;
                    exportDataList.add(
                            new ResultadoData(
                                    candidato.getNome(),
                                    candidato.getPartido(),
                                    votos,
                                    percentagem
                            )
                    );
                }
                exportDataList.sort(
                        Comparator.comparingInt(ResultadoData::getVotos).reversed()
                );
                writer.write("Candidato;Partido;Votos;Percentagem");
                writer.newLine();
                for (ResultadoData resData : exportDataList) {
                    writer.write(
                            String.format(
                                    "%s;%s;%d;%.1f%%",
                                    resData.nomeCandidato,
                                    resData.partido,
                                    resData.votos,
                                    resData.percentagem
                            )
                    );
                    writer.newLine();
                }
                showModernMessage(
                        "Resultados exportados com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (IOException e) {
                showModernMessage(
                        "Erro ao exportar: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void iniciarVotacao() {
        if (SistemaVotacao.isVotacaoAberta()) {
            showModernMessage(
                    "A votação já está em andamento!",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja iniciar a votação?",
                "Confirmar Ação",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            SistemaVotacao.iniciarVotacao();
            showModernMessage(
                    "Votação iniciada com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void encerrarVotacao() {
        if (!SistemaVotacao.isVotacaoAberta()) {
            showModernMessage(
                    "A votação já está encerrada!",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja encerrar a votação?",
                "Confirmar Ação",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            SistemaVotacao.encerrarVotacao();
            showModernMessage(
                    "Votação encerrada com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );
            atualizarDisplayResultados();
        }
    }

    private void fazerLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja sair do sistema?",
                "Confirmar Saída",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginGUI().setVisible(true);
        }
    }

    static class SistemaVotacao {
        private static boolean votacaoAberta = false;
        public static boolean isVotacaoAberta() { return votacaoAberta; }
        public static void iniciarVotacao() { votacaoAberta = true; }
        public static void encerrarVotacao() { votacaoAberta = false; }
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        UIManager.put("OptionPane.yesButtonText", "Sim");
        UIManager.put("OptionPane.noButtonText", "Não");
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        UIManager.put("OptionPane.okButtonText", "OK");
        SwingUtilities.invokeLater(() -> new AdminGUI().setVisible(true));
    }
}