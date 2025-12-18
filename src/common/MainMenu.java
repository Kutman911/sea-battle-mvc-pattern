package common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainMenu extends JDialog {
    private static final Color BORDER_YELLOW = new Color(237, 176, 36);
    private static final Color TEXT_WHITE = Color.WHITE;
    private static final int WIDTH = 1500;
    private static final int HEIGHT = 900;

    private List<Snowflake> snowflakes = new ArrayList<>();
    private Timer animationTimer;
    private Image backgroundImage;

    public MainMenu(Window parent, Runnable onStart, Runnable onSettings, Runnable onRules, Runnable onExit) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Try to load background image from classpath resources first
        try {
            java.net.URL imgUrl = getClass().getResource("/images/bg_main_menu.jpg");
            if (imgUrl != null) {
                backgroundImage = new ImageIcon(imgUrl).getImage();
            }
        } catch (Exception ignored) {}

        // Fallbacks in case classpath resource is not found (e.g., running from IDE)
        if (backgroundImage == null) {
            // relative path inside project
            java.io.File rel = new java.io.File("src\\images\\bg_main_menu.jpg");
            if (rel.exists()) {
                backgroundImage = new ImageIcon(rel.getAbsolutePath()).getImage();
            }
        }

        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            snowflakes.add(new Snowflake(
                    rand.nextInt(WIDTH),
                    rand.nextInt(HEIGHT),
                    rand.nextDouble() * 2 + 1,
                    rand.nextInt(5) + 3
            ));
        }

        JPanel content = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw background image scaled to panel size; if not available, fill with fallback color
                if (backgroundImage != null) {
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2d.setColor(new Color(70, 130, 180));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }

                g2d.setColor(Color.WHITE);
                Random rand = new Random(42);
                for (int i = 0; i < 100; i++) {
                    int x = rand.nextInt(getWidth());
                    int y = rand.nextInt(getHeight());
                    int size = rand.nextInt(3) + 1;
                    g2d.fillOval(x, y, size, size);

                    if (size > 1) {
                        g2d.drawLine(x - 2, y + size/2, x + size + 2, y + size/2);
                        g2d.drawLine(x + size/2, y - 2, x + size/2, y + size + 2);
                    }
                }

                for (Snowflake snowflake : snowflakes) {
                    snowflake.draw(g2d);
                }

                g2d.dispose();
            }
        };
        content.setBorder(new LineBorder(BORDER_YELLOW, 8));
        content.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        JLabel title = new JLabel("🎄 SEA BATTLE 🎄") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = fm.getAscent();
                g2d.drawString(getText(), x + 3, y + 3);

                super.paintComponent(g);
            }
        };
        title.setForeground(BORDER_YELLOW);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 72));
        title.setBorder(new EmptyBorder(60, 20, 40, 20));
        title.setOpaque(false);

        JPanel buttons = new JPanel(new GridBagLayout());
        buttons.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 60, 15, 60);

        JButton startBtn = createMenuButton("⚔ START GAME", new Color(220, 53, 69));
        JButton rulesBtn = createMenuButton("📜 RULES", new Color(23, 162, 184));
        JButton exitBtn = createMenuButton("🎁 EXIT", new Color(108, 117, 125));

        gbc.gridy = 0;
        buttons.add(startBtn, gbc);
        gbc.gridy = 1;
        buttons.add(rulesBtn, gbc);
        gbc.gridy = 2;
        buttons.add(exitBtn, gbc);

        content.add(title, BorderLayout.NORTH);
        content.add(buttons, BorderLayout.CENTER);

        setContentPane(content);
        pack();
        setLocationRelativeTo(parent);

        animationTimer = new Timer(50, e -> {
            for (Snowflake snowflake : snowflakes) {
                snowflake.fall(HEIGHT);
            }
            content.repaint();
        });
        animationTimer.start();

        startBtn.addActionListener(e -> {
            animationTimer.stop();
            dispose();
            if (onStart != null) SwingUtilities.invokeLater(onStart);
        });

        // Кнопка Settings удалена — параметр onSettings игнорируется

        // Открываем встроенный RulesDialog, НЕ вызываем внешний onRules
        rulesBtn.addActionListener(e -> {
            animationTimer.stop();
            RulesDialog dlg = new RulesDialog(MainMenu.this, () -> animationTimer.start());
            dlg.setVisible(true);
        });

        exitBtn.addActionListener(e -> {
            animationTimer.stop();
            dispose();
            if (onExit != null) SwingUtilities.invokeLater(onExit);
        });
    }

    private JButton createMenuButton(String text, Color baseColor) {
        class ScalableButton extends JButton {
            private float scale = 1.0f;

            public ScalableButton(String text) {
                super(text);
                setOpaque(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
            }

            public float getScale() {
                return scale;
            }

            public void setScale(float scale) {
                this.scale = scale;
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int newW = (int)(w * scale);
                int newH = (int)(h * scale);
                int x = (w - newW) / 2;
                int y = (h - newH) / 2;

                g2d.translate(x, y);
                g2d.scale(scale, scale);

                GradientPaint gp = new GradientPaint(
                        0, 0, baseColor.brighter(),
                        0, getHeight(), baseColor
                );
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, w, h, 20, 20);

                g2d.setColor(BORDER_YELLOW);
                g2d.setStroke(new BasicStroke(5));
                g2d.drawRoundRect(0, 0, w - 1, h - 1, 20, 20);

                g2d.dispose();
                super.paintComponent(g);
            }
        }

        ScalableButton b = new ScalableButton(text);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 28));
        b.setForeground(TEXT_WHITE);
        b.setPreferredSize(new Dimension(400, 80));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                Timer scaleUp = new Timer(10, evt -> {
                    float currentScale = b.getScale();
                    if (currentScale < 1.05f) {
                        b.setScale(currentScale + 0.01f);
                        b.repaint();
                    } else {
                        ((Timer)evt.getSource()).stop();
                    }
                });
                scaleUp.start();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                Timer scaleDown = new Timer(10, evt -> {
                    float currentScale = b.getScale();
                    if (currentScale > 1.0f) {
                        b.setScale(currentScale - 0.01f);
                        b.repaint();
                    } else {
                        ((Timer)evt.getSource()).stop();
                    }
                });
                scaleDown.start();
            }
        });

        return b;
    }

    private static class Snowflake {
        private double x, y;
        private double speed;
        private int size;
        private double swing;

        public Snowflake(double x, double y, double speed, int size) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.size = size;
            this.swing = Math.random() * 2 * Math.PI;
        }

        public void fall(int height) {
            y += speed;
            x += Math.sin(swing) * 0.5;
            swing += 0.05;

            if (y > height) {
                y = -10;
                x = Math.random() * 1500;
            }
        }

        public void draw(Graphics2D g2d) {
            g2d.setColor(new Color(255, 255, 255, 200));
            g2d.fillOval((int)x, (int)y, size, size);

            g2d.setStroke(new BasicStroke(1));
            int centerX = (int)x + size/2;
            int centerY = (int)y + size/2;
            int radius = size + 2;

            for (int i = 0; i < 6; i++) {
                double angle = Math.PI / 3 * i;
                int x1 = centerX + (int)(Math.cos(angle) * radius);
                int y1 = centerY + (int)(Math.sin(angle) * radius);
                g2d.drawLine(centerX, centerY, x1, y1);
            }
        }
    }

    // Встроенный RulesDialog: прокручиваемое окно с текстом правил
    private class RulesDialog extends JDialog {
        public RulesDialog(Window parent, Runnable onClose) {
            super(parent, ModalityType.APPLICATION_MODAL);
            setUndecorated(true);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            JPanel content = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(0, 0, new Color(25, 50, 80), 0, getHeight(), new Color(10, 25, 50));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.dispose();
                }
            };
            content.setBorder(new LineBorder(BORDER_YELLOW, 6));
            content.setPreferredSize(new Dimension(700, 520));

            JLabel title = new JLabel("📜 RULES");
            title.setForeground(BORDER_YELLOW);
            title.setHorizontalAlignment(SwingConstants.CENTER);
            title.setFont(new Font("SansSerif", Font.BOLD, 36));
            title.setBorder(new EmptyBorder(20, 20, 10, 20));
            title.setOpaque(false);

            String rulesText =
                    "Rules of Sea Battle:\n\n" +
                            "1. Each player places ships on their grid.\n" +
                            "2. Players take turns to shoot at coordinates on opponent's grid.\n" +
                            "3. A hit is marked and you get another turn; a miss passes the turn.\n" +
                            "4. Sink all opponent's ships to win.\n\n" +
                            "Controls:\n" +
                            "- Click on grid cell to fire.\n" +
                            "- Use settings to toggle hints.\n";

            JTextArea text = new JTextArea(rulesText);
            text.setEditable(false);
            text.setLineWrap(true);
            text.setWrapStyleWord(true);
            text.setOpaque(false);
            text.setForeground(TEXT_WHITE);
            text.setFont(new Font("SansSerif", Font.PLAIN, 16));
            text.setBorder(new EmptyBorder(10, 10, 10, 10));

            JScrollPane scroll = new JScrollPane(text);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            scroll.setBorder(null);
            scroll.setPreferredSize(new Dimension(700 - 80, 520 - 160));
            scroll.getVerticalScrollBar().setUnitIncrement(16);

            JPanel center = new JPanel(new BorderLayout());
            center.setOpaque(false);
            center.setBorder(new EmptyBorder(10, 30, 10, 30));
            center.add(scroll, BorderLayout.CENTER);

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttons.setOpaque(false);
            JButton close = new JButton("Close");
            close.setFont(new Font("SansSerif", Font.PLAIN, 16));
            close.addActionListener(e -> {
                dispose();
                if (onClose != null) SwingUtilities.invokeLater(onClose);
            });
            buttons.add(close);

            content.add(title, BorderLayout.NORTH);
            content.add(center, BorderLayout.CENTER);
            content.add(buttons, BorderLayout.SOUTH);

            setContentPane(content);
            pack();
            setLocationRelativeTo(parent);

            getRootPane().registerKeyboardAction(e -> {
                dispose();
                if (onClose != null) SwingUtilities.invokeLater(onClose);
            }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        }
    }
}
