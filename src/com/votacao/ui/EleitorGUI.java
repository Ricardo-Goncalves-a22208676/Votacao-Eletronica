package com.votacao.ui;

import com.votacao.model.*;
import com.votacao.service.VotacaoService;
import com.votacao.dao.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class EleitorGUI extends JFrame {
    // Modern baby blue color palette (matching LoginGUI & AdminGUI)
    private static final Color PRIMARY_COLOR = new Color(122, 184, 253); // #7AB8FD - Baby Blue
    private static final Color PRIMARY_DARK = new Color(96, 165, 250); // Darker baby blue
    private static final Color PRIMARY_LIGHT = new Color(147, 197, 253); // Lighter baby blue
    private static final Color ACCENT_COLOR = new Color(59, 130, 246); // Blue accent
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129); // Emerald
    private static final Color WARNING_COLOR = new Color(245, 158, 11); // Amber
    private static final Color ERROR_COLOR = new Color(239, 68, 68); // Red
    private static final Color SECONDARY_COLOR = new Color(148, 163, 184); // Slate gray (used for hover on non-primary buttons if needed)
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252); // Very light blue-gray
    private static final Color CARD_BACKGROUND = Color.WHITE; // Keep as CARD_COLOR from other GUIs
    private static final Color TEXT_COLOR = new Color(30, 41, 59); // Slate
    private static final Color TEXT_LIGHT = new Color(71, 85, 105); // Light slate (formerly TEXT_MUTED)
    private static final Color BORDER_COLOR = new Color(226, 232, 240); // Light border
    private static final Color HOVER_COLOR = new Color(241, 245, 249); // Very light blue for general hover (e.g., table rows)


    private JTabbedPane tabbedPane;
    private JTable candidatosTable;
    private Eleitor eleitor;
    private VotacaoService votacaoService;
    private CandidatoDAO candidatoDAO;
    private VotoDAO votoDAO;

    public EleitorGUI(Eleitor eleitor) {
        this.eleitor = eleitor;
        this.votacaoService = new VotacaoService();
        this.candidatoDAO = new CandidatoDAO();
        this.votoDAO = new VotoDAO();
        initializeUI();
        carregarDados();
    }

    private void initializeUI() {
        setTitle("Painel do Eleitor - Sistema de Votação");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = createModernHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createModernHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                GradientPaint gp = new GradientPaint(
                        0,
                        0,
                        PRIMARY_COLOR,
                        getWidth(),
                        0,
                        PRIMARY_DARK
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        headerPanel.setPreferredSize(new Dimension(0, 80));
        // Removed ModernBorder as gradient fills the space

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20)); // Adjusted padding
        leftPanel.setOpaque(false); // Make transparent for gradient

        JLabel iconLabel = new JLabel("👋");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false); // Make transparent

        JLabel welcomeLabel = new JLabel("Bem-vindo, " + eleitor.getNome());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE); // Text on gradient

        JLabel roleLabel = new JLabel("Eleitor registrado no sistema");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleLabel.setForeground(new Color(255, 255, 255, 200)); // Lighter text on gradient

        textPanel.add(welcomeLabel);
        textPanel.add(roleLabel);

        leftPanel.add(iconLabel);
        leftPanel.add(Box.createHorizontalStrut(10));
        leftPanel.add(textPanel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        rightPanel.setOpaque(false); // Make transparent

        JButton logoutButton = createModernButton(
                "Sair",
                ERROR_COLOR,
                ERROR_COLOR.darker()
        );
        logoutButton.addActionListener(e -> fazerLogout());
        rightPanel.add(logoutButton);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        // This panel will be the direct child of mainPanel's CENTER.
        // It should have BACKGROUND_COLOR.
        // The actual content (voted or voting) will be placed inside this.
        JPanel contentWrapper = new JPanel(new GridBagLayout()); // Use GridBagLayout to center content
        contentWrapper.setBackground(BACKGROUND_COLOR);
        contentWrapper.setOpaque(true);


        if (eleitor.isVotou()) {
            contentWrapper.add(createVotedPanel());
        } else {
            // For voting panel, we want it to take more space if possible
            JPanel votingPanelContainer = new JPanel(new BorderLayout());
            votingPanelContainer.setBackground(BACKGROUND_COLOR);
            votingPanelContainer.add(createVotingPanel(), BorderLayout.CENTER);
            contentWrapper.add(votingPanelContainer); // This might need adjustment if it doesn't fill
            // If createVotingPanel should fill, contentWrapper might need BorderLayout too.
            // Let's try with GridBagLayout first for centering the voted panel.
            // For voting panel, it might be better to return it directly or wrap differently.

            // Simpler:
            if (eleitor.isVotou()) {
                return createVotedPanel(); // This panel already uses GridBagLayout to center its card
            } else {
                return createVotingPanel(); // This panel uses BorderLayout
            }
        }
        return contentWrapper; // Fallback, should be covered by if/else
    }


    private JPanel createVotedPanel() {
        JPanel panel = new JPanel(new GridBagLayout()); // Centers the cardPanel
        panel.setBackground(BACKGROUND_COLOR);
        panel.setOpaque(true);

        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(CARD_BACKGROUND);
        cardPanel.setBorder(new ModernBorder(16, BORDER_COLOR)); // Standard border
        cardPanel.setPreferredSize(new Dimension(450, 350)); // Slightly larger

        JLabel iconLabel = new JLabel("✅", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel messageLabel =
                new JLabel("Voto Registrado com Sucesso!", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        messageLabel.setForeground(SUCCESS_COLOR); // Use new SUCCESS_COLOR
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(
                "Obrigado por exercer o seu direito de voto",
                SwingConstants.CENTER
        );
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(TEXT_LIGHT); // Use new TEXT_LIGHT
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardPanel.add(Box.createVerticalGlue());
        cardPanel.add(iconLabel);
        cardPanel.add(Box.createVerticalStrut(20));
        cardPanel.add(messageLabel);
        cardPanel.add(Box.createVerticalStrut(10));
        cardPanel.add(subtitleLabel);
        cardPanel.add(Box.createVerticalGlue());


        panel.add(cardPanel);
        return panel;
    }

    private JPanel createVotingPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20)); // Add gap between instructions and table
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(0,0,0,0)); // No extra border for the container itself

        JPanel tablePanel = createCandidatesTablePanel();
        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCandidatesTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(new ModernBorder(16, BORDER_COLOR)); // Standard border

        JLabel tableTitle = new JLabel("Candidatos Disponíveis");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(TEXT_COLOR);
        tableTitle.setBorder(new EmptyBorder(20, 20, 15, 20)); // Adjusted padding
        panel.add(tableTitle, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Nome", "Partido", "Ação"};
        candidatosTable = createModernTable(columnNames);

        candidatosTable
                .getColumnModel()
                .getColumn(3)
                .setCellRenderer(new ModernButtonRenderer());
        candidatosTable
                .getColumnModel()
                .getColumn(3)
                .setCellEditor(new ModernButtonEditor(new JCheckBox()));
        // Set preferred width for action column
        candidatosTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        candidatosTable.getColumnModel().getColumn(3).setMaxWidth(150);


        JScrollPane scrollPane = new JScrollPane(candidatosTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0,1,1,1)); // Border for scrollpane itself
        scrollPane.getViewport().setBackground(CARD_BACKGROUND);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JTable createModernTable(String[] columnNames) {
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(55); // Adjusted height
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0)); // No spacing, handled by renderer border
        table.setBackground(CARD_BACKGROUND);
        table.setSelectionBackground(new Color(122, 184, 253, 70)); // PRIMARY_COLOR with alpha
        table.setSelectionForeground(TEXT_COLOR);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(HOVER_COLOR); // Lighter background for header
        header.setForeground(TEXT_COLOR);
        header.setPreferredSize(new Dimension(header.getWidth(), 50));
        header.setBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR)
        ); // Bottom border for header

        table.setDefaultRenderer(
                Object.class,
                new DefaultTableCellRenderer() {
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

                        if (!isSelected) {
                            if (row % 2 == 0) {
                                c.setBackground(CARD_BACKGROUND);
                            } else {
                                c.setBackground(HOVER_COLOR); // Use HOVER_COLOR for alternating rows
                            }
                        }
                        ((JComponent) c).setBorder(new EmptyBorder(10, 15, 10, 15)); // Cell padding
                        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                        setForeground(TEXT_COLOR); // Default text color for cells

                        return c;
                    }
                }
        );

        return table;
    }

    class ModernButtonRenderer extends JButton
            implements javax.swing.table.TableCellRenderer {
        public ModernButtonRenderer() {
            super("🗳 Votar"); // Set text here
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 13)); // Slightly smaller for table
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            // setPreferredSize(new Dimension(100, 35)); // Preferred size for the button
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
            // setText is already set in constructor, or can be dynamic if needed
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            GradientPaint gradient = new GradientPaint(
                    0,
                    0,
                    SUCCESS_COLOR,
                    0,
                    getHeight(),
                    SUCCESS_COLOR.darker()
            );
            g2.setPaint(gradient);
            // Adjust bounds for padding within the cell
            g2.fillRoundRect(6, (getHeight() - 36) / 2 , getWidth() - 12, 36, 10, 10);


            FontMetrics fm = g2.getFontMetrics();
            Rectangle stringBounds = fm
                    .getStringBounds(getText(), g2)
                    .getBounds();
            int textX = (getWidth() - stringBounds.width) / 2;
            int textY = (getHeight() - stringBounds.height) / 2 + fm.getAscent();

            g2.setColor(getForeground());
            g2.drawString(getText(), textX, textY);

            g2.dispose();
        }
    }

    class ModernButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int row;

        public ModernButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new ModernButtonRenderer(); // Uses the styled renderer
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column
        ) {
            this.row = row;
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int candidatoId = (int) candidatosTable.getValueAt(row, 0);
                // Check if voting is allowed before proceeding
                if (SistemaVotacao.isVotacaoAberta() && votacaoService.podeVotar(eleitor.getId())) {
                    votar(candidatoId); // votar method includes confirmation
                } else if (!SistemaVotacao.isVotacaoAberta()) {
                    JOptionPane.showMessageDialog(EleitorGUI.this, "A votação está encerrada no momento.", "Votação Encerrada", JOptionPane.WARNING_MESSAGE);
                } else { // Already voted
                    JOptionPane.showMessageDialog(EleitorGUI.this, "Você já votou nesta eleição!", "Voto Duplicado", JOptionPane.WARNING_MESSAGE);
                }
            }
            isPushed = false;
            return "Votar"; // Value of the cell after editing
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private JButton createModernButton(
            String text,
            Color bgColor,
            Color hoverColor
    ) {
        JButton button = new JButton(text) {
            private Color currentBgColor = bgColor;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                GradientPaint gradient = new GradientPaint(
                        0,
                        0,
                        currentBgColor, // Use currentBgColor for dynamic hover
                        0,
                        getHeight(),
                        currentBgColor.darker()
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12); // Rounded corners

                FontMetrics fm = g2.getFontMetrics();
                Rectangle stringBounds = fm
                        .getStringBounds(getText(), g2)
                        .getBounds();
                int textX = (getWidth() - stringBounds.width) / 2;
                int textY =
                        (getHeight() - stringBounds.height) / 2 + fm.getAscent();

                g2.setColor(getForeground());
                g2.drawString(getText(), textX, textY);

                g2.dispose();
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        // button.setBackground(bgColor); // Background is handled by paintComponent
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false); // Important for custom painting
        button.setPreferredSize(new Dimension(130, 40)); // Adjusted size
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        button.putClientProperty("currentBgColor", hoverColor);
                        button.repaint();
                    }

                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        button.putClientProperty("currentBgColor", bgColor);
                        button.repaint();
                    }

                    @Override
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        button.putClientProperty("currentBgColor", hoverColor.darker());
                        button.repaint();
                    }

                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent evt) {
                        if (button.contains(evt.getPoint())) {
                            button.putClientProperty("currentBgColor", hoverColor);
                        } else {
                            button.putClientProperty("currentBgColor", bgColor);
                        }
                        button.repaint();
                    }
                }
        );
        // Initialize currentBgColor for the button's paintComponent
        button.putClientProperty("currentBgColor", bgColor);


        return button;
    }

    static class ModernBorder extends javax.swing.border.AbstractBorder {
        private final int radius;
        private final Color borderColor;

        public ModernBorder(int radius, Color borderColor) {
            this.radius = radius;
            this.borderColor = borderColor;
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
            g2.setColor(this.borderColor);
            g2.setStroke(new BasicStroke(1f)); // 1px border
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1); // Minimal insets
        }
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = 1;
            return insets;
        }
    }

    private void carregarDados() {
        if (!eleitor.isVotou()) {
            atualizarTabelaCandidatos();
        }
    }

    private void atualizarTabelaCandidatos() {
        if (eleitor.isVotou() || candidatosTable == null) return; // Check if table is initialized

        DefaultTableModel model = (DefaultTableModel) candidatosTable.getModel();
        model.setRowCount(0);

        if (!SistemaVotacao.isVotacaoAberta()) {
            // Optionally, show a message directly on the panel instead of a dialog
            // For now, dialog is fine as it's an important state.
            // Consider disabling voting UI elements if voting is closed.
            // JOptionPane.showMessageDialog(this, "A votação está encerrada no momento.", "Votação Encerrada", JOptionPane.WARNING_MESSAGE);
            // For a better UX, you might want to display this message within the UI itself.
            // For example, disable the table and show a label.
            // For now, the button click will handle this.
        }

        List<Candidato> candidatos = candidatoDAO.listarTodos();
        for (Candidato candidato : candidatos) {
            model.addRow(
                    new Object[]{
                            candidato.getId(),
                            candidato.getNome(),
                            candidato.getPartido(),
                            "Votar", // Placeholder text for button column
                    }
            );
        }
    }

    private void votar(int candidatoId) {
        // Confirmation dialog is part of this method
        String candidatoNome = "";
        List<Candidato> candidatos = candidatoDAO.listarTodos(); // Fetch fresh list
        for (Candidato c : candidatos) {
            if (c.getId() == candidatoId) {
                candidatoNome = c.getNome();
                break;
            }
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Confirma o seu voto para:\n" + candidatoNome + "?",
                "Confirmar Voto",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (votacaoService.votar(eleitor.getId(), candidatoId)) {
                eleitor.setVotou(true); // Update local eleitor state

                // No need for JOptionPane here if UI refreshes to "Voted Panel"
                // JOptionPane.showMessageDialog(this,
                // "Voto registrado com sucesso!\nObrigado por participar.",
                // "Sucesso",
                // JOptionPane.INFORMATION_MESSAGE);

                // Refresh the entire UI to show the "Voted" panel
                getContentPane().removeAll();
                // Re-initialize UI components that depend on eleitor.isVotou()
                // This means createModernHeader and createContentPanel need to be called again
                // as part of a larger refresh.
                // The simplest way is to re-run parts of initializeUI or a dedicated refresh method.
                // For now, re-adding mainPanel after recreating its content.

                JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
                mainPanel.setBackground(BACKGROUND_COLOR);
                mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
                mainPanel.add(createModernHeader(), BorderLayout.NORTH); // Recreate header
                mainPanel.add(createContentPanel(), BorderLayout.CENTER); // Recreate content

                setContentPane(mainPanel); // Set the new main panel
                revalidate();
                repaint();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Erro ao registrar voto!",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void fazerLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja sair?",
                "Confirmar Saída",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginGUI().setVisible(true); // Assuming LoginGUI is in the same package or imported
        }
    }
}