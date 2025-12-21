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
        try {
            setOpacity(0f);
        } catch (Throwable ignored) { }

        try {
            java.net.URL imgUrl = getClass().getResource("/resources/images/bg_main_menu.png");
            if (imgUrl != null) {
                backgroundImage = new ImageIcon(imgUrl).getImage();
            }
        } catch (Exception ignored) {}

        if (backgroundImage == null) {
            java.io.File rel = new java.io.File("src\\images\\bg_main_menu.png");
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
        if (parent != null && parent.isVisible()) {
            setLocationRelativeTo(parent);
        } else {
            setLocationRelativeTo(null);
        }

        animationTimer = new Timer(50, e -> {
            for (Snowflake snowflake : snowflakes) {
                snowflake.fall(HEIGHT);
            }
            content.repaint();
        });
        animationTimer.start();

        startBtn.addActionListener(e -> {
            animationTimer.stop();
            fadeOutAndClose(() -> {
                if (onStart != null) SwingUtilities.invokeLater(onStart);
            });
        });


        rulesBtn.addActionListener(e -> {
            animationTimer.stop();
            RulesDialog dlg = new RulesDialog(MainMenu.this, () -> animationTimer.start());
            dlg.setVisible(true);
        });

        exitBtn.addActionListener(e -> {
            animationTimer.stop();
            fadeOutAndClose(() -> {
                if (onExit != null) SwingUtilities.invokeLater(onExit);
            });
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                fadeTo(1f, 180, null);
            }
        });
    }

    private void fadeOutAndClose(Runnable afterFade) {
        fadeTo(0f, 160, () -> {
            try {
                dispose();
            } finally {
                if (afterFade != null) afterFade.run();
            }
        });
    }

    private void fadeTo(float target, int durationMs, Runnable onDone) {
        float start;
        try {
            start = getOpacity();
        } catch (Throwable t) {
            if (onDone != null) SwingUtilities.invokeLater(onDone);
            return;
        }
        final float from = start;
        final float to = Math.max(0f, Math.min(1f, target));
        if (Math.abs(from - to) < 0.01f) {
            if (onDone != null) SwingUtilities.invokeLater(onDone);
            return;
        }
        final int fps = 60;
        final int interval = 1000 / fps;
        final int steps = Math.max(1, durationMs / interval);
        final long startTime = System.nanoTime();
        Timer timer = new Timer(interval, null);
        timer.addActionListener(ev -> {
            double t = (System.nanoTime() - startTime) / (durationMs * 1_000_000.0);
            if (t >= 1.0) t = 1.0;
            double eased = t < 0 ? 0 : (t > 1 ? 1 : (t < 0.5 ? 2*t*t : -1 + (4 - 2*t) * t));
            float value = (float)(from + (to - from) * eased);
            try {
                setOpacity(value);
            } catch (Throwable ignored) { }
            if (t >= 1.0) {
                ((Timer) ev.getSource()).stop();
                if (onDone != null) onDone.run();
            }
        });
        timer.setRepeats(true);
        timer.start();
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

                    int w = getWidth();
                    int h = getHeight();

                    // Фон: вертикальный ночной градиент
                    GradientPaint bg = new GradientPaint(0, 0, new Color(5, 25, 60), 0, h, new Color(3, 10, 30));
                    g2d.setPaint(bg);
                    g2d.fillRect(0, 0, w, h);

                    // Сверкающие звёзды (детерминированно)
                    java.util.Random rnd = new java.util.Random(1234);
                    g2d.setColor(new Color(255, 255, 255, 200));
                    for (int i = 0; i < 60; i++) {
                        int sx = rnd.nextInt(Math.max(1, w));
                        int sy = rnd.nextInt(Math.max(1, h / 2));
                        int s = rnd.nextInt(3) + 1;
                        g2d.fillOval(sx, sy, s, s);
                    }

                    // Гирлянда (синусоида) сверху
                    g2d.setStroke(new BasicStroke(4f));
                    g2d.setColor(new Color(30, 90, 30));
                    int y0 = 30;
                    java.awt.geom.Path2D garland = new java.awt.geom.Path2D.Double();
                    garland.moveTo(0, y0);
                    for (int x = 0; x <= w; x += 20) {
                        double y = y0 + Math.sin((x / (double) Math.max(1, w)) * Math.PI * 4) * 14;
                        garland.lineTo(x, y);
                    }
                    g2d.draw(garland);

                    // Лампочки гирлянды
                    int bulbStep = 40;
                    java.awt.Color[] bulbs = new java.awt.Color[] {
                            new Color(220, 50, 50),
                            new Color(240, 200, 20),
                            new Color(30, 150, 140),
                            new Color(200, 120, 180)
                    };
                    for (int x = 0, i = 0; x <= w; x += bulbStep, i++) {
                        double y = y0 + Math.sin((x / (double) Math.max(1, w)) * Math.PI * 4) * 14;
                        int bx = x - 8;
                        int by = (int) y - 8;
                        java.awt.Color c = bulbs[i % bulbs.length];
                        GradientPaint gp = new GradientPaint(bx, by, c.brighter(), bx + 12, by + 12, c.darker());
                        g2d.setPaint(gp);
                        g2d.fillOval(bx, by, 16, 16);
                        g2d.setColor(new Color(0,0,0,40));
                        g2d.drawOval(bx, by, 16, 16);
                    }

                    // Снежные украшения по углам
                    g2d.setColor(new Color(255, 255, 255, 180));
                    drawSnowflake(g2d, 20, h - 40, 14);
                    drawSnowflake(g2d, w - 40, h - 60, 10);
                    drawSnowflake(g2d, w - 60, 20, 12);

                    // Внешняя рамка (как гирлянда) от BORDER_YELLOW
                    g2d.setColor(BORDER_YELLOW);
                    g2d.setStroke(new BasicStroke(6f));
                    g2d.drawRoundRect(3, 3, w - 7, h - 7, 18, 18);

                    g2d.dispose();
                }

                private void drawSnowflake(Graphics2D g2d, int cx, int cy, int size) {
                    int half = size / 2;
                    g2d.setStroke(new BasicStroke(2f));
                    for (int i = 0; i < 6; i++) {
                        double angle = Math.PI / 3 * i;
                        int x1 = cx + (int)(Math.cos(angle) * half);
                        int y1 = cy + (int)(Math.sin(angle) * half);
                        int x2 = cx + (int)(Math.cos(angle) * (half + 6));
                        int y2 = cy + (int)(Math.sin(angle) * (half + 6));
                        g2d.drawLine(cx, cy, x2, y2);
                        g2d.drawLine(x1, y1, x2, y2);
                    }
                }
            };
            content.setBorder(new LineBorder(BORDER_YELLOW, 6));
            content.setPreferredSize(new Dimension(700, 520));

            JLabel title = new JLabel("📜 RULES") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    String txt = getText();
                    Font f = getFont().deriveFont(Font.BOLD, 36f);
                    g2.setFont(f);
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(txt)) / 2;
                    int y = fm.getAscent() + 6;

                    // Тень
                    g2.setColor(new Color(0, 0, 0, 90));
                    g2.drawString(txt, x + 3, y + 3);

                    // Золотой градиент для заголовка
                    GradientPaint gp = new GradientPaint(x, 0, new Color(255, 215, 90), x + fm.stringWidth(txt), 0, new Color(240, 160, 40));
                    g2.setPaint(gp);
                    g2.drawString(txt, x, y);

                    g2.dispose();
                    // НЕ вызывать super.paintComponent(g) — это убирает двойную надпись
                }
            };
            title.setForeground(BORDER_YELLOW);
            title.setHorizontalAlignment(SwingConstants.CENTER);
            title.setFont(new Font("SansSerif", Font.BOLD, 36));
            title.setBorder(new EmptyBorder(20, 20, 10, 20));
            title.setOpaque(false);

            // Восстановленный текст правил (оставлен оригинальный контент)
            String rulesText =
                    "OBJECTIVE:\n" +
                            "Sink all opponent's ships before they sink yours!\n\n" +

                            "GAME PHASES:\n\n" +

                            "1. SETUP PHASE:\n" +
                            "• Place all your ships on your board\n" +
                            "• Ships come in different sizes: 1x1, 1x2, 1x3, and 1x4\n" +
                            "• Ships CANNOT touch each other (not even diagonally)\n" +
                            "• There must be at least one empty cell between ships\n" +
                            "• Drag ships from the right panel to your board\n" +
                            "• Double-click a ship to rotate it (horizontal/vertical)\n" +
                            "• Use RANDOM button for automatic placement\n" +
                            "• Press START when all ships are placed\n\n" +

                            "2. BATTLE PHASE:\n" +
                            "• Players take turns shooting at opponent's grid\n" +
                            "• Click any cell on computer's board to fire\n" +
                            "• White dot = Miss - turn passes to opponent\n" +
                            "• Explosion = Hit - you get another turn!\n" +
                            "• When entire ship is destroyed:\n" +
                            "  - Ship cells marked with skull symbol\n" +
                            "  - All adjacent cells automatically marked as miss\n" +
                            "• First player to sink all enemy ships wins!\n\n" +

                            "CONTROLS:\n" +
                            "• Mouse: Click to shoot/place ships\n" +
                            "• Double-click: Rotate ship during setup\n" +
                            "• Ctrl+Enter: Start battle\n" +

                            "TIPS:\n" +
                            "• After sinking a ship, surrounding cells are revealed\n" +
                            "• Use the move history panel to track your shots\n" +
                            "• Statistics panel shows your progress\n" +
                            "• Complete all 3 levels to win the game!\n\n" +

                            "Good luck, Captain!";

            JTextArea text = new JTextArea(rulesText);
            text.setEditable(false);
            text.setLineWrap(true);
            text.setWrapStyleWord(true);
            text.setOpaque(false);
            text.setForeground(TEXT_WHITE);
            text.setFont(new Font("SansSerif", Font.PLAIN, 16));
            text.setBorder(new EmptyBorder(10, 10, 10, 10));

            JPanel card = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight();
                    GradientPaint gp = new GradientPaint(0, 0, new Color(255, 255, 255, 20), 0, h, new Color(255, 255, 255, 8));
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, w, h, 14, 14);
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            card.setOpaque(false);
            card.setBorder(new EmptyBorder(12, 12, 12, 12));
            card.add(text, BorderLayout.CENTER);

            JScrollPane scroll = new JScrollPane(card);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            scroll.setBorder(null);
            scroll.setPreferredSize(new Dimension(700 - 80, 520 - 160));
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            scroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
                @Override protected void configureScrollBarColors() {
                    this.thumbColor = new Color(200, 200, 200, 120);
                    this.trackColor = new Color(0,0,0,0);
                }
            });

            JPanel center = new JPanel(new BorderLayout());
            center.setOpaque(false);
            center.setBorder(new EmptyBorder(10, 30, 10, 30));
            center.add(scroll, BorderLayout.CENTER);

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttons.setOpaque(false);
            JButton close = new JButton("Close") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight();
                    GradientPaint gp = new GradientPaint(0, 0, new Color(220, 80, 80), 0, h, new Color(180, 40, 40));
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, w, h, 12, 12);
                    g2.setColor(BORDER_YELLOW);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);

                    // маленькая снежинка-иконка слева
                    g2.setColor(new Color(255, 255, 255, 200));
                    int s = Math.min(12, h - 8);
                    int cx = 8 + s / 2;
                    int cy = h / 2;
                    for (int i = 0; i < 6; i++) {
                        double angle = Math.PI / 3 * i;
                        int x2 = cx + (int)(Math.cos(angle) * (s / 2));
                        int y2 = cy + (int)(Math.sin(angle) * (s / 2));
                        g2.drawLine(cx, cy, x2, y2);
                    }

                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            close.setOpaque(false);
            close.setContentAreaFilled(false);
            close.setFocusPainted(false);
            close.setFont(new Font("SansSerif", Font.PLAIN, 16));
            close.setForeground(TEXT_WHITE);
            close.setBorder(null);
            close.setPreferredSize(new Dimension(120, 36));
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
