/*
* Daniel Ye & Shane Chen
* Ms. Basaraba
* January 7, 2022
* This component draws many of the icons (the buttons and arrows) that is used in the Main menu in Main.java
 */

// imports
import hsa.Console;
import java.awt.*;

// Icon Drawer class
public class IconDrawer {
    Console c; // interface for graphics
    Font buttonFont; // font of button

    // class constructor
    public IconDrawer(Console con){
        // initialize instance variables
        c = con;
        buttonFont = new Font("SansSerif", Font.BOLD, 40);
    } // IconDrawer class constructor

    // draws an arrow
    public void drawArrow(int x, int y){
        // set the color to dark blue
        c.setColor(new Color(86, 131, 255));

        // draw the body of the arrow
        c.fillRect(x, y-10, 30, 20);

        // draw the head of the arrow
        int[] xPoints = {x + 30, x + 50, x + 30};
        int[] yPoints = {y - 20, y, y + 20};
        c.fillPolygon(xPoints, yPoints, 3);
    } // drawArrow method

    // draws a button with message [message]
    public void drawButton(String message, int x, int y, int width, int height, int leftMargin){
        // set the color to teal
        c.setColor(new Color(0, 255, 255));

        // fill the body of the button
        c.fillRoundRect(x, y, width, height, 15, 15);

        // draw the message
        c.setFont(buttonFont);
        c.setColor(Color.BLACK);
        c.drawString(message, x + leftMargin, y + height - (height - 30) / 2);
    } // drawButton method

} // IconDrawer class
