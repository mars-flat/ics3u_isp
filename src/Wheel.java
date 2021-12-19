import hsa.Console;

import java.awt.*;

public class Wheel implements Runnable {
    int centerX, centerY, radius;
    String[] messages;
    Color[] colors;
    Console c;
    Font contentFont;

    public boolean running;

    public Wheel(int x, int y, int rad, int turnTo, String[] parts, Console screen) {
        centerX = x;
        centerY = y;
        radius = rad;
        messages = parts;
        c = screen;
        running = true;
        contentFont = new Font("Monospaced", Font.PLAIN, rad / 10);

        colors = new Color[messages.length];
        for (int i = 0; i < messages.length; i++) {
            int r = (int) (Math.random() * 255), g = (int) (Math.random() * 255), b = (int) (Math.random() * 255);
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
            int dist = radius - radius * (message.length() - i) / 8;
            int x = (int) Math.cos(radians(angle)) * dist + radius / 25;
            int y = (int) Math.sin(radians(angle)) * dist + radius / 20;
            c.drawString(message.substring(i, i + 1), x, y);
        }
    }

    String curMessage(int angle) {
        return messages[angle % 360 * messages.length / 360];
    }

    public void animate(int angle) {
        double increment = 360.0 / messages.length;
        for (int i = 0; i < messages.length; i++) {
            c.setColor(colors[i]);
            c.fillArc(centerX, centerY, 2 * radius, 2 * radius, (int) (increment * i) + angle, (int) increment);
        }
        c.setColor(Color.BLACK);
        for(int i = 0; i < messages.length; i++){
            drawString(messages[i], (int) (increment * i + increment / 2));
        }
    }

    public void stop() {
        running = false;
    }

    public void run() {
        int curAngle = 0;
        while (running) {
            animate(curAngle);
            curAngle += 2;
        }
    }
}
