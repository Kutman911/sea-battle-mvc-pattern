import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
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

    public MainMenu(Window parent, Runnable onStart, Runnable onSettings, Runnable onRules, Runnable onExit) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Инициализация снежинок
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

                // Градиент синего фона как на скриншоте
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(70, 130, 180),
                        0, getHeight(), new Color(70, 130, 180)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Звёзды
                g2d.setColor(Color.WHITE);
                Random rand = new Random(42);
                for (int i = 0; i < 100; i++) {
                    int x = rand.nextInt(getWidth());
                    int y = rand.nextInt(getHeight());
                    int size = rand.nextInt(3) + 1;
                    g2d.fillOval(x, y, size, size);

                    // Крестик на звезде
                    if (size > 1) {
                        g2d.drawLine(x - 2, y + size/2, x + size + 2, y + size/2);
                        g2d.drawLine(x + size/2, y - 2, x + size/2, y + size + 2);
                    }
                }

                // снежинки
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

                // Тень
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
        JButton settingsBtn = createMenuButton("⚙ SETTINGS", new Color(40, 167, 69));
        JButton rulesBtn = createMenuButton("📜 RULES", new Color(23, 162, 184));
        JButton exitBtn = createMenuButton("🎁 EXIT", new Color(108, 117, 125));

        gbc.gridy = 0;
        buttons.add(startBtn, gbc);
        gbc.gridy = 1;
        buttons.add(settingsBtn, gbc);
        gbc.gridy = 2;
        buttons.add(rulesBtn, gbc);
        gbc.gridy = 3;
        buttons.add(exitBtn, gbc);

        content.add(title, BorderLayout.NORTH);
        content.add(buttons, BorderLayout.CENTER);

        setContentPane(content);
        pack();
        setLocationRelativeTo(parent);

        // Анимация снега
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
            SwingUtilities.invokeLater(onStart);
        });
        settingsBtn.addActionListener(e -> SwingUtilities.invokeLater(onSettings));
        rulesBtn.addActionListener(e -> SwingUtilities.invokeLater(onRules));
        exitBtn.addActionListener(e -> {
            animationTimer.stop();
            dispose();
            SwingUtilities.invokeLater(onExit);
        });
    }

    private JButton createMenuButton(String text, Color baseColor) {
        class ScalableButton extends JButton {
            private float scale = 1.0f;

            public ScalableButton(String text) {
                super(text);
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
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
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

            // Лучи снежинки
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
}
