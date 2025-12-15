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
    private static final Color HIT_BG_COLOR = new Color(200, 50, 50);
    private static final Color SUNK_BG_COLOR = new Color(160, 30, 30);
    private static final Color SUNK_BORDER_COLOR = new Color(0, 200, 0);
    private static final Color SHIP_COLOR = new Color(0, 150, 70);
    private static final Color SHIP_BORDER_COLOR = new Color(0, 100, 0);
    private static final Color VALID_PLACEMENT_COLOR = new Color(0, 255, 0, 100);
    private static final Color INVALID_PLACEMENT_COLOR = new Color(255, 0, 0, 100);
    private static final Color SNOWFLAKE_COLOR = new Color(255, 255, 255, 180);
    private static final Color OVERLAY_COLOR = new Color(10, 20, 60, 122);
    private static final Color LEVEL_BOX_COLOR = new Color(255, 250, 245);
    private static final Color LEVEL_BOX_BORDER_COLOR = new Color(200, 0, 0);
    private static final Color LEVEL_TEXT_COLOR = new Color(0, 90, 0);
    private static final Color PLAYER_TURN_GLOW = new Color(0, 255, 0, 100);
    private static final Color COMPUTER_TURN_GLOW = new Color(255, 0, 0, 100);
    private static final Color ORNAMENT_ORANGE = new Color(255, 150, 0);
    private static final Color ORNAMENT_BLUE = new Color(0, 19, 255);
    private static final Color ORNAMENT_PINK = new Color(255, 10, 180);
    private static final Color ORNAMENT_RED = new Color(255, 2, 2);
    private static final Color ORNAMENT_WHITE = new Color(255, 255, 255);
    private static final Color ORNAMENT_HIGHLIGHT = new Color(255, 255, 255, 200);
    private static final Color ORNAMENT_HOOK = new Color(180, 180, 180);

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
                model.getLevelWindow().getWindowState() == 0;

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
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.setColor(Color.WHITE);

        int panelX = 10;
        int panelY = 10;
        int panelW = 180;
        int panelH = 130;

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 20, 20);

        g2.setColor(Color.WHITE);

        g2.drawString("Statistics", panelX + 10, panelY + 30);

        g2.drawString("Computer:", panelX + 10, panelY + 60);
        g2.drawString(
                model.getDestroyedComputerShips() + " / " + model.getTotalComputerShipCells(),
                panelX + 110, panelY + 60);

        g2.drawString("Player:", panelX + 10, panelY + 90);
        g2.drawString(
                model.getDestroyedPlayerShips() + " / " + model.getTotalPlayerShipCells(),
                panelX + 110, panelY + 90);

        g2.setColor(Color.YELLOW);
        g2.drawString("Move: " + (model.isPlayerTurn() ? "Player" : "Computer"),
                panelX + 10, panelY + 120);

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
        if(element == -1) {
            g.setColor(WATER_COLOR);
            g.fillRect(x, y, width, height);
            g.setColor(Color.WHITE);
            int cx = x + width / 2;
            int cy = y + height / 2;
            g.fillOval(cx - 6, cy - 6, 12, 12);
        } else if(element == -9) {
            if (isComputer) {
                g.setColor(HIT_BG_COLOR);
                g.fillRect(x, y, width, height);
            }
            drawCenteredEmoji(g, "💥", x, y, width, height);
        } else if (element == -8) {
            if (isComputer) {
                g.setColor(SUNK_BG_COLOR);
                g.fillRect(x, y, width, height);
            }
            g.setColor(SUNK_BORDER_COLOR);
            g.setStroke(new BasicStroke(3));
            g.drawRect(x, y, width, height);
            drawCenteredEmoji(g, "☠", x, y, width, height);
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
        g.setColor(SHIP_COLOR);
        g.fillRoundRect(x + 2, y + 2, w - 4, h - 4, 8, 8);

        g.setColor(SHIP_BORDER_COLOR);
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

        drawOrnaments(g, x, y, w, h, size, vertical);
    }

    private void drawOrnaments(Graphics2D g, int x, int y, int w, int h, int size, boolean vertical) {
        Color[] ornamentColors = {
                ORNAMENT_ORANGE,
                ORNAMENT_BLUE,
                ORNAMENT_PINK,
                ORNAMENT_RED,
                ORNAMENT_WHITE
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
            g.setColor(ornamentColor);
            g.fillOval(ornX - 5, ornY - 5, 10, 10);

            g.setColor(ORNAMENT_HIGHLIGHT);
            g.fillOval(ornX - 3, ornY - 3, 4, 4);

            g.setColor(ORNAMENT_HOOK);
            g.fillRect(ornX - 1, ornY - 7, 2, 3);
        }
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
        int delay = 600;

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
                    () -> System.exit(0)
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
            viewer.getAudioPlayer().playSound("src/sounds/loseSound.wav");
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
                            viewer.getAudioPlayer().playBackgroundMusic("src/sounds/background_music.wav");
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
                            viewer.getAudioPlayer().playBackgroundMusic("src/sounds/background_music.wav");
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