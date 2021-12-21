import hsa.Console;
import hsa.Message;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Game {
    int[] money;
    Console c;
    String uncovered;
    String[] names;
    String[][] phrases;
    String[] wheelParts;

    int curRound;
    boolean finalRound;
    String curPhrase;

    Background background;
    Font promptFont;
    Font nameFont;
    public static int MAX_NAME_LENGTH = 8;

    public Game(Console con) {
        c = con;
        background = new Background(c);
        promptFont = new Font("Serif", Font.PLAIN, 19);
        nameFont = new Font("MonoSpaced", Font.BOLD, 80);
        uncovered = "";
        money = new int[2];
    }

    private String[] getNames() {
        String[] playerNames = {"", ""};
        for (int i = 0; i < 2; i++) {
            background.drawBackground();
            c.setColor(Color.WHITE);
            c.setFont(promptFont);
            c.drawString("Player " + (i + 1) + ", please enter your name and press <ENTER>: ", 110, 150);
            char cur;
            while ((cur = c.getChar()) != '\n') {
                if (cur == '\b') {
                    if (playerNames[i].length() > 0) {
                        playerNames[i] = playerNames[i].substring(0, playerNames[i].length() - 1);
                    }
                } else {
                    playerNames[i] = playerNames[i] + cur;
                }
                if (playerNames[i].length() > MAX_NAME_LENGTH) {
                    new Message("Please enter a maximum of " + MAX_NAME_LENGTH + " characters");
                    playerNames[i] = "";
                }
                playerNames[i] = playerNames[i].toUpperCase();
                c.setColor(Color.BLACK);
                c.fillRect(120, 200, 400, 300);
                c.setFont(nameFont);
                c.setColor(Color.WHITE);
                c.drawString(playerNames[i], 130, 400);
            }
        }
        return playerNames;
    }

    private static String[] split(String toSplit) {
        int startInd = 0, numSpaces = 0;
        for (int i = 0; i < toSplit.length(); i++) {
            if (toSplit.charAt(i) == ' ') {
                numSpaces++;
            }
        }
        String[] parts = new String[numSpaces + 1];
        int curInd = 0;
        for (int i = 0; i < toSplit.length(); i++) {
            if (toSplit.charAt(i) == ' ') {
                parts[curInd++] = toSplit.substring(startInd, i);
                startInd = i + 1;
            }
        }
        parts[curInd] = toSplit.substring(startInd);
        return parts;
    }

    private void loadPhrases() {
        try {
            BufferedReader lineReader = new BufferedReader(new FileReader("src/data/phrases.txt"));
            int numLines = 0;
            while (lineReader.readLine() != null) {
                numLines++;
            }
            String line;
            BufferedReader br = new BufferedReader(new FileReader("src/data/phrases.txt"));
            phrases = new String[numLines][];
            int curLine = 0;
            while ((line = br.readLine()) != null) {
                phrases[curLine++] = split(line);
            }
        } catch (IOException e) {
            c.print(e.getMessage());
        }
    }

    private void loadWheel() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("src/data/wheel.txt"));
            int numLines = 0;
            while (br.readLine() != null) {
                numLines++;
            }
            wheelParts = new String[numLines];
            br = new BufferedReader(new FileReader("src/data/wheel.txt"));
            for (int i = 0; i < numLines; i++) {
                wheelParts[i] = br.readLine();
            }
        } catch (IOException e) {
            c.print(e.getMessage());
        }

    }

    private void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            c.print(e.getMessage());
        }
    }

    private String spinWheel() {
        background.drawBackground();
        Wheel toSpin = new Wheel(320, 270, 200, wheelParts, c);
        toSpin.animate(0);
        c.setColor(new Color(0, 0, 150));
        c.fillOval(320 - 40, 270 - 40, 80, 80);
        c.setColor(Color.WHITE);
        c.setFont(promptFont);
        c.drawString("ENTER", 293, 275);
        while(c.getChar() != '\n'){
            new Message("Please press the <ENTER> key to spin the wheel");
        }
        pause(1000);

        int[] numTicks = {(int) (Math.random() * 100) + 100, (int) (Math.random() * 40) + 30, (int) (Math.random() * 50) + 50};
        int curAngle = 0;
        for (int i = 0; i < numTicks[0]; i++) {
            toSpin.animate(curAngle);
            pause(50);
            curAngle += 15;
        }
        for (int i = 0; i < numTicks[1]; i++) {
            toSpin.animate(curAngle);
            pause(50);
            curAngle += 7;
        }
        for (int i = 0; i < numTicks[2]; i++) {
            toSpin.animate(curAngle);
            pause(50);
            curAngle += 3;
        }
        return toSpin.curMessage(curAngle);
    }

    private int uncoverPhrases() {
        c.getChar();
        return 0;
    }

    private boolean guessPhrase() {
        return false;
    }

    private boolean turn(int player) {
        String result = spinWheel();
        if (result.equals("broke")) {
            money[player] = 0;
        } else {
            int numUncovered = uncoverPhrases();
            money[player] += Integer.parseInt(result.substring(1)) * numUncovered;
            if (guessPhrase()) {
                money[player] += 20000;
                return true;
            }
        }
        return false;
    }

    private void round() {
        int curPlayer = curRound % 2;
        boolean guessedPhrase = false;
        while(!guessedPhrase){
            guessedPhrase = turn(curPlayer++);
        }
    }

    private void displayResults() {

    }

    private void writeScores() {

    }

    public void play() {
        loadPhrases();
        loadWheel();
        background.drawBackground();
        names = getNames();
        for(int i = 0; i < 3; i++){
            round();
        }
    }

    public static void main(String[] args) {
        Console c = new Console();
        Game g = new Game(c);
        g.play();
    }
}
