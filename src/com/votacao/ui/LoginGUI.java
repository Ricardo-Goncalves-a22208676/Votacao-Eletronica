package com.votacao.ui;

import com.votacao.model.Eleitor;
import com.votacao.model.Usuario;
import com.votacao.service.AutenticacaoService;
import com.votacao.utils.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public class LoginGUI extends JFrame {
    // Modern baby blue color palette (matching AdminGUI)
    private static final Color PRIMARY_COLOR = new Color(122, 184, 253); // #7AB8FD - Baby Blue
    private static final Color PRIMARY_DARK = new Color(96, 165, 250); // Darker baby blue
    private static final Color PRIMARY_LIGHT = new Color(147, 197, 253); // Lighter baby blue
    private static final Color ACCENT_COLOR = new Color(59, 130, 246); // Blue accent
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129); // Emerald
    private static final Color WARNING_COLOR = new Color(245, 158, 11); // Amber
    private static final Color ERROR_COLOR = new Color(239, 68, 68); // Red
    private static final Color SECONDARY_COLOR = new Color(148, 163, 184); // Slate gray
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252); // Very light blue-gray
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(30, 41, 59); // Slate
    private static final Color TEXT_LIGHT = new Color(71, 85, 105); // Light slate
    private static final Color BORDER_COLOR = new Color(226, 232, 240); // Light border
    private static final Color HOVER_COLOR = new Color(241, 245, 249); // Very light blue

    private AutenticacaoService autenticacaoService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    public LoginGUI() {
        this.autenticacaoService = new AutenticacaoService();
        initializeUI();
    }

    private void initializeUI() {
        // Modern window setup
        setTitle("Sistema de Votação Eletrónica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create gradient background
        setContentPane(new GradientPanel());
        setLayout(new BorderLayout());

        // Create main card panel
        JPanel cardPanel = createCardPanel();

        // Center the card
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(cardPanel);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 30));
        card.setBackground(CARD_COLOR);
        card.setBorder(
                new RoundedShadowBorder(20, new Color(122, 184, 253, 30))
        );
        card.setPreferredSize(new Dimension(400, 500));

        // Header with logo and title
        JPanel headerPanel = createHeaderPanel();
        card.add(headerPanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = createFormPanel();
        card.add(formPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(new EmptyBorder(40, 40, 20, 40));

        // Logo (using Unicode symbol)
        JLabel logoLabel = new JLabel("🗳️", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel titleLabel =
                new JLabel("Sistema de Votação", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel =
                new JLabel("Acesso seguro ao seu voto", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(TEXT_LIGHT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(logoLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(new EmptyBorder(20, 40, 40, 40));

        // Username field
        JPanel usernamePanel = createInputPanel(
                "👤 Username",
                usernameField = createModernTextField("Digite seu username")
        );
        usernamePanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the panel

        // Password field
        JPanel passwordPanel = createInputPanel(
                "🔒 Password",
                passwordField = createModernPasswordField("Digite sua password")
        );
        passwordPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the panel

        // Login button
        loginButton = createModernButton("Entrar", PRIMARY_COLOR, PRIMARY_DARK);
        loginButton.addActionListener(e -> handleLogin());
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Ensure button is also centered

        // Status label
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(ERROR_COLOR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components with spacing
        formPanel.add(usernamePanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(passwordPanel);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(loginButton);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(statusLabel);

        // Add enter key listener
        passwordField.addActionListener(e -> handleLogin());

        return formPanel;
    }

    private JPanel createInputPanel(String labelText, JComponent inputField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        // Make the panel transparent so formPanel's background shows if needed,
        // or ensure its background matches formPanel.
        // panel.setOpaque(false); // Option 1: transparent
        panel.setBackground(CARD_COLOR); // Option 2: match (already doing this)


        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        inputField.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(8));
        panel.add(inputField);

        return panel;
    }

    private JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                // Fill background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                super.paintComponent(g2);
                g2.dispose();
            }
        };

        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setForeground(TEXT_COLOR);
        field.setBackground(HOVER_COLOR);
        field.setBorder(
                new RoundedBorder(12, BORDER_COLOR, new Color(0, 0, 0, 0))
        );
        field.setPreferredSize(new Dimension(320, 50));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Placeholder effect
        if (!placeholder.isEmpty()) {
            field.setText(placeholder);
            field.setForeground(SECONDARY_COLOR);

            field.addFocusListener(
                    new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            if (field.getText().equals(placeholder)) {
                                field.setText("");
                                field.setForeground(TEXT_COLOR);
                            }
                            field.setBorder(
                                    new RoundedBorder(
                                            12,
                                            PRIMARY_COLOR,
                                            new Color(122, 184, 253, 30)
                                    )
                            );
                            field.setBackground(CARD_COLOR);
                        }

                        @Override
                        public void focusLost(FocusEvent e) {
                            if (field.getText().isEmpty()) {
                                field.setText(placeholder);
                                field.setForeground(SECONDARY_COLOR);
                            }
                            field.setBorder(
                                    new RoundedBorder(
                                            12,
                                            BORDER_COLOR,
                                            new Color(0, 0, 0, 0)
                                    )
                            );
                            field.setBackground(HOVER_COLOR);
                        }
                    }
            );
        }

        return field;
    }

    private JPasswordField createModernPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                // Fill background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                super.paintComponent(g2);
                g2.dispose();
            }
        };

        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setForeground(TEXT_COLOR);
        field.setBackground(HOVER_COLOR);
        field.setBorder(
                new RoundedBorder(12, BORDER_COLOR, new Color(0, 0, 0, 0))
        );
        field.setPreferredSize(new Dimension(320, 50));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        field.setEchoChar((char) 0);

        // Placeholder for password field
        field.setText(placeholder);
        field.setForeground(SECONDARY_COLOR);
        field.setEchoChar((char) 0);

        field.addFocusListener(
                new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        if (
                                String.valueOf(field.getPassword()).equals(placeholder)
                        ) {
                            field.setText("");
                            field.setEchoChar('•');
                            field.setForeground(TEXT_COLOR);
                        }
                        field.setBorder(
                                new RoundedBorder(
                                        12,
                                        PRIMARY_COLOR,
                                        new Color(122, 184, 253, 30)
                                )
                        );
                        field.setBackground(CARD_COLOR);
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        if (field.getPassword().length == 0) {
                            field.setEchoChar((char) 0);
                            field.setText(placeholder);
                            field.setForeground(SECONDARY_COLOR);
                        }
                        field.setBorder(
                                new RoundedBorder(
                                        12,
                                        BORDER_COLOR,
                                        new Color(0, 0, 0, 0)
                                )
                        );
                        field.setBackground(HOVER_COLOR);
                    }
                }
        );

        return field;
    }

    private JButton createModernButton(
            String text,
            Color bgColor,
            Color hoverColor
    ) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                // Create gradient with baby blue
                GradientPaint gradient = new GradientPaint(
                        0,
                        0,
                        getBackground(),
                        0,
                        getHeight(),
                        getBackground().darker()
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Paint text
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

        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(320, 50));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // button.setAlignmentX(Component.CENTER_ALIGNMENT); // Already set in createFormPanel

        // Hover effect
        button.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        button.setBackground(hoverColor);
                    }

                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        button.setBackground(bgColor);
                    }
                }
        );

        return button;
    }

    // Gradient background panel with baby blue theme
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY
            );

            // Create diagonal gradient with baby blue colors
            GradientPaint gp = new GradientPaint(
                    0,
                    0,
                    new Color(122, 184, 253, 40), // Baby blue
                    getWidth(),
                    getHeight(),
                    new Color(147, 197, 253, 60) // Lighter baby blue
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Add subtle pattern
            g2d.setColor(new Color(255, 255, 255, 10));
            for (int i = 0; i < getWidth(); i += 50) {
                for (int j = 0; j < getHeight(); j += 50) {
                    g2d.fillOval(i, j, 2, 2);
                }
            }
        }
    }

    // Modern rounded border with baby blue shadow
    static class RoundedShadowBorder extends javax.swing.border.AbstractBorder {
        private final int radius;
        private final Color shadowColor;

        public RoundedShadowBorder(int radius, Color shadowColor) {
            this.radius = radius;
            this.shadowColor = shadowColor;
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

            // Draw baby blue shadow
            for (int i = 0; i < 15; i++) {
                Color color = new Color(
                        shadowColor.getRed(),
                        shadowColor.getGreen(),
                        shadowColor.getBlue(),
                        Math.max(0, shadowColor.getAlpha() - i * 2)
                );
                g2.setColor(color);
                g2.drawRoundRect(
                        x + i,
                        y + i,
                        width - 2 * i - 1,
                        height - 2 * i - 1,
                        radius,
                        radius
                );
            }

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(15, 15, 20, 20);
        }
    }

    // Modern input border with baby blue focus
    static class RoundedBorder extends javax.swing.border.AbstractBorder {
        private final int radius;
        private final Color borderColor;
        private final Color backgroundColor;

        public RoundedBorder(
                int radius,
                Color borderColor,
                Color backgroundColor
        ) {
            this.radius = radius;
            this.borderColor = borderColor;
            this.backgroundColor = backgroundColor;
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

            // Fill background
            if (backgroundColor.getAlpha() > 0) {
                g2.setColor(backgroundColor);
                g2.fillRoundRect(x, y, width - 1, height - 1, radius, radius);
            }

            // Draw border
            g2.setColor(borderColor);
            g2.setStroke(
                    new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
            );
            g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, radius, radius);

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(12, 16, 12, 16);
        }
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Clear placeholder text
        if (username.equals("Digite seu username")) username = "";
        if (password.equals("Digite sua password")) password = "";

        if (!ValidationUtils.isNotEmpty(username)) {
            showMessage("O username não pode estar vazio!", ERROR_COLOR);
            return;
        }

        if (!ValidationUtils.isNotEmpty(password)) {
            showMessage("A password não pode estar vazia!", ERROR_COLOR);
            return;
        }

        Usuario usuario = autenticacaoService.autenticar(username, password);

        if (usuario != null) {
            showMessage("Login realizado com sucesso!", SUCCESS_COLOR);

            // Small delay for user feedback
            Timer timer = new Timer(
                    800,
                    e -> {
                        dispose();
                        if (autenticacaoService.isAdministrador(usuario)) {
                            SwingUtilities.invokeLater(
                                    () -> {
                                        AdminGUI adminGUI = new AdminGUI();
                                        adminGUI.setVisible(true);
                                    }
                            );
                        } else if (autenticacaoService.isEleitor(usuario)) {
                            SwingUtilities.invokeLater(
                                    () -> {
                                        EleitorGUI eleitorGUI = new EleitorGUI(
                                                (Eleitor) usuario
                                        );
                                        eleitorGUI.setVisible(true);
                                    }
                            );
                        }
                    }
            );
            timer.setRepeats(false);
            timer.start();
        } else {
            showMessage("Credenciais inválidas!", ERROR_COLOR);
        }
    }

    private void showMessage(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);

        // Fade out effect after 3 seconds (unless it's success message)
        if (color == ERROR_COLOR) {
            Timer timer = new Timer(
                    3000,
                    e -> {
                        statusLabel.setText("");
                    }
            );
            timer.setRepeats(false);
            timer.start();
        }
    }

    public void mostrarLogin() {
        SwingUtilities.invokeLater(
                () -> {
                    setVisible(true);
                }
        );
    }
}