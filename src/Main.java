import hsa.Console;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    Console c;

    private static final Font TITLE_FONT = new Font("IMPACT", Font.BOLD, 50);
    private static final Font SUBTITLE_FONT = new Font("Kristen ITC", Font.BOLD, 24);
    private static final Font PARAGRAPH_FONT = new Font("SansSerif", Font.PLAIN, 14);

    // stores lines of the instructions pages
    String[] instructions;
    LeaderboardEntry[] entries;
    private int entryCount;

    // constructor for the main class
    public Main() {
        c = new Console(40, 160);
        // load data on object initialization (this pattern can be changed)
        try {
            loadInstructions();
            loadScores();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadInstructions() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("data/instructions.txt"));
        // number of lines in the instructions page
        instructions = new String[50];
        // pointer to the index
        int ptr = 0;
        // variable to store read line
        String ln;
        // read until EOF
        while ((ln = br.readLine()) != null) {
            instructions[ptr++] = ln;
        }
    }

    // displays the instructions for the game
    // requires multiple pages
    public void displayInstructions() {
        // lines per page constant
        int linesPerPage = 10;

        // current page
        int currentPage = 1;

        // main instruction loop
        while (true) {
            // clear previously displayed instructions
            c.setColor(Color.BLACK);
            c.fillRect(350,200,600,500);
            c.setColor(Color.WHITE);

            // display the instruction page
            c.setFont(SUBTITLE_FONT);
            c.drawString("Instructions - Page " + currentPage, 510, 250);

            // display instruction content
            c.setFont(PARAGRAPH_FONT);
            for (int i = (currentPage - 1) * linesPerPage, lineY = 320; i < (currentPage * linesPerPage); ++i, lineY += 25) {
                c.drawString(instructions[i], 410, lineY);
            }

            // display controls
            if (currentPage != 1) {
                c.drawString("<Q> previous page", 400, 640);
            }
            c.drawString("<ENTER> main menu", 580, 640);
            if (currentPage != 4) {
                c.drawString("<E> next page", 780, 640);
            }

            // get user input
            while (true) {
                char input = c.getChar();
                if (input == 10) { // leave instructions
                    return;
                } else if (input == 'q' || input == 'Q') { // next page if applicable
                    if (currentPage > 1) {
                        currentPage--;
                    }
                    break;
                } else if (input == 'e' || input == 'E') { // previous page if applicable
                    if (currentPage < 4) {
                        currentPage++;
                    }
                    break;
                }
            }
        }
    }

    public void displayTitle() {
        // draw the background
        Background b = new Background(c);
        b.drawBackground();

        // draw the title
        c.setColor(Color.YELLOW);
        c.setFont(TITLE_FONT);
        c.drawString("WHEEL OF FORTUNE", 430, 160);
    }

    public void displaySplashScreen() {

    }

    private void loadPhrases() {

    }

    // loads scores.txt into array, in format `NAME:SCORE`
    private void loadScores() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("data/scores.txt"));
        // number of lines in the instructions page
        entries = new LeaderboardEntry[1000];
        // pointer to the index
        int ptr = 0;
        // variable to store read line
        String ln;
        // read until EOF
        while ((ln = br.readLine()) != null) {
            int spl = ln.indexOf(":");
            String name = ln.substring(0, spl);
            int score = Integer.parseInt(ln.substring(spl+1));
            entries[ptr++] = new LeaderboardEntry(name, score);
        }
        entryCount = ptr;
        System.out.println(entryCount);
    }

    private void sortScores() {
        while (true) {
            // simple bubble sort algorithm
            // assume sorted
            boolean sorted = true;
            // check if sorted
            for (int i = 1; i < entries.length; ++i) {
                // if current entry is less than previous, it's not sorted
                if (entries[i].score < entries[i-1].score) {
                    sorted = false;

                    // swap the two entries
                    LeaderboardEntry temp = entries[i];
                    entries[i] = entries[i-1];
                    entries[i-1] = temp;

                    // if entries are equal and the current string is lexicographically less than the previous, it's not sorted
                } else if (entries[i].score == entries[i-1].score && entries[i].entryName.compareTo(entries[i-1].entryName) > 0) {
                    sorted = false;

                    // swap the two entries
                    LeaderboardEntry temp = entries[i];
                    entries[i] = entries[i-1];
                    entries[i-1] = temp;
                }
            }
            if (sorted) return;
        }
    }

    private void displayScores() {

    }

    public void play() {

    }

    public static void main(String[] args) throws IOException {
        Main m = new Main();
        m.displayTitle();
        m.displayInstructions();
        System.exit(0);
    }
}
