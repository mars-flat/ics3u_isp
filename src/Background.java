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
    Console c; // interface for drawing the background
    Color background; // background color
    Color fabric; // the "side" curtain color
    Color topFabric; // the color of the top semicircles

    Font titleFont; // the font of the title

    // Background class constructor
    public Background(Console con){
        // set and initialize instance variables
        c = con;
        background = Color.BLACK;
        fabric = new Color(200, 0, 0);
        topFabric = new Color(255, 0, 0);

        titleFont = new Font("SansSerif", Font.BOLD, 60);
    } // Background class constructor

    // draws [percentDone]% of the background - used for animating the curtain coming into the screen
    public void drawCurtain(int percentDone){
        // the number of top semicircles
        int numTops = 7;

        // the diameter of each semicircle
        int topDiam = c.getWidth() / numTops;

        // draw the side curtains
        c.setColor(fabric);
        c.fillRect(0, 0, (int) (percentDone * 0.8), c.getHeight());
        c.fillRect(c.getWidth() - (int) (percentDone * 0.8), 0, percentDone, c.getHeight());

        // draw the circles at the top
        c.setColor(topFabric);
        for(int i = 0; i < numTops; i++){
            // for each circle, calculate the current position and draw it
            c.fillOval(topDiam * i, percentDone * topDiam / 200 - topDiam, topDiam, topDiam);
        } // for loop for drawing top curtains
    } // drawCurtain method

    // draw [percentDone]% of the background - used for animation
    public void drawBackground(int percentDone){
        // fill the background with a blackness
        c.setColor(Color.BLACK);
        c.fillRect(0, 0, c.getWidth(), c.getHeight());

        // draw the curtain at this frame
        drawCurtain(percentDone);
    } // drawBackground method

    // draw the background with a small title
    public void drawBackground(String title, int x){
        // draw the current background
        drawBackground(100);

        // draw an extra title
        c.setColor(Color.YELLOW);
        c.setFont(titleFont);
        c.drawString(title, x, 200);
    } // drawBackground method

    // draws a completed background
    public void drawBackground(){
        // draw the background
        drawBackground("", 0);
    } // drawBackground method
} // Background class