/*
* Shane Chen & Daniel Ye
* Ms. Basaraba
* January 7
* This is the Main class for the Wheel of Fortune simulation game, which interacts with all of the other classes and files in this directory.
 */

// imports
import hsa.Console;
import hsa.Message;

import java.awt.*;
import java.io.*;
// Main class
public class Main {
    Console c; // interface for drawing graphics and receiving input

    // leaderboard entries for the high scores window
    private static final int LEADERBOARD_ENTRIES_PER_PAGE = 10;

    // fonts for various parts of the program
    private static final Font TITLE_FONT = new Font("IMPACT", Font.PLAIN, 80);
    private static final Font SUBHEAD_FONT = new Font("SansSerif", Font.BOLD, 48);
    private static final Font SUBTITLE_FONT = new Font("Kristen ITC", Font.BOLD, 19);
    private static final Font HEADING_FONT = new Font("SansSerif", Font.BOLD, 16);
    private static final Font PARAGRAPH_FONT = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font PROMPT_FONT = new Font("Serif", Font.PLAIN, 19);
    private static final Font SMALL_PROMPT = new Font("Serif", Font.PLAIN, 14);

    // paths to various files
    private static final String SCORE_PATH = "data/scores.txt";
    private static final String INSTRUCTIONS_PATH = "data/instructions.txt";

    // stores lines of the instructions pages
    String[] instructions;
    LeaderboardEntry[] entries;
    private int entryCount;

    // background generator & icon generators
    Background background;
    IconDrawer icon;

    // whether or not cheat mode is on
    boolean cheatOn;

    // constructor for the main class
    public Main() {
        c = new Console(32, 128);
        // load data on object initialization (this pattern can be changed)
        try {
            loadInstructions();
            loadScores();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // add the IconDrawer object, allowing us to draw icons
        icon = new IconDrawer(c);

        // initialize the background
        background = new Background(c);

        cheatOn = false;
    } // Main class constructor

    private void loadInstructions() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(INSTRUCTIONS_PATH));
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
    } // loadInstructions method

    // clears the "keyboard buffer", so that a key entered in the past does not get registered
    private void clearBuffer(){
        // Console.isCharAvail: returns if there is a character available in the "keyboard buffer"
        // while there is a character in the buffer, read it and remove the buffer
        while(c.isCharAvail()){
            c.getChar();
        } // while loop for clearing the buffer
    } // clearBuffer method

    // displays the game name
    private void displayName() {
        // draw "WHEEL OF FORTUNE" at the top
        c.setColor(Color.YELLOW);
        c.setFont(TITLE_FONT);
        c.drawString("WHEEL OF FORTUNE", 208, 160);
    } // displayName method

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
            c.fillRect(240, 160, 560, 480);
            c.setColor(Color.WHITE);

            // display the instruction page
            c.setFont(SUBTITLE_FONT);
            c.drawString("Instructions - Page " + currentPage, 408, 232);

            // display instruction content
            c.setFont(PARAGRAPH_FONT);
            for (int i = (currentPage - 1) * linesPerPage, lineY = 290; i < (currentPage * linesPerPage); ++i, lineY += 20) {
                c.drawString(instructions[i], 330, lineY);
            }

            // display controls
            if (currentPage != 1) {
                c.drawString("<Q> previous page", 320, 540);
            }
            c.drawString("<ENTER> main menu", 460, 540);
            if (currentPage != 4) {
                c.drawString("<E> next page", 620, 540);
            }

            // get user input
            while (true) {
                clearBuffer();
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
                } else {
                    new Message("Press E, Q, or <ENTER>");
                }
            }
        }
    } // displayInstructions method

    // returns a string array of length [length] with empty elements
    private String[] emptyStrings(int length) {
        // initialize a string array
        String[] emptyStrings = new String[length];

        // fill it with blank strings
        for (int i = 0; i < emptyStrings.length; i++) {
            emptyStrings[i] = "";
        }

        // return the empty string array
        return emptyStrings;
    } // emptyStrings method

    // displays the title
    public void displayTitle() {
        // draw the background
        background.drawBackground();
        displayName();

        // draw the option to begin
        icon.drawButton("START", 390, 540, 240, 48, 48);
        icon.drawArrow(340, 560);

        // start a thread for the spinning wheel
        Wheel spinning = new Wheel(510, 340, 160, emptyStrings(10), c);
        Thread t = new Thread(spinning);
        t.start();

        // prompt and wait for the user to continue
        c.setFont(PROMPT_FONT);
        c.setColor(Color.WHITE);
        c.drawString("Press <ENTER> to select", 410, 630);
        clearBuffer();
        while (c.getChar() != '\n') {
            new Message("Please press <ENTER>");
        }

        // stop the wheel
        spinning.stop();

        // wait for the thread to die before continuing
        try {
            t.join();
        } catch (InterruptedException e) {
            c.print(e.getMessage());
        } // try/catch for wheel stopping
    } // displayTitle method

    // pauses the program for [millis] milliseconds
    private void pause(int millis) {
        // sleep for millis milliseconds
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            c.print(e.getMessage());
        } // try/catch for sleeping
    } // pause method

    // animates the splash screen
    public void displaySplashScreen() {
        // displays the splash screen
        Wheel middle = new Wheel(510, 320, 160, emptyStrings(10), c);

        // animate the background coming in through a for loop
        for (int i = 0; i <= 100; i += 5) {
            background.drawBackground(i);
            middle.animate(i);
            pause(50);
        } // for loop for background animation

        // let the wheel continue animating for a while
        for (int i = 100; i <= 300; i += 5) {
            middle.animate(i);
            pause(50);
        } // for loop for wheel spinning
    } // displaySplashScreen method

    // loads scores.txt into array, in format `NAME:SCORE`
    private void loadScores() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(SCORE_PATH));
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
            int score = Integer.parseInt(ln.substring(spl + 1));
            entries[ptr++] = new LeaderboardEntry(name, score);
        }
        entryCount = ptr;
    } // loadScores method

    private void sortScores() {
        while (true) {
            // simple bubble sort algorithm, reverse order
            // assume sorted
            boolean sorted = true;
            // check if sorted
            for (int i = 1; i < entryCount; ++i) {
                // if current entry is less than previous, it's not sorted
                if (entries[i].score > entries[i - 1].score) {
                    sorted = false;

                    // swap the two entries
                    LeaderboardEntry temp = entries[i];
                    entries[i] = entries[i - 1];
                    entries[i - 1] = temp;

                    // if entries are equal and the current string is lexicographically less than the previous, it's not sorted
                } else if (entries[i].score == entries[i - 1].score && entries[i].entryName.compareTo(entries[i - 1].entryName) < 0) {
                    sorted = false;

                    // swap the two entries
                    LeaderboardEntry temp = entries[i];
                    entries[i] = entries[i - 1];
                    entries[i - 1] = temp;
                }
            }
            if (sorted) return;
        }
    } // sortScores method

    private void displayScores() throws IOException {
        // refresh the background
        background.drawBackground();
        displayName();

        //reload scores
        loadScores();
        // sort scores
        sortScores();
        // get total pages
        int pages = entryCount / LEADERBOARD_ENTRIES_PER_PAGE;
        if (entryCount % LEADERBOARD_ENTRIES_PER_PAGE != 0) {
            pages++;
        }

        // current page, default to 1
        int currentPage = 1;

        // main display/ui loop
        while (true) {
            // clear previous listings
            c.setColor(Color.BLACK);
            c.fillRect(272, 180, 490, 440);
            c.setColor(Color.WHITE);

            // subtitle
            c.setFont(SUBTITLE_FONT);
            c.drawString("Leaderboard - Page " + currentPage, 410, 200);

            c.setColor(Color.GREEN);

            // borders
            c.drawLine(320, 240, 700, 240);
            c.drawLine(320, 272, 700, 272);
            c.drawLine(510, 230, 510, 530);

            // column heading
            c.setFont(HEADING_FONT);
            c.drawString("PLAYER", 340, 260);
            c.drawString("SCORE", 528, 264);

            // entries
            c.setFont(PARAGRAPH_FONT);
            int yCoordinate = 300;
            for (int index = (currentPage - 1) * LEADERBOARD_ENTRIES_PER_PAGE;
                 index < Math.min((currentPage - 1) * LEADERBOARD_ENTRIES_PER_PAGE + LEADERBOARD_ENTRIES_PER_PAGE, entryCount);
                 ++index) {
                c.drawString((index + 1) + ". " + entries[index].entryName, 335, yCoordinate);
                c.drawString("" + entries[index].score, 530, yCoordinate);
                yCoordinate += 24;
            }

            // controls
            // display controls
            if (currentPage > 1) {
                c.drawString("<Q> previous page", 320, 580);
            }
            c.drawString("<ENTER> main menu", 460, 575);
            if (currentPage < pages) {
                c.drawString("<E> next page", 630, 575);
            }

            c.setColor(Color.RED);
            if (entryCount > 0) c.drawString("<P> reset scores", 180, 330);


            // handle user input
            clearBuffer();
            char pressed = c.getChar();
            if (pressed == 10) { // leave screen
                return;
            } else if (pressed == 'e' || pressed == 'E') { // next page, if applicable
                if (currentPage < pages) {
                    currentPage++;
                }
            } else if (pressed == 'q' || pressed == 'Q') { // previous page, if applicable
                if (currentPage > 1) {
                    currentPage--;
                }
            } else if (pressed == 'p' || pressed == 'P') { // reset scores
                // warning
                c.drawString("Are you sure?", 170, 360);
                c.drawString("This cannot be undone.", 170, 380);
                c.drawString("Press P again to confirm", 170, 410);
                c.drawString("or any key to cancel", 170, 430);

                // get user confirmation
                char p = c.getChar();
                if (p == 'p' || p == 'P') {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(SCORE_PATH));
                    bw.write("");
                    bw.close();
                }

                // clear warning
                c.setColor(Color.BLACK);
                c.fillRect(160,300,200,300);

                // this is done if the user cancels
                loadScores();
                sortScores();
            }
            else {
                new Message("Press E, Q, P, or <ENTER>");
            }
        }
    } // displayScores method

    // draws a subheading
    private void drawSubheading(String title, int x){
        // set the color and font
        c.setColor(Color.WHITE);
        c.setFont(SUBHEAD_FONT);

        // fill a line above the subheading
        c.fillRect(240, 170, 545, 5);

        // draw the subheading
        c.drawString(title, x, 215);
    } // subHeading method

    // displays the main menu and returns what the user selected upon pressing <ENTER>
    public char mainMenu() {
        // draw the background with the subtitle "Main Menu"
        background.drawBackground();
        // display the game name
        displayName();
        drawSubheading("Main Menu", 400);

        // draw 4 buttons, one for each option
        icon.drawButton("LEADERBOARD", 350, 265, 320, 56, 6);
        icon.drawButton("INSTRUCTIONS", 350, 330, 320, 56, 8);
        icon.drawButton("PLAY", 350, 392, 320, 56, 110);
        icon.drawButton("QUIT", 350, 455, 320, 56, 110);
        if(cheatOn){
            icon.drawButton("CHEATS OFF", 350, 520, 320, 56, 50);
        } else {
            icon.drawButton("CHEATS ON", 350, 520, 320, 56, 50);
        }

        // prompt the user to press <ENTER> to select or use w/s to navigate the menu
        c.setFont(PROMPT_FONT);
        c.setColor(Color.WHITE);
        c.drawString("Press <ENTER> to select", 410, 630);
        c.setFont(SMALL_PROMPT);
        c.drawString("Press 'S' to move the arrow down, Press 'W' to move the arrow up", 320, 600);

        // the current position of the arrow
        int curPos = 0;

        // the character that was pressed
        char pressed;
        do {
            // draw the arrow at the current option (curPos)
            c.setColor(Color.BLACK);
            c.fillRect(160, 240, 190, 330);
            icon.drawArrow(295, 64 * curPos + 290);

            // get the user input
            clearBuffer();
            pressed = c.getChar();

            if (pressed == 'w') {
                // if it is 'w', then try to move the arrow up
                if (curPos > 0) {
                    curPos--;
                } // if statement for moving the arrow up
            } else if (pressed == 's') {
                // if it is 's', then try to move the arrow down
                if (curPos < 4) {
                    curPos++;
                } // if statement for moving the arrow down
            } else if (pressed != '\n'){
                // if the character was invalid, then inform the user of such
                new Message("Please press 'W', 'S', or <ENTER>");
            } // if/else block for handling user input
        } while (pressed != '\n'); // once the user presses <ENTER>, exit the loop

        // switch for what option the user selected based on the current arrow position
        switch (curPos) {
            case 0:
                // the top is "HIGH SCORE"
                return 'h';
            case 1:
                // the second is "INSTRUCTIONS"
                return 'i';
            case 2:
                // the third is "PLAY"
                return 's';
            case 3:
                // the fourth is "QUIT"
                return 'e';
            case 4:
                // this option toggles cheat mode
                cheatOn = !cheatOn;
                return 'k';
            default:
                // if none of the above options were used, then we return an arbitrary character (should never happen)
                return 'u';
        } // switch for handling arrow position
    } // mainMenu method

    public void play() {
        Game curGame = new Game(c, cheatOn);
        curGame.play();
    } // play method

    // run when the player exits
    public void goodbye(){
        // draw the background & the title
        background.drawBackground();
        displayName();
        c.setFont(PROMPT_FONT);

        // draw a thank you prompt for the user
        c.setColor(Color.WHITE);
        c.drawString("Thank you for playing Wheel of Fortune!", 160, 240);
        c.drawString("Wheel of Fortune was developed by Shane Chen and Daniel Ye", 160, 320);
        c.drawString("and Released on January 7, 2022.", 160, 360);
        c.drawString("We hope you enjoyed playing this simulator as much as we did coding it!", 160, 440);

        // wait for 2 seconds
        pause(2000);
    } // goodbye method

    public static void main(String[] args) throws IOException {
        // initialize the main class
        Main m = new Main();

        // display the title and splash screen
        m.displayTitle();
        m.displaySplashScreen();

        // while we do not wish to exit yet
        boolean toExit = false;
        while (!toExit) {
            // display the main method and get the user selected
            char choice = m.mainMenu();

            // based on the choice, we either exit, display the scores, start a game, or display the instructions
            switch (choice) {
                case 'e':
                    m.goodbye();
                    toExit = true;
                    break;
                case 'h':
                    m.displayScores();
                    break;
                case 's':
                    m.play();
                    break;
                case 'i':
                    m.displayInstructions();
                    break;
                case 'k':
                    // skips
                    break;
            } // user choice switch statement
        } // main while loop

        // fully exit the program
        System.exit(0);
    } // main method
} // Main class