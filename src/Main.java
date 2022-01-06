import hsa.Console;
import hsa.Message;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    Console c;

    private static final int LEADERBOARD_ENTRIES_PER_PAGE = 10;

    private static final Font TITLE_FONT = new Font("IMPACT", Font.BOLD, 100);
    private static final Font SUBHEAD_FONT = new Font("SansSerif", Font.BOLD, 60);
    private static final Font SUBTITLE_FONT = new Font("Kristen ITC", Font.BOLD, 24);
    private static final Font HEADING_FONT = new Font("SansSerif", Font.BOLD, 20);
    private static final Font PARAGRAPH_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font PROMPT_FONT = new Font("Serif", Font.PLAIN, 24);
    private static final Font SMALL_PROMPT = new Font("Serif", Font.PLAIN, 18);

    private static final String SCORE_PATH = "data/scores.txt";
    private static final String INSTRUCTIONS_PATH = "data/instructions.txt";

    IconDrawer icon;

    // stores lines of the instructions pages
    String[] instructions;
    LeaderboardEntry[] entries;
    private int entryCount;

    Background background;

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

        // add the IconDrawer object, allowing us to draw icons
        icon = new IconDrawer(c);

        // initialize the background
        background = new Background(c);
    }

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
    }

    // displays the game name
    private void displayName() {
        // draw "WHEEL OF FORTUNE" at the top
        c.setColor(Color.YELLOW);
        c.setFont(TITLE_FONT);
        c.drawString("WHEEL OF FORTUNE", 260, 200);
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
            c.fillRect(300, 200, 700, 600);
            c.setColor(Color.WHITE);

            // display the instruction page
            c.setFont(SUBTITLE_FONT);
            c.drawString("Instructions - Page " + currentPage, 510, 290);

            // display instruction content
            c.setFont(PARAGRAPH_FONT);
            for (int i = (currentPage - 1) * linesPerPage, lineY = 360; i < (currentPage * linesPerPage); ++i, lineY += 25) {
                c.drawString(instructions[i], 410, lineY);
            }

            // display controls
            if (currentPage != 1) {
                c.drawString("<Q> previous page", 400, 680);
            }
            c.drawString("<ENTER> main menu", 580, 680);
            if (currentPage != 4) {
                c.drawString("<E> next page", 780, 680);
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
        icon.drawButton("START", 490, 670, 300, 60, 60);
        icon.drawArrow(420, 700);

        // start a thread for the spinning wheel
        Wheel spinning = new Wheel(640, 430, 200, emptyStrings(10), c);
        Thread t = new Thread(spinning);
        t.start();

        // prompt and wait for the user to continue
        c.setFont(PROMPT_FONT);
        c.setColor(Color.WHITE);
        c.drawString("Press <ENTER> to select", 510, 790);
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
        Wheel middle = new Wheel(640, 400, 200, emptyStrings(10), c);

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
    }

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
    }

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
            c.fillRect(340, 240, 600, 550);
            c.setColor(Color.WHITE);

            // subtitle
            c.setFont(SUBTITLE_FONT);
            c.drawString("Leaderboard - Page " + currentPage, 510, 250);

            c.setColor(Color.GREEN);

            // borders
            c.drawLine(400, 300, 880, 300);
            c.drawLine(400, 340, 880, 340);
            c.drawLine(640, 290, 640, 660);

            // column heading
            c.setFont(HEADING_FONT);
            c.drawString("PLAYER", 420, 330);
            c.drawString("SCORE", 660, 330);

            // entries
            c.setFont(PARAGRAPH_FONT);
            int yCoordinate = 370;
            for (int index = (currentPage - 1) * LEADERBOARD_ENTRIES_PER_PAGE;
                 index < Math.min((currentPage - 1) * LEADERBOARD_ENTRIES_PER_PAGE + LEADERBOARD_ENTRIES_PER_PAGE, entryCount);
                 ++index) {
                c.drawString((index + 1) + ". " + entries[index].entryName, 420, yCoordinate);
                c.drawString("" + entries[index].score, 660, yCoordinate);
                yCoordinate += 30;
            }

            // controls
            // display controls
            if (currentPage > 1) {
                c.drawString("<Q> previous page", 400, 720);
            }
            c.drawString("<ENTER> main menu", 570, 720);
            if (currentPage < pages) {
                c.drawString("<E> next page", 790, 720);
            }

            // handle user input
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
            }
        }
    }

    // draws a subheading
    private void drawSubheading(String title, int x){
        // set the color and font
        c.setColor(Color.WHITE);
        c.setFont(SUBHEAD_FONT);

        // fill a line above the subheading
        c.fillRect(300, 210, 680, 5);

        // draw the subheading
        c.drawString(title, x, 270);
    } // subHeading method

    // displays the main menu and returns what the user selected upon pressing <ENTER>
    public char mainMenu() {
        // draw the background with the subtitle "Main Menu"
        background.drawBackground();
        // display the game name
        displayName();
        drawSubheading("Main Menu", 500);

        // draw 4 buttons, one for each option
        icon.drawButton("LEADERBOARD", 440, 350, 400, 70, 8);
        icon.drawButton("INSTRUCTIONS", 440, 430, 400, 70, 10);
        icon.drawButton("PLAY", 440, 510, 400, 70, 140);
        icon.drawButton("QUIT", 440, 590, 400, 70, 140);

        // prompt the user to press <ENTER> to select or use w/s to navigate the menu
        c.setFont(PROMPT_FONT);
        c.setColor(Color.WHITE);
        c.drawString("Press <ENTER> to select", 510, 790);
        c.setFont(SMALL_PROMPT);
        c.drawString("Press 'S' to move the arrow down, Press 'W' to move the arrow up", 400, 750);

        // the current position of the arrow
        int curPos = 0;

        // the character that was pressed
        char pressed;
        do {
            // draw the arrow at the current option (curPos)
            c.setColor(Color.BLACK);
            c.fillRect(200, 300, 239, 410);
            icon.drawArrow(370, 80 * curPos + 385);

            // get the user input
            pressed = c.getChar();

            if (pressed == 'w') {
                // if it is 'w', then try to move the arrow up
                if (curPos > 0) {
                    curPos--;
                } // if statement for moving the arrow up
            } else if (pressed == 's') {
                // if it is 's', then try to move the arrow down
                if (curPos < 3) {
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
            default:
                // if none of the above options were used, then we return an arbitrary character (should never happen)
                return 'u';
        } // switch for handling arrow position
    } // mainMenu method

    public void play() {
        Game curGame = new Game(c);
        curGame.play();
    }

    public static void main(String[] args) throws IOException {
        Main m = new Main();
        m.displayTitle();
        m.displaySplashScreen();
        boolean toExit = false;
        while (!toExit) {
            char choice = m.mainMenu();
            switch (choice) {
                case 'e':
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
            }
        }
        System.exit(0);
    }
}