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

    public Canvas(Model model) {
        this.model = model;
        setBackground(new Color(30, 30, 30));
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

        Graphics2D graphics2D = (Graphics2D) g;

        Cell[][] cellAreaComputer = model.getCallAreaComputer();

        for(int i = 0; i < cellAreaComputer.length; i++) {
            for(int j = 0; j < cellAreaComputer[i].length; j++) {
                graphics2D.setColor(Color.YELLOW);
                graphics2D.draw(cellAreaComputer[i][j]);

            }
        }
    }


    private void drawWon(Graphics g) {
        g.setFont(new Font("Bernard MT Condensed", Font.BOLD, 50));
        g.setColor(Color.GREEN);
        g.drawString("You have won!", Coordinates.X + (Coordinates.WIDTH * 3), Coordinates.Y + (Coordinates.HEIGHT * 12));

        int[][] desktopComputer = model.getDesktopComputer();

        int x = Coordinates.X;
        int y = Coordinates.Y;
        int width = Coordinates.WIDTH;
        int height = Coordinates.HEIGHT;


        for(int index = 0; index < 4; index++) {
            int i = arrayOfIndexes[0][index];
            int j = arrayOfIndexes[1][index];
            g.drawImage(shipImage, x + (width * j), y + (height * i),  width, height, null);
        }

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

        i1 = arrayOfIndexes[0][16];
        j1 = arrayOfIndexes[1][16];
        i3 = arrayOfIndexes[0][19];
        j3 = arrayOfIndexes[1][19];

        if(i1 == i3) {
            g.drawImage(shipFourShipImageHorizontal, x + (width * j1), y + (height * i1), width * 4, height, null);
        } else {
            g.drawImage(shipFourShipImageVertical, x + (width * j1), y + (height * i1), width, height * 4, null);
        }

        g.setColor(Color.GREEN);
        g.drawRect(x - 10, y - 10, width * 10 + 50, height * 10 + 50);

    }
}
