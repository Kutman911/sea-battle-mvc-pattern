import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Canvas extends JPanel {
    private final Model model;

    private final int cellSize = 40;
    private final int gap = 8;
    private final int top = 100;
    private final int left = 60;
    private final int betweenBoards = 120;

    public Canvas(Model model) {
        this.model = model;
        setBackground(new Color(35, 35, 35));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }

    private Rectangle cellRect(int startX, int startY, int col, int row) {
        int x = startX + col * (cellSize + gap);
        int y = startY + row * (cellSize + gap);
        return new Rectangle(x, y, cellSize, cellSize);
    }

    private void handleClick(int mx, int my) {
        if (model.isGameOver()) return;

        // Right board (computer)
        int startXRight = left + boardPixelSize() + betweenBoards;
        int startY = top;

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Rectangle r = cellRect(startXRight, startY, col, row);
                if (r.contains(mx, my)) {
                    model.playerShoot(col, row);
                    repaint();
                    return;
                }
            }
        }
    }

    private int boardPixelSize() {
        return 10 * cellSize + 9 * gap;
    }

    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int startXLeft = left;
        int startXRight = left + boardPixelSize() + betweenBoards;
        int startY = top;

        // Titles
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 18f));
        g.drawString("Ваш флот", startXLeft, startY - 40);
        g.drawString("Поле компьютера", startXRight, startY - 40);
        g.setFont(g.getFont().deriveFont(Font.PLAIN, 14f));
        g.drawString(model.getStatusText(), left, startY + boardPixelSize() + 50);

        // Draw both boards
        drawBoard(g, model.getPlayerBoard(), startXLeft, startY, true);
        drawBoard(g, model.getComputerBoard(), startXRight, startY, false);
    }

    private void drawBoard(Graphics g, int[][] board, int startX, int startY, boolean showShips) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Rectangle r = cellRect(startX, startY, col, row);

                // Cell background
                g.setColor(new Color(55, 55, 55));
                g.fillRect(r.x, r.y, r.width, r.height);

                int val = board[row][col];

                // Draw ship (only on player's board)
                if (showShips && val == Model.SHIP) {
                    g.setColor(new Color(120, 120, 160));
                    g.fillRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4);
                }

                // Hits and misses
                if (val == Model.HIT) {
                    g.setColor(Color.RED);
                    g.drawLine(r.x + 6, r.y + 6, r.x + r.width - 6, r.y + r.height - 6);
                    g.drawLine(r.x + 6, r.y + r.height - 6, r.x + r.width - 6, r.y + 6);
                } else if (val == Model.MISS) {
                    g.setColor(new Color(90, 180, 255));
                    g.fillOval(r.x + r.width / 2 - 4, r.y + r.height / 2 - 4, 8, 8);
                }

                // Cell border
                g.setColor(Color.YELLOW);
                g.drawRect(r.x, r.y, r.width, r.height);
            }
        }
    }
}
