package common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ResultDialog extends JDialog {
    private static final Color BORDER_YELLOW = new Color(237, 176, 36);
    private static final Color BUTTON_GREEN = new Color(0, 180, 110);
    private static final Color BUTTON_RED = new Color(230, 70, 70);
    private static final Color TEXT_WHITE = Color.WHITE;

    public ResultDialog(Window parent, boolean playerWon, int level, Runnable onReplay, Runnable onNext, Runnable onExit) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Color borderColor = playerWon ? BORDER_YELLOW : BUTTON_RED.darker();
        Color buttonColor = playerWon ? BUTTON_GREEN : BUTTON_RED;

        JPanel content = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                Color top = playerWon ? new Color(60, 170, 110) : new Color(120, 60, 60);
                Color bottom = playerWon ? new Color(20, 90, 60) : new Color(70, 25, 25);
                g2.setPaint(new GradientPaint(0, 0, top, 0, h, bottom));
                g2.fillRoundRect(0, 0, w, h, 28, 28);

                drawGarland(g2, w);
                drawSnow(g2, w, h);
                drawTrees(g2, w, h);

                g2.dispose();
            }
        };
        content.setBorder(new LineBorder(borderColor, 6));
        content.setPreferredSize(new Dimension(680, 360));


        JPanel topDecor = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(borderColor);
                int w = getWidth();
                int xStart = (w - 120) / 2;
                for (int i = 0; i < 5; i++) {
                    g.fillRect(xStart + i * 24, 10, 14, 14);
                }
            }
        };
        topDecor.setOpaque(false);
        topDecor.setPreferredSize(new Dimension(10, 40));

        JLabel title = new JLabel(playerWon ? "YOU WIN!" : "YOU LOSE!");
        title.setForeground(TEXT_WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 56));
        title.setBorder(new EmptyBorder(6, 6, 6, 6));

        JLabel message = new JLabel(
                playerWon ? "Congratulations! You sunk all enemy ships!" : "Computer has won. Try again?"
        );
        message.setForeground(new Color(220, 240, 230));
        message.setHorizontalAlignment(SwingConstants.CENTER);
        message.setFont(new Font("SansSerif", Font.PLAIN, 18));
        message.setBorder(new EmptyBorder(8, 40, 8, 40));

        JPanel buttons = new JPanel(new GridBagLayout());
        buttons.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 16, 12, 16);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton replayBtn = createBigButton("PLAY AGAIN", buttonColor);
        JButton nextBtn = createBigButton("NEXT LEVEL", buttonColor);
        JButton exitBtn = createBigButton("EXIT", buttonColor);

        if (!playerWon || level >= 3) {
            nextBtn.setEnabled(false);
            nextBtn.setForeground(new Color(200, 200, 200));
        }

        gbc.gridx = 0;
        gbc.weightx = 1;
        buttons.add(replayBtn, gbc);

        gbc.gridx = 1;
        buttons.add(nextBtn, gbc);

        gbc.gridx = 2;
        buttons.add(exitBtn, gbc);

        content.add(topDecor, BorderLayout.NORTH);
        content.add(title, BorderLayout.CENTER);
        JPanel mid = new JPanel(new BorderLayout());
        mid.setOpaque(false);
        mid.add(message, BorderLayout.NORTH);
        mid.add(buttons, BorderLayout.SOUTH);
        content.add(mid, BorderLayout.SOUTH);

        setContentPane(content);
        pack();
        setLocationRelativeTo(parent);

        replayBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(onReplay);
        });
        nextBtn.addActionListener(e -> {
            if (nextBtn.isEnabled()) {
                dispose();
                SwingUtilities.invokeLater(onNext);
            }
        });
        exitBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(onExit);
        });

        getRootPane().registerKeyboardAction(e -> {
            dispose();
            SwingUtilities.invokeLater(onExit);
        }, KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private JButton createBigButton(String text, Color btnColor) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 18));
        b.setForeground(new Color(20, 120, 75));

        b.setOpaque(true);
        b.setContentAreaFilled(true);

        Color base = btnColor;
        b.setBackground(base);

        b.setPreferredSize(new Dimension(200, 64));
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(255, 255, 255, 180), 3),
                new EmptyBorder(10, 16, 10, 16)
        ));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (b.isEnabled()) b.setBackground(base.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (b.isEnabled()) b.setBackground(base);
            }
        });
        return b;
    }

    private void drawSnow(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(255, 255, 255, 180));
        int seed = 12345;

        for (int i = 0; i < 120; i++) {
            int x = (seed * (i + 17)) % w;
            int y = (seed * (i + 31)) % h;
            int s = 1 + (i % 3);
            g2.fillOval(x, y, s, s);
        }
    }

    private void drawTrees(Graphics2D g2, int w, int h) {
        int baseY = h - 10;

        g2.setColor(new Color(0, 50, 35, 140));
        for (int i = 0; i < 7; i++) {
            int x = (int)(i * (w / 6.5));
            drawPine(g2, x, baseY + 12, 70, 90);
        }

        g2.setColor(new Color(0, 80, 45, 190));
        for (int i = 0; i < 4; i++) {
            int x = (int)(i * (w / 3.8)) + 20;
            drawPine(g2, x, baseY, 100, 120);
        }

        g2.setColor(new Color(255, 255, 255, 160));
        g2.fillRoundRect(-20, h - 50, w + 40, 60, 40, 40);
    }
    private void drawPine(Graphics2D g2, int cx, int baseY, int w, int h) {
        int topY = baseY - h;

        Polygon p1 = new Polygon(
                new int[]{cx, cx - w / 2, cx + w / 2},
                new int[]{topY, topY + h / 2, topY + h / 2},
                3
        );
        Polygon p2 = new Polygon(
                new int[]{cx, cx - (int)(w * 0.6), cx + (int)(w * 0.6)},
                new int[]{topY + h / 3, baseY, baseY},
                3
        );

        g2.fillPolygon(p1);
        g2.fillPolygon(p2);

        g2.setColor(new Color(255, 255, 255, 130));
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx - w / 4, topY + h / 2, cx + w / 4, topY + h / 2);
        g2.drawLine(cx - w / 3, topY + (int)(h * 0.8), cx + w / 3, topY + (int)(h * 0.8));
    }
    private void drawGarland(Graphics2D g2, int w) {
        int y = 26;

        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(30, 20, 10, 140));
        g2.drawLine(30, y, w - 30, y);

        Color[] bulbs = {
                new Color(255, 60, 60),
                new Color(255, 200, 60),
                new Color(60, 220, 140),
                new Color(60, 160, 255),
                new Color(255, 80, 200)
        };

        int step = 55;
        for (int x = 50, i = 0; x < w - 50; x += step, i++) {
            Color c = bulbs[i % bulbs.length];

            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 90));
            g2.fillOval(x - 10, y - 2, 20, 20);

            g2.setColor(c);
            g2.fillOval(x - 7, y + 1, 14, 14);

            g2.setColor(new Color(255, 255, 255, 180));
            g2.fillOval(x - 4, y + 4, 4, 4);

            g2.setColor(new Color(120, 90, 60, 200));
            g2.fillRect(x - 2, y - 2, 4, 4);
        }
    }

}
