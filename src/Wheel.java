/*
 * Shane Chen & Daniel Ye
 * Ms. Basaraba
 * January 5
 * This class is a component of Main.java - an object which simulates a wheel spinning and contains a utility method which returns section that the wheel is currently on
 */

// imports
import hsa.Console;
import java.awt.*;

// Wheel class
public class Wheel implements Runnable {
    int centerX, centerY, radius; // the dimensions of the wheel - the center is at (centerX, centerY) and the wheel has radius [radius]
    String[] messages; // the messages to be displayed in each section
    Color[] colors; // the color of each section
    Console c; // the interface for drawing the wheel
    Font contentFont; // the font for drawing the messages

    public boolean running; // whether or not the wheel is running - used for when this wheel is an animation

    // class constructor
    public Wheel(int x, int y, int rad, String[] parts, Console screen) {
        // initialize the various instance variables
        centerX = x;
        centerY = y;
        radius = rad;
        messages = parts;
        c = screen;
        running = true;
        contentFont = new Font("Monospaced", Font.BOLD, rad / 8);

        colors = new Color[messages.length];

        // fill in each color
        int minIntensity = 70; // the minimum intensity of each "strand" of red, green, and blue
        for (int i = 0; i < messages.length; i++) {
            // pick 3 random values for the red, green, and blue, each value is a random number between minIntensity and 255
            int r = (int) (Math.random() * (256 - minIntensity)) + minIntensity;
            int g = (int) (Math.random() * (256 - minIntensity)) + minIntensity;
            int b = (int) (Math.random() * (256 - minIntensity)) + minIntensity;

            // get the maximum value
            int maxVal = Math.max(r, Math.max(g, b));

            // the value to multiply each value by, so that we can "brighten" each color and so they don't seem "dull"
            double multiplier = 255.0 / maxVal;

            // multiply each number by [multiplier] - this guarantees that at least one of r, g, and b is equal to 255
            r *= multiplier;
            g *= multiplier;
            b *= multiplier;

            // set the current color
            colors[i] = new Color(r, g, b);
        } // for loop for filling in colors
    } // Wheel class constructor

    // returns the radian equivalent of an angle in degrees
    private double radians(double degrees) {
        return degrees / 180 * 3.14159265;
    } // radians method

    // draws a string at a particular angle
    private void drawString(String message, int angle) {
        // set the font to the current content font
        c.setFont(contentFont);

        // for each letter, calculate its position
        for (int i = 0; i < message.length(); i++) {
            // calculate the distance at which the center of the letter should be, decrements of radius/9 for each letter
            int dist = radius * 6 / 7 - radius * i / 9;

            // calculate the x and y coordinates for a particular distance, add an offset to get the bottom left corner to display the letter
            int x = (int) (Math.cos(radians(angle)) * dist - radius / 35);
            int y = (int) (Math.sin(radians(angle)) * dist + radius / 20);

            // draw the letter
            c.drawString(message.substring(i, i + 1), x + centerX, y + centerY);
        } // for loop for drawing each letter
    } // drawString method

    // get the current message if the wheel is at some angle
    String curMessage(int angle) {
        // add 270 degrees since the "ticker" is at the top but 0 degrees in hsa is to the right
        angle += 270;
        return messages[angle % 360 * messages.length / 360];
    } // curMessage method

    // animates the wheel at a particular angle
    public void animate(int angle) {
        // the increment in the angle to draw each section - also the width of each section
        double increment = 360.0 / messages.length;

        // for each section, draw the background for that section
        for (int i = 0; i < messages.length; i++) {
            c.setColor(colors[i]);
            c.fillArc(centerX - radius, centerY - radius, 2 * radius, 2 * radius, (int) (increment * i) + angle - 1, (int) increment + 2);
        } // for loop for drawing the wheel itself

        // draw an outline of the wheel
        c.setColor(Color.BLACK);
        c.drawOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);

        // for each message, draw the string at that angle
        for(int i = 0; i < messages.length; i++){
            // we draw along the "middle" of the section, so we add an offset of increment / 2
            drawString(messages[i], (int) (increment * i + increment / 2 - angle));
        } // for loop for drawing each word in the message

        // draw/fill the "ticker" at the top
        int[] x = {centerX - radius / 20, centerX + radius / 20, centerX};
        int[] y = {centerY - radius - radius / 20, centerY - radius - radius / 20, centerY - radius + radius / 20};

        c.setColor(new Color(0, 150, 200));
        c.fillPolygon(x, y, 3);
    } // animate mtehod

    // stops the wheel
    public void stop() {
        running = false;
    } // stop method

    // the method that is run when this is run as purely an animation (i.e when this is run as a thread)
    public void run() {
        // calculate the current angle to draw the wheel on
        int curAngle = 0;

        // while the wheel is being animated
        while (running) {
            // animate the current angle
            animate(curAngle);
            // increment the current angle
            curAngle += 5;
            // pause for 50 milliseconds
            try{
                Thread.sleep(50);
            } catch(InterruptedException e){
                c.print(e.getMessage());
            } // try/catch for pausing
        } // while the wheel is being animated
    } // run method

    // ----------------------------- TESTING -----------------------------------
    public static void main(String[] args){
        Console c = new Console();
        Wheel w = new Wheel(200, 200, 200, new String[] {"$500", "$600", "$700", "$500", "$1200", "$1600", "$300", "broke", "$800"}, c);
        Thread t = new Thread(w);
        t.start();
        try{
            Thread.sleep(5000);
            w.stop();
            t.join();
        } catch (InterruptedException e){
            c.print(e.getMessage());
        }
    }

} // Wheel class
