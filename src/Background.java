import hsa.Console;

import java.awt.*;

public class Background{
    Console c;
    Color background;
    Color dust;
    Color fabric;
    Color topFabric;
    int[][] dustCoords;

    public Background(Console con){
        c = con;
        background = Color.BLACK;
        dust = Color.WHITE;
        fabric = new Color(200, 0, 0);
        topFabric = new Color(255, 0, 0);

        int numDust = 10;
        dustCoords = new int[numDust][2];
        for(int i = 0; i < 10; i++){
            dustCoords[i][0] = (int) (Math.random() * 640);
            dustCoords[i][1] = (int) (Math.random() * 500);
        }
    }

    public void drawCurtain(int percentDone){
        int numTops = 4;
        int topDiam = 640 / numTops;
        c.setColor(fabric);
        c.fillRect(0, 0, percentDone, 500);
        c.fillRect(640 - percentDone, 0, percentDone, 500);

        c.setColor(topFabric);
        for(int i = 0; i < numTops; i++){
            c.fillOval(topDiam * i, percentDone * topDiam / 200 - topDiam, topDiam, topDiam);
        }
    }

    public void drawBackground(int percentDone){
        c.setColor(Color.BLACK);
        c.fillRect(0, 0, 640, 500);
        drawCurtain(percentDone);
    }

    public void drawBackground(){
        drawBackground(100);
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
