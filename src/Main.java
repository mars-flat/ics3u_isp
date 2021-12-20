import hsa.Console;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    Console c;
    String[] phrases;

    public Main() {
        c = new Console(30, 120);
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
            c.fillRect(80,80,600,300);
            c.setColor(Color.BLACK);
            // display the page
            c.drawString("Instructions - Page " + currentPage, 200, 100);
            for (int i = (currentPage - 1) * linesPerPage, lineY = 130; i < (currentPage * linesPerPage); ++i, lineY += 20) {
                c.drawString(instructions[i], 200, lineY);
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
    }
}
