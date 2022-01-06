/*
* Daniel Ye and Shane Chen
* Ms. Basaraba
* January 6
* This class is a component that is used throughout the rest of the program, drawing the Backgrounds in a variety of ways
 */

// imports
import hsa.Console;
import java.awt.*;

// Background class
public class Background{
    Console c;
    Color background;
    Color fabric;
    Color topFabric;

    Font titleFont;

    public Background(Console con){
        c = con;
        background = Color.BLACK;
        fabric = new Color(200, 0, 0);
        topFabric = new Color(255, 0, 0);

        titleFont = new Font("SansSerif", Font.BOLD, 60);
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
        c.setColor(Color.WHITE);
        c.setFont(titleFont);
        if(!title.equals("")){
            c.fillRect(300, 210, 680, 5);
            c.drawString(title, x, 270);
        }
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
