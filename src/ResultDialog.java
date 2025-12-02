import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;

public class ResultDialog extends JDialog {

    private static final Color BACKGROUND_BLUE = new Color(74, 144, 226); // Основной светло-синий фон
    private static final Color CONTAINER_BLUE = new Color(41, 60, 90); // Тёмно-синий для контейнеров (как на изображении)
    private static final Color TEXT_WHITE = Color.WHITE;
    private static final Color ACCENT_LIGHT = new Color(174, 214, 241); // Светло-голубой акцент
    private static final Color WIN_COLOR = new Color(46, 204, 113); // Зелёный для победы
    private static final Color LOSE_COLOR = new Color(255, 100, 100); // Красный для поражения

    public ResultDialog(Frame owner, boolean isWin, Runnable onRetry) {
        super(owner, isWin ? "✔ Victory!" : "✘ Defeat…", true);

        getContentPane().setBackground(BACKGROUND_BLUE);

        String message = isWin ? "Congratulations! You sunk all enemy ships." : "All your ships were sunk. Better luck next time!";
        String title = isWin ? "You Win!" : "You Lose!";
        Color titleColor = isWin ? WIN_COLOR : LOSE_COLOR;

        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BorderLayout(25, 25));
        containerPanel.setBackground(CONTAINER_BLUE); // Тёмный фон контейнера
        containerPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        new LineBorder(ACCENT_LIGHT, 2, true), // Светлая рамка, имитирующая границу
                        new EmptyBorder(40, 50, 40, 50) // Внутренний отступ
                )
        );

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 48)); // Более крупный и чистый шрифт
        titleLabel.setForeground(TEXT_WHITE);
        containerPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        messageLabel.setForeground(ACCENT_LIGHT);
        containerPanel.add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(CONTAINER_BLUE);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        JButton retryButton = createStyledButton("PLAY AGAIN?", e -> {
            if (onRetry != null) {
                onRetry.run();
            }
            setVisible(false);
            dispose();
        });
        buttonPanel.add(retryButton);

        JButton exitButton = createStyledButton("EXIT", e -> System.exit(0));
        buttonPanel.add(exitButton);

        containerPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BACKGROUND_BLUE);
        wrapper.add(containerPanel);

        getContentPane().add(wrapper);
        setSize(600, 400);
        setResizable(false);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private JButton createStyledButton(String text, ActionListener action) {
        JButton button = new JButton(text);

        button.setPreferredSize(new Dimension(160, 45));
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));

        button.setForeground(TEXT_WHITE);
        button.setBackground(CONTAINER_BLUE.brighter());

        button.setBorder(new LineBorder(ACCENT_LIGHT, 2, true));
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
                g.setColor(CONTAINER_BLUE.darker());
                g.fillRect(0, 0, b.getWidth(), b.getHeight());
            }
        });

        button.addActionListener(action);
        return button;
    }
}
