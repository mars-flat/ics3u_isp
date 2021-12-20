import hsa.Console;

import java.awt.*;

public class Curtain {
    Color fabric;
    Color topFabric;
    Console c;

    public Curtain(Console con){
        c = con;
        fabric = new Color(200, 0, 0);
        topFabric = new Color(255, 0, 0);
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

    // ------------------------ TESTING ------------------------------------------
    public static void main(String[] args){
        Console c = new Console();
        Curtain curt = new Curtain(c);
        for(int i = 0; i <= 100; i += 3) {
            curt.drawCurtain(i);
            try {
                Thread.sleep(50);
            } catch(InterruptedException e){
                c.print(e.getMessage());
            }
        }
    }

}
