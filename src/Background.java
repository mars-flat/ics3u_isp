import hsa.Console;

import java.awt.*;

public class Background implements Runnable{
    Console c;
    Color background;
    Color dust;
    Color fabric;
    Color topFabric;
    int[][] dustCoords;

    boolean running;

    public Background(Console con){
        c = con;
        background = Color.BLACK;
        dust = Color.WHITE;
        fabric = new Color(200, 0, 0);
        topFabric = new Color(255, 0, 0);
        running = true;

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

    private void updateDust(){
        for(int i = 0; i < dustCoords.length; i++){
            dustCoords[i][1] = (dustCoords[i][1] + 5) % 500;
        }
    }

    private void drawDust(){
        c.setColor(Color.WHITE);
        for(int i = 0; i < dustCoords.length; i++){
            c.fillOval(dustCoords[i][0], dustCoords[i][1], 5, 5);
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

    public void stop(){
        running = false;
    }

    // only runs the "dust animation" along with the static background
    public void run(){
        while(running){
            updateDust();
            drawBackground();
            drawDust();
            try{
                Thread.sleep(50);
            } catch(InterruptedException e){
                c.println(e.getMessage());
            }
        }
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
        Thread t = new Thread(b);
        t.start();
        try{
            Thread.sleep(5000);
            b.stop();
            t.join();
        } catch (InterruptedException e){
            c.println(e.getMessage());
        }
    }
}
