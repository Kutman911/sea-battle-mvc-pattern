import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Canvas extends JPanel {

    private Model model;
    private int[][] arrayOfIndexes;
    private Random snowRandom;
    private final int PADDING = 50;
    private final int BOARD_SPACING = 50;
    private final int TOP_Y = 150;
    private final int BOARD_WIDTH_PX = Coordinates.WIDTH * 10;
    private int playerBoardX;
    private int computerBoardX;

    public Canvas(Model model) {
        this.model = model;
        setBackground(new Color(75, 139, 181));
        int[][] array = model.getArrayOfIndexes();
        arrayOfIndexes = new int[2][array[0].length];
        snowRandom = new Random(42);

        for(int index = 0; index < array[0].length; index++) {
            arrayOfIndexes[0][index] = array[0][index];
            arrayOfIndexes[1][index] = array[1][index];
        }
    }

    private void calculateBoardPositions() {
        int panelWidth = getWidth();

        if (model.isSetupPhase()) {
            int offBoardShipAreaWidth = (int)(BOARD_WIDTH_PX * 0.9);
            int totalSetupWidth = BOARD_WIDTH_PX + BOARD_SPACING + offBoardShipAreaWidth;
            int startX = (panelWidth - totalSetupWidth) / 2;
            playerBoardX = startX;
            computerBoardX = startX + BOARD_WIDTH_PX + BOARD_SPACING;
        } else {
            int totalBattleWidth = BOARD_WIDTH_PX * 2 + BOARD_SPACING;
            int startX = (panelWidth - totalBattleWidth) / 2;
            playerBoardX = startX;
            computerBoardX = startX + BOARD_WIDTH_PX + BOARD_SPACING;
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        calculateBoardPositions();
        drawSnowflakes(g2);

        if (model.won() && model.getLevelWindow().getCurrentLevel() >= 3) {
            drawWon(g2);
        } else if (model.isSetupPhase()) {
            drawDesktopPlayer(g2, true);
        } else {
            drawDesktopPlayer(g2, false);
            drawDesktopComputer(g2);
        }

        drawLevelWindow(g2);
    }

    public Point getComputerBoardPosition() {
        return new Point(computerBoardX, TOP_Y);
    }

    public Point getPlayerBoardPosition() {
        return new Point(playerBoardX, TOP_Y);
    }

    @Override
    public Dimension getPreferredSize() {
        int totalBattleWidth = BOARD_WIDTH_PX * 2 + BOARD_SPACING + PADDING * 2;
        int maxSetupWidth = BOARD_WIDTH_PX + PADDING + Coordinates.WIDTH + PADDING + PADDING;
        int width = Math.max(totalBattleWidth, maxSetupWidth + 50);
        int height = TOP_Y + BOARD_WIDTH_PX + PADDING;
        return new Dimension(width, height);
    }

    private void drawWon(Graphics2D g) {
        int width = Coordinates.WIDTH;
        int height = Coordinates.HEIGHT;
        int totalWidth = width * 10;

        Point centerPos = getCenteredPosition(totalWidth, width * 10);
        int x = centerPos.x;
        int y = centerPos.y;

        g.setFont(new Font("Bernard MT Condensed", Font.BOLD, 50));
        g.setColor(Color.GREEN);
        g.drawString("You won!", x + (width * 2), y - 40);

        for(int index = 0; index < 4; index++) {
            drawShipFromIndexes(g, x, y, width, height, index, index, 1);
        }

        for(int index = 4; index < 10; index += 2) {
            drawShipFromIndexes(g, x, y, width, height, index, index + 1, 2);
        }

        drawShipFromIndexes(g, x, y, width, height, 10, 12, 3);
        drawShipFromIndexes(g, x, y, width, height, 13, 15, 3);

        drawShipFromIndexes(g, x, y, width, height, 16, 19, 4);

        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(3));
        g.drawRect(x - 10, y - 10, width * 10 + 20, height * 10 + 20);
    }

    private void drawShipFromIndexes(Graphics2D g, int baseX, int baseY, int width, int height,
                                     int startIndex, int endIndex, int size) {
        int i1 = arrayOfIndexes[0][startIndex];
        int j1 = arrayOfIndexes[1][startIndex];
        int i2 = arrayOfIndexes[0][endIndex];
        int j2 = arrayOfIndexes[1][endIndex];

        boolean horizontal = (i1 == i2);

        drawChristmasShip(g,
                baseX + (width * j1),
                baseY + (height * i1),
                horizontal ? width * size : width,
                horizontal ? height : height * size,
                size,
                !horizontal);
    }

    private void drawDesktopComputer(Graphics2D g) {
        drawGridLabels(g, "Desktop");
        int[][] board = model.getComputerPlayer().getBoard();

        int x = computerBoardX;
        int y = TOP_Y;

        drawBoardLabel(g, "Computer's Board", x, y);
        drawBoard(g, board, x, y, false);
    }

    private void drawGridLabels(Graphics2D g, String state) {
        int width = Coordinates.WIDTH;
        int height = Coordinates.HEIGHT;

        int x, y;
        if (state.equals("Desktop")) {
            x = computerBoardX + 510;
            y = TOP_Y + 40;
        } else if (state.equals("Player")) {
            x = playerBoardX - 35;
            y = TOP_Y + 40;
        } else {
            return;
        }

        g.setFont(new Font("Bernard MT Condensed", Font.BOLD, 35));
        g.setColor(Color.WHITE);

        final String row = "12345678910";
        final String column = "ABCDEFGHIJ";

        for (int i = 0; i < row.length(); i++) {
            if (i == row.length() - 2) {
                g.drawString(row.substring(i), x, y);
                break;
            }
            g.drawString(String.valueOf(row.charAt(i)), x, y);
            y += height;
        }

        x = state.equals("Player") ? playerBoardX + 15 : computerBoardX + 15;
        y = TOP_Y - 10;

        for (int i = 0; i < column.length(); i++) {
            g.drawString(String.valueOf(column.charAt(i)), x, y);
            x += width;
        }
    }

    private void drawDesktopPlayer(Graphics2D g, boolean drawOffBoardShips) {
        drawGridLabels(g, "Player");
        int width = Coordinates.WIDTH;
        int height = Coordinates.HEIGHT;

        int boardX = playerBoardX;
        int boardY = TOP_Y;
        int[][] desktopPlayer = model.getDesktopPlayer();

        String label = drawOffBoardShips ? "Your Board (Setup)" : "Your Board";
        drawBoardLabel(g, label, boardX, boardY);

        for (Ship ship : model.getPlayerShips()) {
            drawShip(g, ship, boardX, boardY, width, height, drawOffBoardShips);
        }

        drawBoard(g, desktopPlayer, boardX, boardY, true);

        if (drawOffBoardShips) {
            drawOffBoardShips(g, width, height);
        }
    }

    private void drawBoardLabel(Graphics2D g, String label, int boardX, int boardY) {
        int width = Coordinates.WIDTH;
        g.setFont(new Font("Bernard MT Condensed", Font.BOLD, 30));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        int labelWidth = fm.stringWidth(label);
        int labelX = boardX + (width * 10 - labelWidth) / 2;
        g.drawString(label, labelX, boardY - 70);
    }

    private void drawShip(Graphics2D g, Ship ship, int boardX, int boardY, int width, int height, boolean isSetup) {
        int shipDrawX, shipDrawY, drawWidth, drawHeight;

        if (ship.isDragging()) {
            shipDrawX = ship.getX();
            shipDrawY = ship.getY();

            if (model.isValidPlacement(ship)) {
                g.setColor(new Color(0, 255, 0, 100));
            } else {
                g.setColor(new Color(255, 0, 0, 100));
            }

            drawWidth = ship.isVertical() ? width : width * ship.getSize();
            drawHeight = ship.isVertical() ? height * ship.getSize() : height;
            g.fillRect(shipDrawX, shipDrawY, drawWidth, drawHeight);

        } else if (ship.isPlaced()) {
            shipDrawX = boardX + ship.getX() * width;
            shipDrawY = boardY + ship.getY() * height;
            drawWidth = ship.isVertical() ? width : width * ship.getSize();
            drawHeight = ship.isVertical() ? height * ship.getSize() : height;
        } else {
            return;
        }

        drawChristmasShip(g, shipDrawX, shipDrawY, drawWidth, drawHeight, ship.getSize(), ship.isVertical());
    }

    private void drawBoard(Graphics2D g, int[][] board, int startX, int startY, boolean isPlayerBoard) {
        int width = Coordinates.WIDTH;
        int height = Coordinates.HEIGHT;
        int x = startX;
        int y = startY;

        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                int element = board[i][j];

                if(element == -1) {
                    g.setColor(new Color(30, 60, 100));
                    g.fillRect(x, y, width, height);
                    g.setColor(Color.WHITE);
                    int cx = x + width / 2;
                    int cy = y + height / 2;
                    g.fillOval(cx - 6, cy - 6, 12, 12);
                } else if(element == -9) {
                    if (!isPlayerBoard) {
                        g.setColor(new Color(200, 50, 50));
                        g.fillRect(x, y, width, height);
                    }
                    drawCenteredEmoji(g, "💥", x, y, width, height);
                } else if (element == -8) {
                    if (!isPlayerBoard) {
                        g.setColor(new Color(160, 30, 30));
                        g.fillRect(x, y, width, height);
                    }
                    g.setColor(new Color(0, 200, 0));
                    g.setStroke(new BasicStroke(3));
                    g.drawRect(x, y, width, height);
                    drawCenteredEmoji(g, "☠", x, y, width, height);
                }

                g.setColor(Color.WHITE);
                g.setStroke(new BasicStroke(1));
                g.drawRect(x, y, width, height);
                x += width;
            }
            x = startX;
            y += height;
        }
    }

    private void drawOffBoardShips(Graphics2D g, int width, int height) {
        int offBoardX = computerBoardX;
        int offBoardTopY = TOP_Y;

        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        String instructionLabel = "Drag ships (double-click to rotate)";
        int colSpacing = 30;
        int colWidth = Coordinates.WIDTH * 5;
        int totalOffBoardWidth = colWidth * 2 + colSpacing;
        int instructionX = offBoardX + (totalOffBoardWidth - fm.stringWidth(instructionLabel)) / 2;
        g.drawString(instructionLabel, Math.max(offBoardX, instructionX), TOP_Y - 20);

        int cellGapY = 20;
        int index = 0;
        for (Ship ship : model.getPlayerShips()) {
            if (!ship.isPlaced() && !ship.isDragging()) {
                int col = index % 2;
                int row = index / 2;

                int shipDrawX = offBoardX + col * (colWidth + colSpacing);
                int shipDrawY = offBoardTopY + row * (height + cellGapY);

                int drawWidth = ship.isVertical() ? width : width * ship.getSize();
                int drawHeight = ship.isVertical() ? height * ship.getSize() : height;

                drawChristmasShip(g, shipDrawX, shipDrawY, drawWidth, drawHeight, ship.getSize(), ship.isVertical());
                index++;
            }
        }
    }

    private void drawCenteredEmoji(Graphics2D g, String emoji, int x, int y, int w, int h) {
        int fontSize = (int)(Math.min(w, h) * 0.8);
        Font prev = g.getFont();
        Color prevColor = g.getColor();
        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, fontSize);
        g.setFont(emojiFont);
        g.setColor(Color.WHITE);

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(emoji);
        int textAscent = fm.getAscent();
        int drawX = x + (w - textWidth) / 2;
        int drawY = y + (h + textAscent) / 2 - 2;
        g.drawString(emoji, drawX, drawY);

        g.setFont(prev);
        g.setColor(prevColor);
    }

    private void drawChristmasShip(Graphics2D g, int x, int y, int w, int h, int size, boolean vertical) {
        g.setColor(new Color(200, 30, 30));
        g.fillRoundRect(x + 2, y + 2, w - 4, h - 4, 8, 8);

        g.setColor(new Color(255, 215, 0));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x + 2, y + 2, w - 4, h - 4, 8, 8);

        g.setColor(Color.WHITE);
        if (vertical) {
            for (int i = 0; i < size; i++) {
                int segY = y + (h / size) * i;
                g.fillRoundRect(x + 4, segY + 4, w - 8, 6, 4, 4);
            }
        } else {
            for (int i = 0; i < size; i++) {
                int segX = x + (w / size) * i;
                g.fillRoundRect(segX + 4, y + 4, 6, h - 8, 4, 4);
            }
        }

        Color[] ornamentColors = {
                new Color(255, 215, 0),
                new Color(0, 191, 255),
                new Color(255, 105, 180),
                new Color(50, 205, 50),
                new Color(255, 140, 0)
        };

        int cellWidth = vertical ? w : (w / size);
        int cellHeight = vertical ? (h / size) : h;

        for (int i = 0; i < size; i++) {
            int ornX = vertical ? x + w / 2 : x + (cellWidth * i) + cellWidth / 2;
            int ornY = vertical ? y + (cellHeight * i) + cellHeight / 2 : y + h / 2;

            Color ornamentColor = ornamentColors[i % ornamentColors.length];

            g.setColor(ornamentColor);
            g.fillOval(ornX - 5, ornY - 5, 10, 10);

            g.setColor(new Color(255, 255, 255, 200));
            g.fillOval(ornX - 3, ornY - 3, 4, 4);

            g.setColor(new Color(180, 180, 180));
            g.fillRect(ornX - 1, ornY - 7, 2, 3);
        }
    }

    private void drawSnowflakes(Graphics2D g) {
        g.setColor(new Color(255, 255, 255, 180));
        snowRandom.setSeed(42);

        for (int i = 0; i < 100; i++) {
            int x = snowRandom.nextInt(getWidth());
            int y = snowRandom.nextInt(getHeight());
            int size = 2 + snowRandom.nextInt(4);
            g.fillOval(x, y, size, size);

            if (size > 3) {
                g.drawLine(x - 2, y + size/2, x + size + 2, y + size/2);
                g.drawLine(x + size/2, y - 2, x + size/2, y + size + 2);
            }
        }
    }

    private Point getCenteredPosition(int totalWidth, int totalHeight) {
        int panelWidth = getWidth();
        int x = (panelWidth - totalWidth) / 2;
        int y = TOP_Y;
        return new Point(x, y);
    }

    private void drawLevelWindow(Graphics g) {
        LevelWindow manager = model.getLevelWindow();
        int state = manager.getWindowState();
        if (state == 0) return;

        float alpha = manager.getAlpha();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        g2.setColor(new Color(10, 20, 60, 122));
        g2.fillRect(0, 0, panelWidth, panelHeight);

        int boxWidth = 500;
        int boxHeight = 220;
        int x = (panelWidth - boxWidth) / 2;
        int y = (panelHeight - boxHeight) / 2;

        g2.setColor(new Color(255, 250, 245));
        g2.fillRoundRect(x, y, boxWidth, boxHeight, 35, 35);

        g2.setColor(new Color(200, 0, 0));
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x, y, boxWidth, boxHeight, 35, 35);

        g2.setColor(new Color(0, 90, 0));
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 38));

        String text;
        if (state == 1) {
            text = "LEVEL " + manager.getCurrentLevel();
        } else if (state == 2) {
            text = "LEVEL " + manager.getCurrentLevel() + " COMPLETED!";
        } else {
            text = "CONGRATULATIONS!";
        }

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textX = x + (boxWidth - textWidth) / 2;
        int textY = y + boxHeight / 2 + 12;

        g2.drawString(text, textX, textY);
        g2.dispose();
    }
}