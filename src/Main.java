import hsa.Console;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    Console c;

    public Main() {
        c = new Console();
    }

    // displays the instructions for the game
    // requires multiple pages
    public void displayInstructions() throws IOException {
        // lines per page constant
        int linesPerPage = 10;
        // preprocessing
        // BufferedReader instance
        BufferedReader br = new BufferedReader(new FileReader("data/instructions.txt"));
        // number of lines in the instructions page
        String[] instructions = new String[50];
        // pointer to the index
        int ptr = 0;
        // variable to store read line
        String ln;
        // read until EOF
        while ((ln = br.readLine()) != null) {
            instructions[ptr++] = ln;
        }
        // TODO: insert background

        int currentPage = 1;

        // main instruction loop
        while (true) {
            c.setColor(Color.WHITE);
            c.fillRect(120,80,430,300);
            c.setColor(Color.BLACK);
            // display the page
            c.drawString("Instructions - Page " + currentPage, 270, 100);
            for (int i = (currentPage - 1) * linesPerPage, lineY = 130; i < (currentPage * linesPerPage); ++i, lineY += 20) {
                c.drawString(instructions[i], 150, lineY);
            }
            // display controls
            if (currentPage != 1) {
                c.drawString("<Q> previous page", 150, 350);
            }
            c.drawString("<ENTER> main menu", 270, 350);
            if (currentPage != 4) {
                c.drawString("<E> next page", 400, 350);
            }

            // get user input
            while (true) {
                char input = c.getChar();
                if (input == 10) {
                    return;
                } else if (input == 'q' || input == 'Q') {
                    if (currentPage > 1) {
                        currentPage--;
                    }
                    break;
                } else if (input == 'e' || input == 'E') {
                    if (currentPage < 4) {
                        currentPage++;
                    }
                    break;
                }
            }
        }
    }

    public void displayTitle() {

    }

    public void displaySplashScreen() {

    }

    private void loadPhrases() {

    }

    private void loadScores() {

    }

    private void displayHighScore() {

    }

    public void play() {

    }

    public static void main(String[] args) throws IOException {
        Main m = new Main();
        m.displayInstructions();
        System.exit(0);
    }
}
