import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Canvas extends JPanel {

    private Model model;
    private int[][] arrayOfIndexes;
    private Point computerBoardPosition;
    private float levelAlpha = 0f;
    private Random snowRandom = new Random(42);

    public Canvas(Model model) {
        this.model = model;
        setBackground(new Color(75, 139, 181));

        int[][] array = model.getArrayOfIndexes();
        arrayOfIndexes = new int[2][array[0].length];

        for(int index = 0; index < array[0].length; index++) {
            int i = array[0][index];
            int j = array[1][index];
            arrayOfIndexes[0][index] = i;
            arrayOfIndexes[1][index] = j;
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(model.won()) {
            drawWon(g2);
        } else {
            drawDesktopComputer(g2);
        }

        drawDesktopPlayer(g2);
        drawSnowflakes(g2);
        drawLevelWindow(g2);
    }

    public Point getComputerBoardPosition() {
        return computerBoardPosition;
    }

    private void drawWon(Graphics2D g) {
        int width = Coordinates.WIDTH;
        int height = Coordinates.HEIGHT;

        int totalWidth = width * 10;
        int totalHeight = height * 10;
        Point centerPos = getCenteredPosition(totalWidth, totalHeight);
        int x = centerPos.x;
        int y = centerPos.y;

        g.setFont(new Font("Bernard MT Condensed", Font.BOLD, 50));
        g.setColor(Color.GREEN);
        g.drawString("You won!", x + (width * 2), y - 40);

        // Рисуем корабли размера 1
        for(int index = 0; index < 4; index++) {
            int i = arrayOfIndexes[0][index];
            int j = arrayOfIndexes[1][index];
            drawChristmasShip(g, x + (width * j), y + (height * i), width, height, 1, false);
        }

        // Рисуем корабли размера 2
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

        // Рисуем корабли размера 3
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

        // Рисуем корабль размера 4
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
        int[][] desktopComputer = model.getDesktopComputer();

        int width = Coordinates.WIDTH;
        int height = Coordinates.HEIGHT;
        int totalWidth = width * 10 + 100 + width * 10;
        int totalHeight = height * 10;
        Point centerPos = getCenteredPosition(totalWidth, totalHeight);

        int x = centerPos.x;
        int y = centerPos.y;

        computerBoardPosition = new Point(x, y);

        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        String label = "Computer's Board";
        int labelWidth = fm.stringWidth(label);
        int labelX = x + (width * 10 - labelWidth) / 2;
        g.drawString(label, labelX, y - 20);

        for(int i = 0; i < desktopComputer.length; i++) {
            for(int j = 0; j < desktopComputer[i].length; j++) {
                int element = desktopComputer[i][j];

                if(element == -1) {
                    g.setColor(new Color(30, 60, 100));
                    g.fillRect(x, y, width, height);
                    g.setColor(Color.WHITE);
                    g.drawLine(x, y, x + width, y + height);
                    g.drawLine(x + width, y, x, y + height);
                } else if(element == -9){
                    g.setColor(new Color(200, 50, 50));
                    g.fillRect(x, y, width, height);
                    g.setColor(Color.YELLOW);
                    int cx = x + width / 2;
                    int cy = y + height / 2;
                    g.fillOval(cx - 8, cy - 8, 16, 16);
                    g.setColor(Color.RED);
                    g.drawLine(x, y, x + width, y + height);
                    g.drawLine(x + width, y, x, y + height);
                }

                g.setColor(Color.WHITE);
                g.drawRect(x, y, width, height);
                x = x + width;
            }
            x = centerPos.x;
            y = y + height;
        }
    }

    private void drawDesktopPlayer(Graphics2D g) {
        int width = Coordinates.WIDTH;
        int height = Coordinates.HEIGHT;

        int totalWidth = width * 10 + 100 + width * 10;
        int totalHeight = height * 10;
        Point centerPos = getCenteredPosition(totalWidth, totalHeight);

        int boardX = centerPos.x + (width * 10) + 100;
        int boardY = centerPos.y;

        int[][] desktopPlayer = model.getDesktopPlayer();

        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        String label = "Your Board";
        int labelWidth = fm.stringWidth(label);
        int labelX = boardX + (width * 10 - labelWidth) / 2;
        g.drawString(label, labelX, boardY - 20);

        int x = boardX;
        int y = boardY;

        for(int i = 0; i < desktopPlayer.length; i++) {
            for(int j = 0; j < desktopPlayer[i].length; j++) {
                int element = desktopPlayer[i][j];

                if(element == -1) {
                    g.setColor(new Color(30, 60, 100));
                    g.fillRect(x, y, width, height);
                    g.setColor(Color.WHITE);
                    g.drawLine(x, y, x + width, y + height);
                    g.drawLine(x + width, y, x, y + height);
                } else if(element == -9){
                    g.setColor(new Color(200, 50, 50));
                    g.fillRect(x, y, width, height);
                    g.setColor(Color.YELLOW);
                    int cx = x + width / 2;
                    int cy = y + height / 2;
                    g.fillOval(cx - 8, cy - 8, 16, 16);
                    g.setColor(Color.RED);
                    g.drawLine(x, y, x + width, y + height);
                    g.drawLine(x + width, y, x, y + height);
                }

                g.setColor(Color.WHITE);
                g.drawRect(x, y, width, height);
                x = x + width;
            }
            x = boardX;
            y = y + height;
        }

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

            } else {
                shipDrawX = boardX + ship.getX() * width;
                shipDrawY = boardY + ship.getY() * height;
                drawWidth = ship.isVertical() ? width : width * ship.getSize();
                drawHeight = ship.isVertical() ? height * ship.getSize() : height;
            }

            drawChristmasShip(g, shipDrawX, shipDrawY, drawWidth, drawHeight, ship.getSize(), ship.isVertical());
        }
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
                new Color(255, 215, 0),    // Золотой
                new Color(0, 191, 255),    // Голубой
                new Color(255, 105, 180),  // Розовый
                new Color(50, 205, 50),    // Зеленый
                new Color(255, 140, 0)     // Оранжевый
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
        int panelHeight = getHeight();

        int x = (panelWidth - totalWidth) / 2;
        int y = (panelHeight - totalHeight) / 2;

        return new Point(x, y);
    }

    public Point getPlayerBoardPosition() {
        int width = Coordinates.WIDTH;
        int height = Coordinates.HEIGHT;
        int totalWidth = width * 10 + 100 + width * 10;
        int totalHeight = height * 10;
        Point centerPos = getCenteredPosition(totalWidth, totalHeight);

        int x = centerPos.x + (width * 10) + 100;
        int y = centerPos.y;
        return new Point(x, y);
    }

    public void startLevelWindowAnimation() {
        levelAlpha = 0f;
        Timer timer = new Timer(15, null);

        timer.addActionListener(e -> {
            levelAlpha += 0.03f;

            if (levelAlpha >= 1f) {
                levelAlpha = 1f;
                timer.stop();
            }

            repaint();
        });

        timer.start();
    }

    public void startLevelWindowFadeOut() {
        levelAlpha = 1f;
        Timer timer = new Timer(15, null);

        timer.addActionListener(e -> {
            levelAlpha -= 0.03f;

            if (levelAlpha <= 0f) {
                levelAlpha = 0f;
                timer.stop();
            }

            repaint();
        });

        timer.start();
    }

    private void drawLevelWindow(Graphics g) {
        int state = model.getLevelWindow();
        if (state == 0) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, levelAlpha));

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
            text = "LEVEL " + model.getCurrentLevel();
        } else if (state == 2) {
            text = "LEVEL " + model.getCurrentLevel() + " COMPLETED!";
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