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
    Wheel curWheel;

    int curRound, curPlayer;
    boolean finalRound;
    int wheelAmt;
    String[] ansPhrase;
    String[] curPhrase;

    Background background;
    Font promptFont, smallPrompt, nameFont, phraseFont, letterFont;
    public static int MAX_NAME_LENGTH = 12;
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public Game(Console con) {
        c = con;
        background = new Background(c);
        promptFont = new Font("SansSerif", Font.PLAIN, 24);
        smallPrompt = new Font("Serif", Font.PLAIN, 14);
        nameFont = new Font("MonoSpaced", Font.BOLD, 140);
        phraseFont = new Font("MonoSpaced", Font.BOLD, 80);
        letterFont = new Font("MonoSpaced", Font.BOLD, 40);
        uncovered = "";
        money = new int[2];
    }

    private String[] getNames() {
        String[] playerNames = {"", ""};
        for (int i = 0; i < 2; i++) {
            background.drawBackground("GAME START", 340);
            c.setColor(Color.WHITE);
            c.setFont(promptFont);
            c.drawString("Player " + (i + 1) + ", please enter your name: ", 410, 300);
            c.drawString("Press <ENTER> to submit", 430, 780);
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
                c.fillRect(120, 350, 1040, 200);
                c.setFont(nameFont);
                c.setColor(Color.WHITE);
                c.drawString(playerNames[i], 130, 500);
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
                line = line.toUpperCase();
                phrases[curLine++] = split(line);
            }
        } catch (IOException e) {
            c.print(e.getMessage());
        }
    }

    private void drawStats() {
        c.setColor(Color.WHITE);
        c.setFont(promptFont);
        c.drawString(names[0], 120, 120);
        c.drawString("$" + money[0], 120, 160);
        c.drawString(names[1], 950, 120);
        c.drawString("$" + money[1], 950, 160);
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
        drawStats();
        curWheel.animate(0);
        c.setColor(new Color(0, 0, 150));
        c.fillOval(640 - 50, 450 - 50, 100, 100);
        c.setColor(Color.WHITE);
        c.setFont(promptFont);
        c.drawString("ENTER", 605, 460);
        c.setFont(smallPrompt);
        c.setColor(Color.WHITE);
        c.drawString(names[curPlayer] + ", Press <ENTER> to spin the wheel", 400, 795);
        while (c.getChar() != '\n') {
            new Message("Please press the <ENTER> key to spin the wheel");
        }
        pause(200);
        background.drawBackground();
        drawStats();

        int[] numTicks = {(int) (Math.random() * 75) + 100, (int) (Math.random() * 40) + 30, (int) (Math.random() * 50) + 50};
        int curAngle = 0;
        for (int i = 0; i < numTicks[0]; i++) {
            curWheel.animate(curAngle);
            pause(50);
            curAngle += 15;
        }
        for (int i = 0; i < numTicks[1]; i++) {
            curWheel.animate(curAngle);
            pause(50);
            curAngle += 7;
        }
        for (int i = 0; i < numTicks[2]; i++) {
            curWheel.animate(curAngle);
            pause(50);
            curAngle += 3;
        }
        curWheel.animate(curAngle);
        pause(400);
        String result = curWheel.curMessage(curAngle);
        background.drawBackground();
        drawStats();
        c.setFont(new Font("Serif", Font.BOLD, 200));
        for (int i = 0; i < 4; i++) {
            c.setColor(Color.BLACK);
            c.fillRect(390, 300, 700, 250);
            pause(300);
            c.setColor(Color.YELLOW);
            c.drawString(result, 400, 500);
            pause(300);
        }
        return result;
    }

    private void drawBlanks(String[] words) {
        int[] row = {400, 500, 600};
        int numPerRow = 10;
        int width = 900 / numPerRow;
        int margin = width + 5;
        int height = 90;
        int curRow = 0;
        int curCol = 0;
        c.setFont(phraseFont);
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() + curCol >= numPerRow) {
                curCol = 0;
                curRow++;
            }
            for (int let = 0; let < words[i].length(); let++) {
                if (words[i].charAt(let) == '_') {
                    c.setColor(Color.WHITE);
                    c.fillRoundRect(curCol * margin + 110, row[curRow] - height, width, height, 20, 20);
                } else {
                    c.setColor(Color.CYAN);
                    c.fillRoundRect(curCol * margin + 110, row[curRow] - height, width, height, 20, 20);
                    c.setColor(Color.BLACK);
                    c.drawString(words[i].substring(let, let + 1), curCol * margin + 130, row[curRow] - height / 4);
                }
                curCol++;
            }
            ++curCol;
        }
    }

    private void sortUncovered() {
        String sorted = "";
        for (int let = 0; let < ALPHABET.length(); let++) {
            if (uncovered.indexOf(ALPHABET.charAt(let)) != -1) {
                sorted += ALPHABET.charAt(let);
            }
        }
        uncovered = sorted;
    }

    private int uncoverPhrases() {
        background.drawBackground("LETTER GUESSING", 220);
        drawStats();
        drawBlanks(curPhrase);
        c.setFont(promptFont);
        c.setColor(Color.WHITE);
        c.drawString("Choose 3 letters to uncover, then press <ENTER>", 350, 700);
        sortUncovered();
        c.drawString("Uncovered: " + uncovered, 120, 780);
        c.setFont(promptFont);
        char[] toUncover = new char[3];
        int choosing = 0;
        char choice;
        while (((choice = c.getChar()) != '\n') || choosing < 3) {
            if (!((choice >= 'a' && choice <= 'z') || (choice >= 'A' && choice <= 'Z') || choice == '\b')) {
                new Message("Please pick a letter");
            } else if (choice == '\b') {
                if (choosing > 0) {
                    choosing--;
                }
            } else {
                if (choosing < 3) {
                    toUncover[choosing] = ("" + choice).toUpperCase().charAt(0);
                    choosing++;
                }
            }
            c.setColor(Color.BLACK);
            c.fillRect(1000, 750, 140, 50);
            c.setColor(Color.WHITE);
            int curX = 1100;
            for (int i = 0; i < choosing; i++) {
                c.drawString("" + toUncover[i], curX, 790);
                curX -= 20;
            }
        }
        for (int i = 0; i < toUncover.length; i++) {
            if (uncovered.indexOf(i) == -1) {
                uncovered = uncovered + toUncover[i];
            }
        }
        int newReveal = 0;
        for (int i = 0; i < curPhrase.length; i++) {
            for (int let = 0; let < curPhrase[i].length(); let++) {
                if (curPhrase[i].charAt(let) == '_' && uncovered.indexOf(ansPhrase[i].charAt(let)) != -1) {
                    String newWord = curPhrase[i].substring(0, let) + ansPhrase[i].charAt(let);
                    if (let + 1 < curPhrase[i].length()) newWord += curPhrase[i].substring(let + 1);
                    curPhrase[i] = newWord;
                    newReveal++;
                }
                drawBlanks(curPhrase);
                pause(100);
            }
        }
        return newReveal;
    }

    private boolean guessPhrase() {
        background.drawBackground("WHEEL OF FORTUNE", 200);
        drawStats();
        drawBlanks(curPhrase);
        String line = names[curPlayer] + ", test your luck, guess the phrase: ";
        c.setFont(promptFont);
        c.setColor(Color.WHITE);
        c.drawString(line, 250, 700);
        c.setFont(smallPrompt);
        c.drawString("Press <ENTER> to submit", 500, 790);
        c.setFont(promptFont);

        int promptLen = line.length();
        char ent;
        while ((ent = c.getChar()) != '\n') {
            if (ent == '\b') {
                if (line.length() > promptLen) {
                    line = line.substring(0, line.length() - 1);
                }
            } else if (ent >= 'a' && ent <= 'z' || ent >= 'A' && ent <= 'Z' || ent == ' ') {
                if (line.length() < 70) {
                    line = line + ent;
                }
            } else {
                new Message("Please enter a letter or <ENTER>");
            }
            c.setColor(Color.BLACK);
            c.fillRect(250, 650, 900, 60);
            c.setColor(Color.WHITE);
            c.drawString(line, 250, 700);
        }
        String[] words = split(line.toUpperCase().substring(promptLen));
        if (words.length != curPhrase.length) return false;
        for (int i = 0; i < curPhrase.length; i++) {
            if (!words[i].equals(curPhrase[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean turn() {
        String result = spinWheel();
        if (result.equals("broke")) {
            money[curPlayer] = 0;
        } else {
            int numUncovered = uncoverPhrases();
            background.drawBackground();
            drawStats();
            int amtGain = Integer.parseInt(result.substring(1)) * numUncovered;
            c.setColor(Color.BLACK);
            c.fillRect(200, 200, 800, 300);
            c.setFont(new Font("SansSerif", Font.BOLD, 60));
            c.setColor(Color.YELLOW);
            c.drawString(names[curPlayer] + " uncovered ", 210, 300);
            c.drawString(numUncovered + " letters, ", 210, 380);
            c.drawString("Gaining him $" + result.substring(1) + " x " + numUncovered + " = $" + amtGain + "!", 210, 460);

            c.setColor(Color.WHITE);
            c.setFont(smallPrompt);
            c.drawString("Press any key to continue", 400, 790);
            c.getChar();
            money[curPlayer] += amtGain;
            if (guessPhrase()) {
                money[curPlayer] += 20000;
                curPhrase = ansPhrase;
                background.drawBackground();
                drawStats();
                drawBlanks(curPhrase);
                c.setColor(Color.WHITE);
                c.setFont(promptFont);
                c.drawString("Correct! " + names[curPlayer] + " gains $20000!", 250, 700);
                c.setFont(smallPrompt);
                c.drawString("Press any key to continue", 400, 790);
                c.getChar();
                return true;
            } else {
                c.setColor(Color.BLACK);
                c.fillRect(200, 670, 950, 120);
                c.setColor(Color.WHITE);
                c.setFont(promptFont);
                c.drawString("Unfortunately, that is incorrect", 400, 700);
                c.setFont(smallPrompt);
                c.drawString("Press any key to continue", 400, 790);
                c.getChar();
            }
        }
        return false;
    }

    private void round() {
        curWheel = new Wheel(640, 450, 320, wheelParts, c);
        background.drawBackground();
        String toDisplay = "Round " + (curRound + 1);
        c.setFont(new Font("Serif", Font.BOLD, 200));
        c.setColor(Color.YELLOW);
        for(int i = 0; i < toDisplay.length(); i++){
            c.drawString(toDisplay.substring(0, i + 1), 300, 500);
            pause(300);
        }
        pause(1000);
        curPlayer = curRound % 2;
        ansPhrase = phrases[(int) (Math.random() * phrases.length)];
        curPhrase = new String[ansPhrase.length];
        for (int i = 0; i < ansPhrase.length; i++) {
            curPhrase[i] = "";
            for (int let = 0; let < ansPhrase[i].length(); let++) {
                curPhrase[i] += "_";
            }
        }
        boolean guessedPhrase = false;
        while (!guessedPhrase) {
            guessedPhrase = turn();
            curPlayer = (curPlayer + 1) % 2;
        }
    }

    public void play() {
        loadPhrases();
        loadWheel();
        background.drawBackground();
        names = getNames();
        for (int i = 0; i < 3; i++) {
            round();
        }
    }

    public static void main(String[] args) {
        // 1280 x 800
        Console c = new Console(40, 160);
        Game g = new Game(c);
        g.play();
    }
}
