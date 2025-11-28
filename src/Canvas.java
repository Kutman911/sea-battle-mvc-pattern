import javax.swing.JPanel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Canvas extends JPanel{

    private Model model;
    private Image shipImage;
    private Image shipImageHorizontal;
    private Image shipImageVertical;
    private Image shipThreeShipImageHorizontal;
    private Image shipThreeShipImageVertical;
    private Image shipFourShipImageHorizontal;
    private Image shipFourShipImageVertical;
    private int[][] arrayOfIndexes;
    private Point computerBoardPosition;

    public Canvas(Model model) {
        this.model = model;
        setBackground(new Color(75, 139, 181));
        File fileImage = new File("images/oneShip.png");
        File fileImageHorizontal = new File("images/twoShipHorizontal.png");
        File fileImageVertical = new File("images/twoShipVertical.png");
        File fileThreeShipImageHorizontal = new File("images/threeShipHorizontal.png");
        File fileThreeShipImageVertical = new File("images/threeShipVertical.png");
        File fileFourShipImageHorizontal = new File("images/fourShipHorizontal.png");
        File fileFourShipImageVertical = new File("images/fourShipVertical.png");

        try {
            shipImage = ImageIO.read(fileImage );
            shipImageHorizontal = ImageIO.read(fileImageHorizontal);
            shipImageVertical = ImageIO.read(fileImageVertical);
            shipThreeShipImageHorizontal = ImageIO.read(fileThreeShipImageHorizontal);
            shipThreeShipImageVertical = ImageIO.read(fileThreeShipImageVertical);
            shipFourShipImageHorizontal = ImageIO.read(fileFourShipImageHorizontal);
            shipFourShipImageVertical = ImageIO.read(fileFourShipImageVertical);
        } catch (IOException ioe) {
            System.out.println("Error " + ioe);
        }

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

        if(model.won()) {
            drawWon(g);
        } else {
            drawDesktopComputer(g);
        }

        drawDesktopPlayer(g);
        drawSnowflakes((Graphics2D) g);
    }

    public Point getComputerBoardPosition() {
        return computerBoardPosition;
    }

    private void drawWon(Graphics g) {
        int width = Coordinates.WIDTH;
        int height = Coordinates.HEIGHT;

        int totalWidth = width * 10;
        int totalHeight = height * 10;
        Point centerPos = getCenteredPosition(totalWidth, totalHeight);
        int x = centerPos.x;
        int y = centerPos.y;

        g.setFont(new Font("Bernard MT Condensed", Font.BOLD, 50));
        g.setColor(Color.GREEN);
        g.drawString("You are won!", Coordinates.X + (Coordinates.WIDTH * 3), Coordinates.Y + (Coordinates.HEIGHT * 12));

        int[][] desktopComputer = model.getDesktopComputer();

        // draw 1
        for(int index = 0; index < 4; index++) {
            int i = arrayOfIndexes[0][index];
            int j = arrayOfIndexes[1][index];
            g.drawImage(shipImage, x + (width * j), y + (height * i),  width, height, null);
        }

        // draw 2
        for(int index = 4; index < 10; index = index + 2) {
            int i1 = arrayOfIndexes[0][index];
            int j1 = arrayOfIndexes[1][index];
            int i2 = arrayOfIndexes[0][index + 1];
            int j2 = arrayOfIndexes[1][index + 1];
            if(i1 == i2) {
                g.drawImage(shipImageHorizontal, x + (width * j1), y + (height * i1), width * 2, height, null);
            } else {
                g.drawImage(shipImageVertical, x + (width * j1), y + (height * i1), width, height * 2, null);
            }
        }

        // draw 3
        int i1 = arrayOfIndexes[0][10];
        int j1 = arrayOfIndexes[1][10];
        int i3 = arrayOfIndexes[0][12];
        int j3 = arrayOfIndexes[1][12];

        if(i1 == i3) {
            g.drawImage(shipThreeShipImageHorizontal, x + (width * j1), y + (height * i1), width * 3, height, null);
        } else {
            g.drawImage(shipThreeShipImageVertical, x + (width * j1), y + (height * i1), width, height * 3, null);
        }

        i1 = arrayOfIndexes[0][13];
        j1 = arrayOfIndexes[1][13];
        i3 = arrayOfIndexes[0][15];
        j3 = arrayOfIndexes[1][15];

        if(i1 == i3) {
            g.drawImage(shipThreeShipImageHorizontal, x + (width * j1), y + (height * i1), width * 3, height, null);
        } else {
            g.drawImage(shipThreeShipImageVertical, x + (width * j1), y + (height * i1), width, height * 3, null);
        }

        // draw 4
        i1 = arrayOfIndexes[0][16];
        j1 = arrayOfIndexes[1][16];
        i3 = arrayOfIndexes[0][19];
        j3 = arrayOfIndexes[1][19];

        if(i1 == i3) {
            g.drawImage(shipFourShipImageHorizontal, x + (width * j1), y + (height * i1), width * 4, height, null);
        } else {
            g.drawImage(shipFourShipImageVertical, x + (width * j1), y + (height * i1), width, height * 4, null);
        }

        g.setColor(Color.WHITE);
        g.drawRect(x - 10, y - 10, width * 10 + 50, height * 10 + 50);

    }

    private void drawDesktopComputer(Graphics g) {
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
                    g.setColor(Color.BLACK);
                    g.fillRect(x, y, width, height);
                    g.setColor(Color.WHITE);
                    g.drawLine(x, y, x + width, y + height);
                    g.drawLine(x + width, y, x, y + height);
                    g.setColor(Color.WHITE);
                    g.drawRect(x, y, width, height);
                } else if(element == -9){
                    g.setColor(Color.RED);
                    g.fillRect(x, y, width, height);
                    g.setColor(Color.WHITE);
                    g.drawLine(x, y, x + width, y + height);
                    g.drawLine(x + width, y, x, y + height);
                    g.setColor(Color.WHITE);
                    g.drawRect(x, y, width, height);
                } else {
                    g.setColor(Color.WHITE);
                    g.drawRect(x, y, width, height);
                }
                x = x + width;
            }
            x = centerPos.x;
            y = y + height;
        }
    }

    private void drawDesktopPlayer(Graphics g) {
        int width = Coordinates.WIDTH;
        int height = Coordinates.HEIGHT;

        int totalWidth = width * 10 + 100 + width * 10;
        int totalHeight = height * 10;
        Point centerPos = getCenteredPosition(totalWidth, totalHeight);

        int boardX = centerPos.x + (width * 10) + 100;
        int boardY = centerPos.y;

        int[][] desktopPlayer = model.getDesktopPlayer();


        int x = boardX;
        int y = boardY;

        for(int i = 0; i < desktopPlayer.length; i++) {
            for(int j = 0; j < desktopPlayer[i].length; j++) {

                int element = desktopPlayer[i][j];

                if(element == -1) {
                    g.setColor(Color.BLACK);
                    g.fillRect(x, y, width, height);
                    g.setColor(Color.WHITE);
                    g.drawLine(x, y, x + width, y + height);
                    g.drawLine(x + width, y, x, y + height);
                    g.setColor(Color.WHITE);
                    g.drawRect(x, y, width, height);
                } else if(element == -9){
                    g.setColor(Color.RED);
                    g.fillRect(x, y, width, height);
                    g.setColor(Color.WHITE);
                    g.drawLine(x, y, x + width, y + height);
                    g.drawLine(x + width, y, x, y + height);
                    g.setColor(Color.WHITE);
                    g.drawRect(x, y, width, height);
                } else {
                    g.setColor(Color.WHITE);
                    g.drawRect(x, y, width, height);
                }
                x = x + width;
            }
            x = boardX;
            y = y + height;
        }

        for (Ship ship : model.getPlayerShips()) {
            Image shipImageToDraw = getShipImage(ship);

            int shipDrawX, shipDrawY;

            if (ship.isDragging()) {
                shipDrawX = ship.getX();
                shipDrawY = ship.getY();

                if (model.isValidPlacement(ship)) {
                    g.setColor(new Color(0, 255, 0, 100));
                } else {
                    g.setColor(new Color(255, 0, 0, 100));
                }

                int sw = ship.isVertical() ? width : width * ship.getSize();
                int sh = ship.isVertical() ? height * ship.getSize() : height;
                g.fillRect(shipDrawX, shipDrawY, sw, sh);

            } else {
                shipDrawX = boardX + ship.getX() * width;
                shipDrawY = boardY + ship.getY() * height;
            }

            int drawWidth = ship.isVertical() ? width : width * ship.getSize();
            int drawHeight = ship.isVertical() ? height * ship.getSize() : height;

            g.drawImage(shipImageToDraw, shipDrawX, shipDrawY, drawWidth, drawHeight, null);
        }
    }

    private void drawSnowflakes(Graphics2D g) {
        g.setColor(new Color(255, 255, 255, 180));
        for (int i = 0; i < 100; i++) {
            int x = (int)(Math.random() * getWidth());
            int y = (int)(Math.random() * getHeight());
            int size = (int)(2 + Math.random() * 4);
            g.fillOval(x, y, size, size);
        }
    }

    private Point getCenteredPosition(int totalWidth, int totalHeight) {
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int x = (panelWidth - totalWidth) / 2;
        int y = (panelHeight - totalHeight) / 2;

        return new Point(x, y);
    }

    private Image getShipImage(Ship ship) {
        if (ship.getSize() == 1) return shipImage;
        if (ship.getSize() == 2) {
            return ship.isVertical() ? shipImageVertical : shipImageHorizontal;
        }
        if (ship.getSize() == 3) {
            return ship.isVertical() ? shipThreeShipImageVertical : shipThreeShipImageHorizontal;
        }
        if (ship.getSize() == 4) {
            return ship.isVertical() ? shipFourShipImageVertical : shipFourShipImageHorizontal;
        }
        return null;
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

}
