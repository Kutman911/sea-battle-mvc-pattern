package common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ResultDialog extends JDialog {
    private static final Color BG_GREEN = new Color(20, 120, 75);
    private static final Color BORDER_YELLOW = new Color(237, 176, 36);
    private static final Color BUTTON_GREEN = new Color(28, 150, 90);
    private static final Color BG_RED = new Color(150, 40, 40);
    private static final Color BUTTON_RED = new Color(180, 50, 50);
    private static final Color TEXT_WHITE = Color.WHITE;

    public ResultDialog(Window parent, boolean playerWon, int level, Runnable onReplay, Runnable onNext, Runnable onExit) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Color bg = playerWon ? BG_GREEN : BG_RED;
        Color borderColor = playerWon ? BORDER_YELLOW : BUTTON_RED.darker();
        Color buttonColor = playerWon ? BUTTON_GREEN : BUTTON_RED;

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(bg);
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

        JButton replayBtn = createBigButton("PLAY AGAIN", buttonColor, borderColor);
        JButton nextBtn = createBigButton("NEXT LEVEL", buttonColor, borderColor);
        JButton exitBtn = createBigButton("EXIT", buttonColor, borderColor);

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

    private JButton createBigButton(String text, Color btnColor, Color borderColor) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 18));
        b.setForeground(TEXT_WHITE);
        b.setBackground(btnColor);
        b.setOpaque(true);
        b.setPreferredSize(new Dimension(200, 64));
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 5),
                new EmptyBorder(8, 16, 8, 16)
        ));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (b.isEnabled()) b.setBackground(btnColor.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (b.isEnabled()) b.setBackground(btnColor);
            }
        });
        return b;
    }
}
