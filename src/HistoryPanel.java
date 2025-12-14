import javax.swing.*;
import java.awt.*;

public class HistoryPanel extends JPanel {

    private final JPanel content;
    private final JScrollPane scrollPane;

    public HistoryPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(230, 0));
        setBackground(new Color(15, 35, 60));

        JLabel title = new JLabel("Move History", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        add(title, BorderLayout.NORTH);

        content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(10, 25, 45));
        content.setBorder(BorderFactory.createEmptyBorder(12, 0, 25, 0));

        scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);


        add(scrollPane, BorderLayout.CENTER);
    }

    public void addMove(Move move) {
        content.add(createRow(move));
        content.revalidate();
        scrollToBottom();
    }

    public void clear() {
        content.removeAll();
        content.revalidate();
        content.repaint();
    }

    private JPanel createRow(Move move) {
        JLabel label = new JLabel(move.toDisplayString());
        label.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        if (move.isSunk()) {
            label.setForeground(new Color(255, 80, 80));
        } else if (move.isHit()) {
            label.setForeground(new Color(255, 215, 0));
        } else {
            label.setForeground(new Color(180, 180, 180));
        }

        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(10, 25, 45));
        row.add(label, BorderLayout.WEST);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        return row;
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }
}
