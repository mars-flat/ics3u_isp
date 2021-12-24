import hsa.Console;

import java.awt.*;

public class Background{
    Console c;
    Color background;
    Color dust;
    Color fabric;
    Color topFabric;

    Font titleFont;

    public Background(Console con){
        c = con;
        background = Color.BLACK;
        dust = Color.WHITE;
        fabric = new Color(200, 0, 0);
        topFabric = new Color(255, 0, 0);

        titleFont = new Font("SansSerif", Font.BOLD, 80);
    }

    public void drawCurtain(int percentDone){
        int numTops = 7;
        int topDiam = c.getWidth() / numTops;
        c.setColor(fabric);
        c.fillRect(0, 0, percentDone, c.getHeight());
        c.fillRect(c.getWidth() - percentDone, 0, percentDone, c.getHeight());

        c.setColor(topFabric);
        for(int i = 0; i < numTops; i++){
            c.fillOval(topDiam * i, percentDone * topDiam / 200 - topDiam, topDiam, topDiam);
        }
    }

    public void drawBackground(int percentDone){
        c.setColor(Color.BLACK);
        c.fillRect(0, 0, c.getWidth(), c.getHeight());
        drawCurtain(percentDone);
    }

    public void drawBackground(String title, int x){
        drawBackground(100);
        c.setColor(Color.YELLOW);
        c.setFont(titleFont);
        c.drawString(title, x, 250);
    }

    public void drawBackground(){
        drawBackground("", 0);
    }

    // ---------------------------------- TESTING ----------------------------
    public static void main(String[] args){
        Console c = new Console();
        Background b = new Background(c);
        for(int i = 0; i <= 100; i += 5){
            try{
                b.drawBackground(i);
                Thread.sleep(50);
            } catch (InterruptedException e){
                c.print(e.getMessage());
            }
        }
    }
}
