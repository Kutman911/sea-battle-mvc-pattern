package canvas;

import common.*;
import model.Model;
import viewer.Viewer;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Canvas extends JPanel {

    private final Model model;
    private int[][] arrayOfIndexes;
    private Random snowRandom;

    private final int PADDING = 50;
    private final int BOARD_SPACING = 50;
    private final int TOP_Y = 150;
    private final int BOARD_WIDTH_PX = Coordinates.WIDTH * 10;
    private int playerBoardX;
    private int computerBoardX;

    private static final Color BACKGROUND_COLOR = new Color(75, 139, 181);
    private static final Color WATER_COLOR = new Color(30, 60, 100);
    private static final Color VALID_PLACEMENT_COLOR = new Color(0, 255, 0, 100);
    private static final Color INVALID_PLACEMENT_COLOR = new Color(255, 0, 0, 100);
    private static final Color SNOWFLAKE_COLOR = new Color(255, 255, 255, 180);
    private static final Color OVERLAY_COLOR = new Color(10, 20, 60, 122);
    private static final Color LEVEL_BOX_COLOR = new Color(255, 250, 245);
    private static final Color LEVEL_BOX_BORDER_COLOR = new Color(200, 0, 0);
    private static final Color LEVEL_TEXT_COLOR = new Color(0, 90, 0);
    private static final Color PLAYER_TURN_GLOW = new Color(0, 255, 0, 100);
    private static final Color COMPUTER_TURN_GLOW = new Color(255, 0, 0, 100);
    private static final Color CANDY_BLUE = new Color(40, 140, 255);
    private static final Color CANDY_PINK = new Color(255, 40, 170);
    private static final Color CANDY_GREEN = new Color(40, 220, 120);
    private static final Color CANDY_RED = new Color(255, 60, 60);
    private static final Color CANDY_WHITE = new Color(255, 255, 255, 220);
    private static final Color CELL_EDGE = new Color(255, 255, 255, 160);
    private static final Color CELL_INNER = new Color(0, 0, 0, 35);
    private static final Color ICE_FILL_1 = new Color(170, 235, 255, 160);
    private static final Color ICE_FILL_2 = new Color(90, 180, 255, 140);
    private static final Color ICE_EDGE = new Color(240, 252, 255, 220);
    private static final Color ICE_SHADOW = new Color(0, 0, 0, 40);
    private static final Color HIT_FILL_TOP = new Color(255, 120, 120, 190);
    private static final Color HIT_FILL_BOTTOM = new Color(170, 40, 40, 210);
    private static final Color HIT_STAR = new Color(255, 255, 255, 235);
    private static final Color HIT_SPARK = new Color(255, 235, 180, 210);
    private static final Color HIT_CRACK = new Color(255, 255, 255, 170);
    private static final Color SUNK_DEEP_1 = new Color(255, 70, 70, 240);
    private static final Color SUNK_DEEP_2 = new Color(160, 20, 20, 245);
    private static final Color SUNK_RING   = new Color(255, 220, 220, 130);
    private static final Color SUNK_BUBBLE = new Color(255, 200, 200, 160);
    private static final Color SUNK_WRECK  = new Color(60, 0, 0, 140);


    // Transient UI hint overlay
    private String hintMessage = null;
    private long hintExpireAt = 0L;
    private javax.swing.Timer hintTimer;

    public Canvas(Model model) {
        this.model = model;
        setBackground(BACKGROUND_COLOR);
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

        boolean showFinalShips = model.won() &&
                model.getLevelWindow().getCurrentLevel() < 3 &&
                model.getLevelWindow().getWindowState() == LevelWindow.WindowState.HIDDEN;

        if (showFinalShips) {
            drawWon(g2);
        } else if (model.isSetupPhase()) {
            drawDesktopPlayer(g2, true);
        } else {
            drawDesktopPlayer(g2, false);
            drawDesktopComputer(g2);
        }

        drawTurnHighlight(g2);
        drawLevelWindow(g2);
        drawCount(g2);

        drawHintOverlay(g2);
    }

    private void drawCount(Graphics2D g2) {
        if (model.isSetupPhase()) return;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelW = getWidth() - 40;
        int panelH = 70;
        int panelX = 20;
        int panelY = getHeight() - panelH - 15;

        g2.setColor(OVERLAY_COLOR);
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 20, 20);

        for (int i = 0; i < 3; i++) {
            g2.setColor(new Color(255, 215, 0, 40 - i * 10));
            g2.setStroke(new BasicStroke(2 + i));
            g2.drawRoundRect(panelX - i, panelY - i, panelW + i * 2, panelH + i * 2, 20, 20);
        }

        int lightY = panelY - 5;
        Color[] lightColors = {Color.RED, Color.GREEN, Color.YELLOW, Color.CYAN, Color.PINK};
        for (int i = 0; i < panelW; i += 30) {
            g2.setColor(lightColors[(i / 30) % lightColors.length]);
            g2.fillOval(panelX + i + 10, lightY, 8, 8);
        }

        int leftX = panelX + 25;
        int topY = panelY + 28;
        int bottomY = panelY + 52;

        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(Color.WHITE);
        g2.drawString("You destroyed:", leftX, topY);
        g2.setColor(Color.GREEN);
        g2.drawString(
                model.getDestroyedComputerShips() + " / " + model.getTotalComputerShipCells(),
                leftX + 150, topY
        );

        g2.setColor(Color.WHITE);
        g2.drawString("You lost:", leftX, bottomY);
        g2.setColor(Color.RED);
        g2.drawString(
                model.getDestroyedPlayerShips() + " / " + model.getTotalPlayerShipCells(),
                leftX + 150, bottomY
        );

        int rightX = panelX + panelW - 220;
        int centerY = panelY + panelH / 2;

        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.setColor(Color.WHITE);
        g2.drawString("Current Turn:", rightX, centerY - 5);

        g2.setFont(new Font("Arial", Font.BOLD, 22));
        String turnText = model.isPlayerTurn() ? "Player" : "Computer";
        g2.setColor(model.isPlayerTurn() ? Color.GREEN : Color.RED);
        g2.drawString(turnText, rightX + 10, centerY + 20);

        g2.setColor(SNOWFLAKE_COLOR);
        Random rand = new Random(panelX + panelY);
        for (int i = 0; i < 20; i++) {
            int x = panelX + rand.nextInt(panelW);
            int y = panelY + rand.nextInt(panelH);
            g2.fillOval(x, y, 2, 2);
        }
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

    public void showHint(String message, int durationMs) {
        if (message == null || durationMs <= 0) return;
        long now = System.currentTimeMillis();
        this.hintMessage = message;
        this.hintExpireAt = now + durationMs;

        if (hintTimer == null) {
            hintTimer = new javax.swing.Timer(50, e -> {
                if (System.currentTimeMillis() >= hintExpireAt) {
                    hintTimer.stop();
                }
                repaint();
            });
        }
        if (!hintTimer.isRunning()) {
            hintTimer.start();
        }
        repaint();
    }

    private void drawHintOverlay(Graphics2D g2) {
        if (hintMessage == null) return;
        long now = System.currentTimeMillis();
        if (now >= hintExpireAt) {
            hintMessage = null;
            return;
        }

        // Style
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(hintMessage);
        int textH = fm.getAscent();

        int paddingX = 18;
        int paddingY = 12;

        // Position: bottom center above padding (raised a bit for better visibility)
        int boxW = textW + paddingX * 2;
        int boxH = textH + paddingY * 2;
        int x = (getWidth() - boxW) / 2;
        // Use a responsive bottom margin so the hint sits slightly higher and is easier to notice
        int bottomMargin = Math.max(80, getHeight() / 12);
        int y = getHeight() - boxH - bottomMargin;

        // Background
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRoundRect(x, y, boxW, boxH, 12, 12);
        g2.setColor(new Color(255, 255, 255, 220));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(x, y, boxW, boxH, 12, 12);

        // Text
        int tx = x + paddingX;
        int ty = y + paddingY + fm.getAscent() - 2;
        g2.setColor(Color.WHITE);
        g2.drawString(hintMessage, tx, ty);
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
            Point coords = getShipCoordinates(index);
            drawChristmasShip(g, x + (width * coords.x), y + (height * coords.y), width, height, 1, false);
        }

        for(int index = 4; index < 10; index += 2) {
            drawShipFromIndexRange(g, x, y, width, height, index, index + 1, 2);
        }

        drawShipFromIndexRange(g, x, y, width, height, 10, 12, 3);
        drawShipFromIndexRange(g, x, y, width, height, 13, 15, 3);
        drawShipFromIndexRange(g, x, y, width, height, 16, 19, 4);

        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(3));
        g.drawRect(x - 10, y - 10, width * 10 + 20, height * 10 + 20);
    }

    private void drawShipFromIndexRange(Graphics2D g, int baseX, int baseY, int cellWidth, int cellHeight,
                                        int startIndex, int endIndex, int size) {
        Point start = getShipCoordinates(startIndex);
        Point end = getShipCoordinates(endIndex);
        boolean horizontal = isHorizontalShip(startIndex, endIndex);

        int shipWidth = horizontal ? cellWidth * size : cellWidth;
        int shipHeight = horizontal ? cellHeight : cellHeight * size;

        drawChristmasShip(g, baseX + (cellWidth * start.x), baseY + (cellHeight * start.y),
                shipWidth, shipHeight, size, !horizontal);
    }

    private void drawDesktopComputer(Graphics2D g) {
        drawBoardLabel(g, "Computer's Board", computerBoardX, TOP_Y);
        drawGridLabels(g, computerBoardX, TOP_Y, true);

        int[][] desktopComputer = model.getComputerPlayer().getBoard();
        drawBoard(g, desktopComputer, computerBoardX, TOP_Y, true);
    }

    private void drawDesktopPlayer(Graphics2D g, boolean drawOffBoardShips) {
        String label = drawOffBoardShips ? "Your Board (Setup)" : "Your Board";
        drawBoardLabel(g, label, playerBoardX, TOP_Y);
        drawGridLabels(g, playerBoardX, TOP_Y, false);

        for (Ship ship : model.getPlayerShips()) {
            drawPlayerShip(g, ship, playerBoardX, TOP_Y);
        }

        int[][] desktopPlayer = model.getDesktopPlayer();
        drawBoard(g, desktopPlayer, playerBoardX, TOP_Y, false);

        if (drawOffBoardShips) {
            drawOffBoardShips(g);
        }
    }

    private void drawPlayerShip(Graphics2D g, Ship ship, int boardX, int boardY) {
        int width = Coordinates.WIDTH;
        int height = Coordinates.HEIGHT;
        int shipDrawX, shipDrawY, drawWidth, drawHeight;

        if (ship.isDragging()) {
            shipDrawX = ship.getX();
            shipDrawY = ship.getY();

            g.setColor(model.isValidPlacement(ship) ? VALID_PLACEMENT_COLOR : INVALID_PLACEMENT_COLOR);
            drawWidth = ship.isVertical() ? width : width * ship.getSize();
            drawHeight = ship.isVertical() ? height * ship.getSize() : height;
            g.fillRect(shipDrawX, shipDrawY, drawWidth, drawHeight);

        } else if (ship.isPlaced()) {
            shipDrawX = boardX + ship.getX() * width;
            shipDrawY = boardY + ship.getY() * height;
            drawWidth = ship.isVertical() ? width : width * ship.getSize();
            drawHeight = ship.isVertical() ? height * ship.getSize() : height;
            drawChristmasShip(g, shipDrawX, shipDrawY, drawWidth, drawHeight, ship.getSize(), ship.isVertical());
        }
    }

    private void drawOffBoardShips(Graphics2D g) {
        int offBoardX = computerBoardX;
        int offBoardTopY = TOP_Y;
        int width = Coordinates.WIDTH;
        int height = Coordinates.HEIGHT;

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

    private void drawBoard(Graphics2D g, int[][] board, int boardX, int boardY, boolean isComputer) {
        int width = Coordinates.WIDTH;
        int height = Coordinates.HEIGHT;
        int currentX = boardX;
        int currentY = boardY;

        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                int element = board[i][j];
                drawBoardCell(g, element, currentX, currentY, width, height, isComputer);
                currentX += width;
            }
            currentX = boardX;
            currentY += height;
        }
    }

    private void drawBoardCell(Graphics2D g, int element, int x, int y, int width, int height, boolean isComputer) {
        if (element == -1) {
            g.setColor(WATER_COLOR);
            g.fillRect(x, y, width, height);

            drawIcyMiss(g, x, y, width, height, isComputer );
        } else if (element == -9) {
            drawHitCell(g, x, y, width, height);

        } else if (element == -8) {
            drawSunkCell(g, x, y, width, height);
        }
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1));
        g.drawRect(x, y, width, height);
    }

    private void drawBoardLabel(Graphics2D g, String label, int boardX, int boardY) {
        g.setFont(new Font("Bernard MT Condensed", Font.BOLD, 30));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        int labelWidth = fm.stringWidth(label);
        int labelX = boardX + (Coordinates.WIDTH * 10 - labelWidth) / 2;
        g.drawString(label, labelX, boardY - 70);
    }

    private void drawGridLabels(Graphics2D g, int boardX, int boardY, boolean isRightSide) {
        int width = Coordinates.WIDTH;
        int height = Coordinates.HEIGHT;

        g.setFont(new Font("Bernard MT Condensed", Font.BOLD, 35));
        g.setColor(Color.WHITE);

        final String row = "12345678910";
        final String column = "ABCDEFGHIJ";

        int rowX = isRightSide ? boardX + 510 : boardX - 35;
        int rowY = boardY + 40;
        for (int i = 0; i < row.length(); i++) {
            if (i == row.length() - 2) {
                String text = ("" + row.charAt(i)) + row.charAt(i + 1);
                g.drawString(text, rowX, rowY);
                break;
            }
            g.drawString(String.valueOf(row.charAt(i)), rowX, rowY);
            rowY += height;
        }

        int colX = boardX + 15;
        int colY = boardY - 10;
        for (int i = 0; i < column.length(); i++) {
            g.drawString(String.valueOf(column.charAt(i)), colX, colY);
            colX += width;
        }
    }

    private Color getCandyBaseBySize(int size) {
        return switch (size) {
            case 1 -> CANDY_BLUE;
            case 2 -> CANDY_PINK;
            case 3 -> CANDY_GREEN;
            default -> CANDY_RED;
        };
    }

    private Color brighten(Color c, float amount) {
        return new Color(
                clamp((int)(c.getRed()   + 255 * amount)),
                clamp((int)(c.getGreen() + 255 * amount)),
                clamp((int)(c.getBlue()  + 255 * amount)),
                c.getAlpha()
        );
    }
    private Color darken(Color c, float amount) {
        return new Color(
                clamp((int)(c.getRed()   * (1f - amount))),
                clamp((int)(c.getGreen() * (1f - amount))),
                clamp((int)(c.getBlue()  * (1f - amount))),
                c.getAlpha()
        );
    }
    private int clamp(int v) { return Math.max(0, Math.min(255, v)); }

    private void drawChristmasShip(Graphics2D g, int x, int y, int w, int h, int size, boolean vertical) {
        Color base = getCandyBaseBySize(size);

        int segW = vertical ? w : w / size;
        int segH = vertical ? h / size : h;
        int arc = 14;

        g.setColor(new Color(0, 0, 0, 40));
        g.fillRoundRect(x + 3, y + 4, w, h, arc, arc);

        for (int i = 0; i < size; i++) {
            int sx = vertical ? x : x + i * segW;
            int sy = vertical ? y + i * segH : y;

            float tone = (i % 2 == 0) ? 0.10f : 0.02f;
            Color top = brighten(base, 0.18f + tone);
            Color bottom = darken(base, 0.10f);

            Paint prev = g.getPaint();
            g.setPaint(new GradientPaint(sx, sy, top, sx, sy + segH, bottom));
            g.fillRoundRect(sx, sy, segW, segH, arc, arc);
            g.setPaint(prev);

            drawCandyStripes(g, sx, sy, segW, segH, vertical);

            g.setColor(CELL_INNER);
            g.drawRoundRect(sx + 2, sy + 2, segW - 4, segH - 4, arc, arc);

            g.setStroke(new BasicStroke(2f));
            g.setColor(CELL_EDGE);
            g.drawRoundRect(sx, sy, segW, segH, arc, arc);

            g.setColor(new Color(255, 255, 255, 85));
            g.fillRoundRect(sx + 4, sy + 4, (int)(segW * 0.55), (int)(segH * 0.35), 12, 12);
        }

        g.setStroke(new BasicStroke(3f));
        g.setColor(new Color(255, 255, 255, 140));
        g.drawRoundRect(x, y, w, h, arc, arc);
    }

    private void drawCandyStripes(Graphics2D g, int x, int y, int w, int h, boolean vertical) {
        Shape oldClip = g.getClip();
        g.setClip(new java.awt.geom.RoundRectangle2D.Float(x, y, w, h, 14, 14));

        g.setColor(CANDY_WHITE);

        int stripe = Math.max(10, Math.min(w, h) / 3);
        int gap = stripe + 10;

        for (int i = -h; i < w + h; i += gap) {
            Graphics2D gg = (Graphics2D) g.create();
            gg.translate(x, y);
            gg.rotate(Math.toRadians(vertical ? 25 : -25), w / 2.0, h / 2.0);
            gg.fillRoundRect(i, -10, stripe, h + 20, 10, 10);
            gg.dispose();
        }

        g.setClip(oldClip);
    }

    private void drawIcyMiss(Graphics2D g, int x, int y, int w, int h, boolean isComputer) {
        g.setColor(ICE_SHADOW);
        g.fillRoundRect(x + 2, y + 3, w - 3, h - 3, 10, 10);

        Paint prev = g.getPaint();
        g.setPaint(new GradientPaint(x, y, ICE_FILL_1, x, y + h, ICE_FILL_2));
        g.fillRoundRect(x + 1, y + 1, w - 2, h - 2, 10, 10);
        g.setPaint(prev);

        g.setStroke(new BasicStroke(2f));
        g.setColor(ICE_EDGE);
        g.drawRoundRect(x + 2, y + 2, w - 4, h - 4, 10, 10);

        int seed = x * 31 + y * 17;
        for (int i = 0; i < 10; i++) {
            int px = x + 3 + ((seed + i * 37) & 1023) % (w - 6);
            int py = y + 3 + ((seed + i * 91) & 1023) % (h - 6);
            int s  = 1 + (((seed + i * 13) & 7) == 0 ? 2 : 1);
            g.setColor(new Color(255, 255, 255, 120));
            g.fillOval(px, py, s, s);
        }

        drawFlake(g, x, y, w, h);

        g.setColor(new Color(255, 255, 255, 70));
        g.fillRoundRect(x + 4, y + 4, (int)(w * 0.55), (int)(h * 0.35), 8, 8);

        g.setColor(new Color(255, 255, 255, 120));
        g.setStroke(new BasicStroke(1f));
        g.drawRect(x, y, w, h);
    }

    private void drawFlake(Graphics2D g, int x, int y, int w, int h) {
        int cx = x + w / 2;
        int cy = y + h / 2;

        int r = Math.max(6, Math.min(w, h) / 4);

        g.setColor(new Color(255, 255, 255, 210));
        g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g.drawLine(cx - r, cy, cx + r, cy);
        g.drawLine(cx, cy - r, cx, cy + r);
        g.drawLine(cx - r / 2, cy - r / 2, cx + r / 2, cy + r / 2);
        g.drawLine(cx - r / 2, cy + r / 2, cx + r / 2, cy - r / 2);

        int br = r / 2;

        g.drawLine(cx + br, cy, cx + br + 4, cy - 4);
        g.drawLine(cx + br, cy, cx + br + 4, cy + 4);
        g.drawLine(cx - br, cy, cx - br - 4, cy - 4);
        g.drawLine(cx - br, cy, cx - br - 4, cy + 4);
        g.drawLine(cx, cy - br, cx - 4, cy - br - 4);
        g.drawLine(cx, cy - br, cx + 4, cy - br - 4);
        g.drawLine(cx, cy + br, cx - 4, cy + br + 4);
        g.drawLine(cx, cy + br, cx + 4, cy + br + 4);
    }

    private void drawSnowflakes(Graphics2D g) {
        g.setColor(SNOWFLAKE_COLOR);
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
    private void drawHitCell(Graphics2D g, int x, int y, int w, int h) {
        Paint prev = g.getPaint();
        g.setPaint(new GradientPaint(x, y, HIT_FILL_TOP, x, y + h, HIT_FILL_BOTTOM));
        g.fillRoundRect(x + 1, y + 1, w - 2, h - 2, 10, 10);
        g.setPaint(prev);

        int cx = x + w / 2;
        int cy = y + h / 2;
        drawStar(g, cx, cy, Math.max(6, Math.min(w, h) / 5), Math.max(10, Math.min(w, h) / 3), 8, HIT_STAR);

        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(HIT_SPARK);
        g.drawLine(cx - 10, cy + 2, cx - 16, cy + 8);
        g.drawLine(cx + 10, cy - 2, cx + 16, cy - 8);
        g.drawLine(cx - 2, cy - 10, cx - 8, cy - 16);
        g.drawLine(cx + 2, cy + 10, cx + 8, cy + 16);

        g.setColor(HIT_CRACK);
        g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(cx - 3, cy, x + 4, y + 6);
        g.drawLine(cx + 3, cy, x + w - 5, y + h - 7);
        g.drawLine(cx, cy - 3, x + w - 6, y + 6);

        g.setColor(new Color(255, 255, 255, 140));
        g.setStroke(new BasicStroke(1f));
        g.drawRect(x, y, w, h);
    }

    private void drawSunkCell(Graphics2D g, int x, int y, int w, int h) {
        int cx = x + w / 2;
        int cy = y + h / 2;

        for (int i = 0; i < 10; i++) {
            float t = i / 9f;                  // 0..1
            int r = (int)((Math.min(w, h) * 0.55) * (1f - t));
            int a = (int)(220 * (1f - t) * 0.20f) + 20; // мягко
            Color c = lerpColor(SUNK_DEEP_2, SUNK_DEEP_1, t, a);
            g.setColor(c);
            g.fillOval(cx - r, cy - r, r * 2, r * 2);
        }

        g.setColor(SUNK_RING);
        g.setStroke(new BasicStroke(2f));
        g.drawOval(x + 4, y + 4, w - 8, h - 8);

        g.setColor(SUNK_WRECK);
        int r  = Math.min(w, h) / 4;
        g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(cx - r, cy - r, cx + r, cy + r);
        g.drawLine(cx + r, cy - r, cx - r, cy + r);

        int seed = x * 57 + y * 101;
        for (int i = 0; i < 9; i++) {
            int bx = x + 4 + ((seed + i * 37) & 1023) % (w - 8);
            int by = y + 4 + ((seed + i * 91) & 1023) % (h - 8);
            int br = 2 + ((seed + i * 17) & 3);

            g.setColor(new Color(SUNK_BUBBLE.getRed(), SUNK_BUBBLE.getGreen(), SUNK_BUBBLE.getBlue(), 110));
            g.drawOval(bx - br, by - br, br * 2, br * 2);

            g.setColor(new Color(255, 255, 255, 90));
            g.fillOval(bx - 1, by - 1, 2, 2);
        }

        g.setColor(new Color(255, 255, 255, 80));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(x + 2, y + 2, w - 4, h - 4, 10, 10);
    }

    private Color lerpColor(Color a, Color b, float t, int alpha) {
        int r = (int)(a.getRed()   + (b.getRed()   - a.getRed())   * t);
        int g = (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = (int)(a.getBlue() + (b.getBlue()  - a.getBlue())  * t);
        return new Color(clamp(r), clamp(g), clamp(bl), clamp(alpha));
    }

    private void drawStar(Graphics2D g, int cx, int cy, int innerR, int outerR, int points, Color color) {
        double angle = Math.PI / points;
        Polygon p = new Polygon();
        for (int i = 0; i < points * 2; i++) {
            int r = (i % 2 == 0) ? outerR : innerR;
            double a = i * angle - Math.PI / 2;
            p.addPoint(cx + (int)(Math.cos(a) * r), cy + (int)(Math.sin(a) * r));
        }
        Color prev = g.getColor();
        g.setColor(color);
        g.fillPolygon(p);
        g.setColor(prev);
    }

    private Point getCenteredPosition(int totalWidth, int totalHeight) {
        int panelWidth = getWidth();
        int x = (panelWidth - totalWidth) / 2;
        int y = TOP_Y;
        return new Point(x, y);
    }

    private void drawLevelWindow(Graphics g) {
        LevelWindow manager = model.getLevelWindow();
        LevelWindow.WindowState state = manager.getWindowState();

        if (state == LevelWindow.WindowState.HIDDEN) {
            return;
        }

        float alpha = manager.getAlpha();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        g2.setColor(OVERLAY_COLOR);
        g2.fillRect(0, 0, panelWidth, panelHeight);

        int boxWidth = 500;
        int boxHeight = 220;
        int x = (panelWidth - boxWidth) / 2;
        int y = (panelHeight - boxHeight) / 2;

        g2.setColor(LEVEL_BOX_COLOR);
        g2.fillRoundRect(x, y, boxWidth, boxHeight, 35, 35);

        g2.setColor(LEVEL_BOX_BORDER_COLOR);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x, y, boxWidth, boxHeight, 35, 35);

        g2.setColor(LEVEL_TEXT_COLOR);
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 38));

        String text;
        if (state == LevelWindow.WindowState.LEVEL_START) {
            text = "LEVEL " + manager.getCurrentLevel();
        } else if (state == LevelWindow.WindowState.LEVEL_COMPLETED) {
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

    private void drawTurnHighlight(Graphics2D g) {
        if (model.isSetupPhase()) return;

        int width = Coordinates.WIDTH * 10;
        int height = Coordinates.HEIGHT * 10;

        int x = model.isPlayerTurn() ? computerBoardX : playerBoardX;
        int y = TOP_Y;
        Color glowColor = model.isPlayerTurn() ? PLAYER_TURN_GLOW : COMPUTER_TURN_GLOW;

        for (int i = 0; i < 12; i++) {
            float alpha = (12 - i) / 12f * 0.10f;
            int glowSize = 6 + i;

            g.setColor(new Color(
                    glowColor.getRed(),
                    glowColor.getGreen(),
                    glowColor.getBlue(),
                    (int)(glowColor.getAlpha() * alpha)
            ));

            g.setStroke(new BasicStroke(glowSize));
            g.drawRect(x - glowSize, y - glowSize, width + glowSize * 2, height + glowSize * 2);
        }

        g.setColor(glowColor);
        g.setStroke(new BasicStroke(6));
        g.drawRect(x - 3, y - 3, width + 6, height + 6);
    }

    private Point getShipCoordinates(int index) {
        return new Point(arrayOfIndexes[1][index], arrayOfIndexes[0][index]);
    }

    private boolean isHorizontalShip(int index1, int index2) {
        return arrayOfIndexes[0][index1] == arrayOfIndexes[0][index2];
    }

    public void scheduleComputerTurn(Viewer viewer) {
        int delay = 1000;

        Timer timer = new Timer(delay, e -> {
            model.computerTurn();
            viewer.update();
            ((Timer) e.getSource()).stop();
        });

        timer.setRepeats(false);
        timer.start();
    }

    public void attemptStart(Viewer viewer) {
        if (!model.areAllShipsPlaced()) {
            JOptionPane.showMessageDialog(
                    viewer.getFrame(),
                    "Please place all ships first",
                    "Reminder",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (!model.isBattleStarted()) {
            model.startBattlePhase();
            // During the battle the START button should be hidden and disabled
            viewer.getStartButton().setEnabled(false);
            viewer.getStartButton().setVisible(false);
            if (viewer.getRandomButton() != null) {
                viewer.getRandomButton().setEnabled(false);
                viewer.getRandomButton().setVisible(false);
            }
        }
        viewer.update();
    }

    public void showMainMenu(Viewer viewer) {
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu(
                    viewer.getFrame(),
                    () -> {
                        setVisibleFrame(viewer.getFrame());
                    },
                    () -> JOptionPane.showMessageDialog(viewer.getFrame(), "Settings coming soon"),
                    () -> JOptionPane.showMessageDialog(viewer.getFrame(), "Rules coming soon"),
                    () -> {
                        System.exit(0);
                    }
            );
            menu.setVisible(true);
        });
    }

    public void setVisibleFrame(JFrame jFrame) {
        jFrame.setVisible(true);
        SwingUtilities.invokeLater(() -> model.getLevelWindow().showLevelStartWindow());
    }

    public void showResult(boolean isWin, Viewer viewer) {
        if (viewer.getAudioPlayer() != null) {
            viewer.getAudioPlayer().stop();
        }
        if (!isWin) {
            viewer.getAudioPlayer().playSound("/resources/sounds/loseSound.wav");
        }

        SwingUtilities.invokeLater(() -> {
            ResultDialog dialog = new ResultDialog(
                    viewer.getFrame(),
                    isWin,
                    model.getLevelWindow().getCurrentLevel(),
                    () -> {
                        if (isWin) {
                            if (model.getLevelWindow().getCurrentLevel() >= 3) {
                                model.getLevelWindow().resetToLevelOne();
                            }
                        } else {
                            model.getLevelWindow().resetToLevelOne();
                        }
                        model.resetGame();
                        if (viewer.getStartButton() != null) {
                            viewer.getStartButton().setEnabled(true);
                            // Show the START button again when the battle is over
                            viewer.getStartButton().setVisible(true);
                        }
                        if (viewer.getRandomButton() != null) {
                            viewer.getRandomButton().setEnabled(true);
                            viewer.getRandomButton().setVisible(true);
                        }
                        SwingUtilities.invokeLater(() -> model.getLevelWindow().showLevelStartWindow());
                        if (viewer.getAudioPlayer() != null) {
                            viewer.getAudioPlayer().playBackgroundMusic("/resources/sounds/background_music.wav");
                        }
                    },
                    () -> {
                        model.getLevelWindow().nextLevel();
                        if (viewer.getStartButton() != null) {
                            viewer.getStartButton().setEnabled(true);
                            // Also make it visible for the next level
                            viewer.getStartButton().setVisible(true);
                        }
                        if (viewer.getRandomButton() != null) {
                            viewer.getRandomButton().setEnabled(true);
                            viewer.getRandomButton().setVisible(true);
                        }
                        if (viewer.getAudioPlayer() != null) {
                            viewer.getAudioPlayer().playBackgroundMusic("/resources/sounds/background_music.wav");
                        }
                    },
                    () -> System.exit(0)
            );
            dialog.setVisible(true);
        });
    }

    public Model getModel() {
        return model;
    }
}