import hsa.Console;

import java.awt.*;

public class Wheel implements Runnable {
    int centerX, centerY, radius;
    String[] messages;
    Color[] colors;
    Console c;
    Font contentFont;

    public boolean running;

    public Wheel(int x, int y, int rad, String[] parts, Console screen) {
        centerX = x;
        centerY = y;
        radius = rad;
        messages = parts;
        c = screen;
        running = true;
        contentFont = new Font("Monospaced", Font.BOLD, rad / 8);

        colors = new Color[messages.length];

        int minIntensity = 70;
        for (int i = 0; i < messages.length; i++) {
            int r = (int) (Math.random() * (256 - minIntensity)) + minIntensity;
            int g = (int) (Math.random() * (256 - minIntensity)) + minIntensity;
            int b = (int) (Math.random() * (256 - minIntensity)) + minIntensity;
            int maxVal = Math.max(r, Math.max(g, b));

            // the value to multiply each value by, so that we can "brighten" each color and so they don't seem "dull"
            double multiplier = 255.0 / maxVal;
            r *= multiplier;
            g *= multiplier;
            b *= multiplier;
            colors[i] = new Color(r, g, b);
        }
    }

    private double radians(double degrees) {
        return degrees / 180 * 3.14159265;
    }

    private void drawString(String message, int angle) {
        c.setFont(contentFont);
        for (int i = 0; i < message.length(); i++) {
            int dist = radius - radius / 7 - radius * i / 9;
            int x = (int) (Math.cos(radians(angle)) * dist + radius / 25);
            int y = (int) (Math.sin(radians(angle)) * dist + radius / 20);
            c.drawString(message.substring(i, i + 1), x + centerX, y + centerY);
        }
    }

    String curMessage(int angle) {
        return messages[angle % 360 * messages.length / 360];
    }

    public void animate(int angle) {
        double increment = 360.0 / messages.length;
        for (int i = 0; i < messages.length; i++) {
            c.setColor(colors[i]);
            c.fillArc(centerX - radius, centerY - radius, 2 * radius, 2 * radius, (int) (increment * i) + angle, (int) increment);
        }
        c.setColor(Color.BLACK);
        c.drawOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
        for(int i = 0; i < messages.length; i++){
            drawString(messages[i], (int) (increment * i + increment / 2 - angle));
        }
    }

    public void stop() {
        running = false;
    }

    public void run() {
        int curAngle = 0;
        while (running) {
            animate(curAngle);
            curAngle += 5;
            try{
                Thread.sleep(50);
            } catch(InterruptedException e){
                c.print(e.getMessage());
            }
        }
    }

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

}
