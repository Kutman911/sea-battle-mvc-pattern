import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.util.Random;

public class ResultDialog extends JDialog {

    private static final Color BACKGROUND_BLUE = new Color(75, 139, 181);
    private static final Color CONTAINER_RED = new Color(165, 25, 25);
    private static final Color CONTAINER_GREEN = new Color(25, 111, 61);
    private static final Color TEXT_WHITE = Color.WHITE;
    private static final Color TEXT_GOLD = new Color(255, 215, 0);
    private static final Color ACCENT_LIGHT = new Color(255, 250, 240);

    private SnowPanel snowPanel;

    public ResultDialog(Frame owner, boolean isWin, Runnable onRetry) {
        super(owner, isWin ? "Victory!" : "Defeat", true);

        snowPanel = new SnowPanel(isWin);
        setContentPane(snowPanel);
        snowPanel.setLayout(new GridBagLayout());

        String message = isWin
                ? "Congratulations! You sunk all enemy ships!"
                : "All your ships were sunk. Try again!";
        String title = isWin ? "YOU WIN!" : "YOU LOSE";
        Color containerColor = isWin ? CONTAINER_GREEN : CONTAINER_RED;

        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BorderLayout(25, 25));
        containerPanel.setBackground(containerColor);
        containerPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        new LineBorder(TEXT_GOLD, 4, true),
                        new EmptyBorder(40, 50, 40, 50)
                )
        );

        JPanel decorPanel = new JPanel();
        decorPanel.setLayout(new BorderLayout());
        decorPanel.setBackground(containerColor);

        JLabel topDecor = new JLabel(isWin ? "★ ★ ★ ★ ★" : "* * * * *", SwingConstants.CENTER);
        topDecor.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        topDecor.setForeground(TEXT_GOLD);
        decorPanel.add(topDecor, BorderLayout.NORTH);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
        titleLabel.setForeground(TEXT_WHITE);
        decorPanel.add(titleLabel, BorderLayout.CENTER);

        containerPanel.add(decorPanel, BorderLayout.NORTH);

        JLabel messageLabel = new JLabel("<html><center>" + message + "</center></html>", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        messageLabel.setForeground(ACCENT_LIGHT);
        containerPanel.add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(containerColor);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        JButton retryButton = createStyledButton("PLAY AGAIN", e -> {
            if (onRetry != null) {
                onRetry.run();
            }
            snowPanel.stopAnimation();
            setVisible(false);
            dispose();
        }, containerColor);
        buttonPanel.add(retryButton);

        JButton exitButton = createStyledButton("EXIT", e -> {
            snowPanel.stopAnimation();
            System.exit(0);
        }, containerColor);
        buttonPanel.add(exitButton);

        containerPanel.add(buttonPanel, BorderLayout.SOUTH);

        snowPanel.add(containerPanel);

        setSize(650, 450);
        setResizable(false);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        snowPanel.startAnimation();
    }

    private JButton createStyledButton(String text, ActionListener action, Color baseColor) {
        JButton button = new JButton(text);

        button.setPreferredSize(new Dimension(180, 50));
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));

        button.setForeground(TEXT_WHITE);
        button.setBackground(baseColor.brighter());

        button.setBorder(new LineBorder(TEXT_GOLD, 3, true));
        button.setFocusPainted(false);

        button.setUI(new BasicButtonUI() {
            @Override
            protected void installDefaults(AbstractButton b) {
                super.installDefaults(b);
                b.setOpaque(true);
                b.setBorderPainted(true);
            }

            @Override
            protected void paintButtonPressed(Graphics g, AbstractButton b) {
                g.setColor(baseColor.darker());
                g.fillRect(0, 0, b.getWidth(), b.getHeight());
            }
        });

        button.addActionListener(action);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor.brighter().brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor.brighter());
            }
        });

        return button;
    }

    private static class SnowPanel extends JPanel {
        private Snowflake[] snowflakes;
        private Timer timer;
        private Random random = new Random();
        private boolean isWin;

        public SnowPanel(boolean isWin) {
            this.isWin = isWin;
            setBackground(BACKGROUND_BLUE);
            snowflakes = new Snowflake[80];
            for (int i = 0; i < snowflakes.length; i++) {
                snowflakes[i] = new Snowflake(random);
            }
        }

        public void startAnimation() {
            timer = new Timer(30, e -> {
                for (Snowflake snowflake : snowflakes) {
                    snowflake.fall(getHeight());
                }
                repaint();
            });
            timer.start();
        }

        public void stopAnimation() {
            if (timer != null) {
                timer.stop();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (Snowflake snowflake : snowflakes) {
                snowflake.draw(g2d);
            }

            if (isWin) {
                drawChristmasTree(g2d, 50, getHeight() - 100, 40);
                drawChristmasTree(g2d, getWidth() - 80, getHeight() - 100, 40);
            }

            drawStars(g2d);
        }

        private void drawChristmasTree(Graphics2D g2d, int x, int y, int size) {
            g2d.setColor(new Color(101, 67, 33));
            g2d.fillRect(x + size / 3, y + size, size / 3, size / 2);

            g2d.setColor(new Color(34, 139, 34));
            int[] xPoints1 = {x, x + size / 2, x + size};
            int[] yPoints1 = {y + size * 2 / 3, y, y + size * 2 / 3};
            g2d.fillPolygon(xPoints1, yPoints1, 3);

            int[] xPoints2 = {x + size / 6, x + size / 2, x + size * 5 / 6};
            int[] yPoints2 = {y + size / 2, y - size / 4, y + size / 2};
            g2d.fillPolygon(xPoints2, yPoints2, 3);

            int[] xPoints3 = {x + size / 4, x + size / 2, x + size * 3 / 4};
            int[] yPoints3 = {y + size / 4, y - size / 2, y + size / 4};
            g2d.fillPolygon(xPoints3, yPoints3, 3);

            g2d.setColor(TEXT_GOLD);
            g2d.fillOval(x + size / 2 - 5, y - size / 2 - 5, 10, 10);
        }

        private void drawStars(Graphics2D g2d) {
            g2d.setColor(new Color(255, 255, 255, 150));
            Random r = new Random(42); // Фиксированный seed для постоянных позиций
            for (int i = 0; i < 30; i++) {
                int x = r.nextInt(getWidth());
                int y = r.nextInt(getHeight() / 2);
                int size = 2 + r.nextInt(3);

                g2d.fillOval(x, y, size, size);

                if (i % 3 == 0) {
                    g2d.drawLine(x - 3, y + size/2, x + size + 3, y + size/2);
                    g2d.drawLine(x + size/2, y - 3, x + size/2, y + size + 3);
                }
            }
        }
    }

    private static class Snowflake {
        private double x, y;
        private double speed;
        private int size;
        private double drift;
        private double driftOffset;
        private int opacity;

        public Snowflake(Random random) {
            reset(random, -50);
        }

        private void reset(Random random, int startY) {
            x = random.nextInt(2000);
            y = startY;
            speed = 1 + random.nextDouble() * 2;
            size = 3 + random.nextInt(5);
            drift = random.nextDouble() * 0.5 - 0.25;
            driftOffset = 0;
            opacity = 150 + random.nextInt(106);
        }

        public void fall(int height) {
            y += speed;
            driftOffset += 0.05;
            x += Math.sin(driftOffset) * drift;

            if (y > height) {
                reset(new Random(), -20);
            }
        }

        public void draw(Graphics2D g2d) {
            g2d.setColor(new Color(255, 255, 255, opacity));

            Ellipse2D.Double snowflake = new Ellipse2D.Double(x, y, size, size);
            g2d.fill(snowflake);

            if (size > 4) {
                g2d.setStroke(new BasicStroke(1));
                int centerX = (int)(x + size / 2);
                int centerY = (int)(y + size / 2);
                int armLength = size;

                g2d.drawLine(centerX - armLength, centerY, centerX + armLength, centerY);
                g2d.drawLine(centerX, centerY - armLength, centerX, centerY + armLength);

                int diag = (int)(armLength * 0.7);
                g2d.drawLine(centerX - diag, centerY - diag, centerX + diag, centerY + diag);
                g2d.drawLine(centerX - diag, centerY + diag, centerX + diag, centerY - diag);
            }
        }
    }
}