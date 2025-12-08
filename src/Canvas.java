import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Canvas extends JPanel {

    private Model model;
    private int[][] arrayOfIndexes;
    private Point computerBoardPosition;
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
            int i = array[0][index];
            int j = array[1][index];
            arrayOfIndexes[0][index] = i;
            arrayOfIndexes[1][index] = j;
        }
    }

    private void calculateBoardPositions() {
        int panelWidth = getWidth();

        if (model.isSetupPhase()) {


            int offBoardShipAreaWidth = (int)(BOARD_WIDTH_PX * 0.9);
            int totalSetupWidth = BOARD_WIDTH_PX + BOARD_SPACING + offBoardShipAreaWidth;

            int startX = (panelWidth - totalSetupWidth) / 2;

            playerBoardX = startX;
            computerBoardX = startX + BOARD_WIDTH_PX + BOARD_SPACING; // reuse as "off‑board" left X

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
        int maxSetupWidth = BOARD_WIDTH_PX + PADDING +
                Coordinates.WIDTH + PADDING + PADDING;

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
            int i = arrayOfIndexes[0][index];
            int j = arrayOfIndexes[1][index];
            drawChristmasShip(g, x + (width * j), y + (height * i), width, height, 1, false);
        }

        for(int index = 4; index < 10; index = index + 2) {
            int i1 = arrayOfIndexes[0][index];
            int j1 = arrayOfIndexes[1][index];
            int i2 = arrayOfIndexes[0][index + 1];
            int j2 = arrayOfIndexes[1][index + 1];
            boolean horizontal = (i1 == i2);
            drawChristmasShip(g, x + (width * j1), y + (height * i1),
                    horizontal ? width * 2 : width,
                    horizontal ? height : height * 2,
                    2, !horizontal);
        }

        int i1 = arrayOfIndexes[0][10];
        int j1 = arrayOfIndexes[1][10];
        int i3 = arrayOfIndexes[0][12];
        int j3 = arrayOfIndexes[1][12];
        boolean horizontal = (i1 == i3);
        drawChristmasShip(g, x + (width * j1), y + (height * i1),
                horizontal ? width * 3 : width,
                horizontal ? height : height * 3,
                3, !horizontal);

        i1 = arrayOfIndexes[0][13];
        j1 = arrayOfIndexes[1][13];
        i3 = arrayOfIndexes[0][15];
        j3 = arrayOfIndexes[1][15];
        horizontal = (i1 == i3);
        drawChristmasShip(g, x + (width * j1), y + (height * i1),
                horizontal ? width * 3 : width,
                horizontal ? height : height * 3,
                3, !horizontal);

        i1 = arrayOfIndexes[0][16];
        j1 = arrayOfIndexes[1][16];
        i3 = arrayOfIndexes[0][19];
        j3 = arrayOfIndexes[1][19];
        horizontal = (i1 == i3);
        drawChristmasShip(g, x + (width * j1), y + (height * i1),
                horizontal ? width * 4 : width,
                horizontal ? height : height * 4,
                4, !horizontal);

        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(3));
        g.drawRect(x - 10, y - 10, width * 10 + 20, height * 10 + 20);
    }

    private void drawDesktopComputer(Graphics2D g) {
        drawGridLabels(g, "Desktop");
        int[][] desktopComputer = model.getComputerPlayer().getBoard();

        int width = Coordinates.WIDTH;
        int height = Coordinates.HEIGHT;

        int x = computerBoardX;
        int y = TOP_Y;

        computerBoardPosition = new Point(x, y);

        g.setFont(new Font("Bernard MT Condensed", Font.BOLD, 30));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        String label = "Computer's Board";
        int labelWidth = fm.stringWidth(label);
        int labelX = x + (width * 10 - labelWidth) / 2;
        g.drawString(label, labelX, y - 70);

        int currentX = x;
        int currentY = y;

        for(int i = 0; i < desktopComputer.length; i++) {
            for(int j = 0; j < desktopComputer[i].length; j++) {
                int element = desktopComputer[i][j];

                if(element == -1) {
                    g.setColor(new Color(30, 60, 100));
                    g.fillRect(currentX, currentY, width, height);
                    g.setColor(Color.WHITE);
                    int cx = currentX + width / 2;
                    int cy = currentY + height / 2;
                    g.fillOval(cx - 6, cy - 6, 12, 12);
                } else if(element == -9){
                    // Hit — show explosion emoji 💥
                    g.setColor(new Color(200, 50, 50));
                    g.fillRect(currentX, currentY, width, height);
                    drawCenteredEmoji(g, "💥", currentX, currentY, width, height);
                } else if (element == -8) {
                    // Sunk — darker red with skull ☠
                    g.setColor(new Color(160, 30, 30));
                    g.fillRect(currentX, currentY, width, height);
                    g.setColor(new Color(0, 200, 0));
                    g.setStroke(new BasicStroke(3));
                    g.drawRect(currentX, currentY, width, height);
                    drawCenteredEmoji(g, "☠", currentX, currentY, width, height);
                }

                g.setColor(Color.WHITE);
                g.setStroke(new BasicStroke(1));
                g.drawRect(currentX, currentY, width, height);
                currentX = currentX + width;
            }
            currentX = x;

            currentY = currentY + height;
        }
    }

    private void drawGridLabels(Graphics2D g, String state) {
        int width = Coordinates.WIDTH;
        int height = Coordinates.HEIGHT;

        int x;
        int y;

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
                String text = ("" + row.charAt(i)) + row.charAt(i + 1);
                g.drawString(text, x, y);
                break;
            }
            g.drawString(String.valueOf(row.charAt(i)), x, y);
            y = y + height;
        }

        x = state.equals("Player") ? playerBoardX + 15 : computerBoardX + 15;
        y = TOP_Y - 10;

        for (int i = 0; i < column.length(); i++) {
            g.drawString(String.valueOf(column.charAt(i)), x, y);
            x = x + width;
        }
    }

    private void drawDesktopPlayer(Graphics2D g, boolean drawOffBoardShips) {
        drawGridLabels(g, "Player");
        int width = Coordinates.WIDTH;
        int height = Coordinates.HEIGHT;

        int boardX = playerBoardX;
        int boardY = TOP_Y;

        int[][] desktopPlayer = model.getDesktopPlayer();

        g.setFont(new Font("Bernard MT Condensed", Font.BOLD, 30));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        String label = drawOffBoardShips ? "Your Board (Setup)" : "Your Board";
        int labelWidth = fm.stringWidth(label);
        int labelX = boardX + (width * 10 - labelWidth) / 2;
        g.drawString(label, labelX, boardY - 70);

        for (Ship ship : model.getPlayerShips()) {
            int shipDrawX, shipDrawY;
            int drawWidth, drawHeight;

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
            } else if (drawOffBoardShips) {
                continue; // Отложим отрисовку боковых кораблей
            } else {
                continue;
            }

            drawChristmasShip(g, shipDrawX, shipDrawY, drawWidth, drawHeight, ship.getSize(), ship.isVertical());
        }

        int x = boardX;
        int y = boardY;

        for(int i = 0; i < desktopPlayer.length; i++) {
            for(int j = 0; j < desktopPlayer[i].length; j++) {
                int element = desktopPlayer[i][j];

                if(element == -1) {
                    g.setColor(new Color(30, 60, 100));
                    g.fillRect(x, y, width, height);
                    g.setColor(Color.WHITE);
                    int cx = x + width / 2;
                    int cy = y + height / 2;
                    g.fillOval(cx - 6, cy - 6, 12, 12);
                } else if(element == -9){
                    // Попадание — 💥 поверх клетки/корабля
                    drawCenteredEmoji(g, "💥", x, y, width, height);
                } else if (element == -8) {
                    // Потоплен — ☠ и жирная рамка
                    g.setColor(new Color(0, 200, 0));
                    g.setStroke(new BasicStroke(3));
                    g.drawRect(x, y, width, height);
                    drawCenteredEmoji(g, "☠", x, y, width, height);
                }

                g.setColor(Color.WHITE);
                g.setStroke(new BasicStroke(1));
                g.drawRect(x, y, width, height);
                x = x + width;
            }
            x = boardX;
            y = y + height;
        }

        if (drawOffBoardShips) {
            // Off‑board area: two columns next to the player board for easier drag & drop
            int offBoardX = computerBoardX; // left edge of off‑board area
            int offBoardTopY = TOP_Y;       // align with board top

            // Instruction label above the off‑board ships
            g.setFont(new Font("Arial", Font.BOLD, 22));
            g.setColor(Color.WHITE);
            FontMetrics fm2 = g.getFontMetrics();
            String instructionLabel = "Drag ships (double-click to rotate)";
            int colSpacing = 30;
            int colWidth = Coordinates.WIDTH * 5;
            int totalOffBoardWidth = colWidth * 2 + colSpacing;
            int instructionX = offBoardX + (totalOffBoardWidth - fm2.stringWidth(instructionLabel)) / 2;
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
    }

    private void drawCenteredEmoji(Graphics2D g, String emoji, int x, int y, int w, int h) {
        // Try an emoji-capable font; fallback will be automatic if missing
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
            int ornX, ornY;
            if (vertical) {
                ornX = x + w / 2;
                ornY = y + (cellHeight * i) + cellHeight / 2;
            } else {
                ornX = x + (cellWidth * i) + cellWidth / 2;
                ornY = y + h / 2;
            }

            Color ornamentColor = ornamentColors[i % ornamentColors.length];

            // Основной шар
            g.setColor(ornamentColor);
            g.fillOval(ornX - 5, ornY - 5, 10, 10);

            // Блик
            g.setColor(new Color(255, 255, 255, 200));
            g.fillOval(ornX - 3, ornY - 3, 4, 4);

            // Крючок
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