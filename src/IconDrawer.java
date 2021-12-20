import hsa.Console;

import java.awt.*;

public class IconDrawer {
    Console c;

    Font buttonFont;

    public IconDrawer(Console con){
        c = con;
        buttonFont = new Font("SansSerif", Font.BOLD, 50);
    }

    public void drawArrow(int x, int y){
        c.setColor(new Color(0, 255, 255));
        c.fillRect(x, y-10, 40, 20);
        int[] xPoints = {x + 40, x + 60, x + 40};
        int[] yPoints = {y - 20, y, y + 20};
        c.fillPolygon(xPoints, yPoints, 3);
    }

    public void drawButton(String message, int x, int y, int width, int height, int leftMargin){
        c.setColor(new Color(0, 255, 255));
        c.fillRoundRect(x, y, width, height, 15, 15);
        c.setFont(buttonFont);
        c.setColor(Color.BLACK);
        c.drawString(message, x + leftMargin, y + height - (height - 40) / 2);
    }

    // -------------------------------------------- TESTING ----------------------------------
    public static void main(String[] args){
        Console c = new Console();
        IconDrawer d = new IconDrawer(c);
        c.setColor(Color.BLACK);
        c.fillRect(0, 0, 640, 500);
        d.drawArrow(100, 100);

        d.drawButton("Hello", 300, 300, 200, 50, 30);
    }
}
