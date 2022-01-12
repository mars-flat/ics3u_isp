/*
 * Shane Chen & Daniel Ye
 * Ms. Basaraba
 * January 1
 * This class is a component of Main.java - this simulates the ingame processes of Wheel of Fortune, void of menus and the like
 */


// imports
import hsa.Console;
import hsa.Message;
import java.awt.*;
import java.io.*;

// Game class
public class Game {
    private static final String PHRASE_PATH = "data/phrases.txt";
    private static final String WHEEL_PATH = "data/wheel.txt";
    private static final String FINAL_WHEEL_PATH = "data/final-wheel.txt";
    private static final String SCORE_PATH = "data/scores.txt";

    int[] money; // the amount of money players have
    Console c; // interface for graphics & input
    String uncovered; // the uncovered letters
    String[] names; // the names of the players
    String[][] phrases; // all of the phrases, where each element is a name
    String[] wheelParts; // strings to display on the wheel
    String[] finalWheelParts; // strings to display on the final wheel
    Wheel curWheel; // the current wheel

    int curRound, curPlayer; // the current round, the player whose turn it is right now
    boolean finalRound; // whether or not it is the final round
    String[] ansPhrase; // the randomly chosen phrase - each element is a word
    String[] curPhrase; // the phrase, with covered parts replaced with a '_' - each element is a word

    Background background; // the background
    Font promptFont, smallPrompt, nameFont, phraseFont, letterFont; // various fonts that are commonly used
    public static int MAX_NAME_LENGTH = 12; // the maximum length for a name
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // the alphabet

    // class constructor
    public Game(Console con) {
        // set the user interface (Console c)
        c = con;

        // initialize the background, various fonts, and the amount of money the players have
        background = new Background(c);
        promptFont = new Font("SansSerif", Font.PLAIN, 24);
        smallPrompt = new Font("Serif", Font.PLAIN, 14);
        nameFont = new Font("MonoSpaced", Font.BOLD, 140);
        phraseFont = new Font("MonoSpaced", Font.BOLD, 80);
        letterFont = new Font("MonoSpaced", Font.BOLD, 40);
        money = new int[2];
    }

    // get the names of the two players and returns an array with the names of the two players
    private String[] getNames() {
        // initialize the player names
        String[] playerNames = {"", ""};

        // repeat getting the name for both players
        for (int i = 0; i < 2; i++) {
            // draw a background with "GAME START"
            background.drawBackground("GAME START", 340);

            // prompt the i-th player to enter their name
            c.setColor(Color.WHITE);
            c.setFont(promptFont);
            c.drawString("Player " + (i + 1) + ", please enter your name: ", 410, 300);
            c.drawString("Press <ENTER> to submit", 430, 780);

            // the current character
            char cur;

            // while they have not pressed enter
            while ((cur = c.getChar()) != '\n') {
                // if they pressed backspace, remove a letter. Otherwise, add the letter they entered
                if (cur == '\b') {
                    if (playerNames[i].length() > 0) {
                        playerNames[i] = playerNames[i].substring(0, playerNames[i].length() - 1);
                    }
                } else {
                    playerNames[i] = playerNames[i] + cur;
                }

                // if the number of letters is over the maximum name length
                if (playerNames[i].length() > MAX_NAME_LENGTH) {
                    // tell the user to enter at most MAX_NAME_LENGTH letters and reset their name
                    new Message("Please enter a maximum of " + MAX_NAME_LENGTH + " characters");
                    playerNames[i] = "";
                }

                // make their names uppercase to stay consistent
                playerNames[i] = playerNames[i].toUpperCase();

                // redraw the current name
                c.setColor(Color.BLACK);
                c.fillRect(120, 350, 1040, 200);
                c.setFont(nameFont);
                c.setColor(Color.WHITE);
                c.drawString(playerNames[i], 130, 500);
            } // while they have not entered <ENTER>

            if (playerNames[i].equals("")) {
                i--;
            }
        } // for both players

        // return the player names
        return playerNames;
    } // getNames method

    // splits a string by a space
    private static String[] split(String toSplit) {
        // initialize the current index and the total number of spaces
        int startInd = 0, numSpaces = 0;

        // loop through the string, incrementing the number of spaces
        for (int i = 0; i < toSplit.length(); i++) {
            if (toSplit.charAt(i) == ' ') {
                numSpaces++;
            }
        }

        // an array of parts - having i spaces splits the string into i+1 parts
        String[] parts = new String[numSpaces + 1];

        // the current word we are calculating for
        int curInd = 0;
        // loop through the string
        for (int i = 0; i < toSplit.length(); i++) {
            // if the current character is a space
            if (toSplit.charAt(i) == ' ') {
                // set the current word equal to the substring from the first unprocessed letter to but not including this space
                // also updates curInd to the next index
                parts[curInd++] = toSplit.substring(startInd, i);

                // update startInd so that the first unprocessed letter is the next letter
                startInd = i + 1;
            } // if the current letter is a space
        } // for loop, loops through the string

        // we do not process the final part of the string since there is not space after it, so we add the final part here
        parts[curInd] = toSplit.substring(startInd);

        // return the parts
        return parts;
    } // split method

    // loads the phrases to use in the Wheel of Fortune game
    private void loadPhrases() {
        try {
            // initialize a BufferedReader
            BufferedReader lineReader = new BufferedReader(new FileReader(PHRASE_PATH));
            // the number of lines in phrases.txt
            int numLines = 0;
            // for each line, increment numLines
            while (lineReader.readLine() != null) {
                numLines++;
            }

            // the current line
            String line;
            phrases = new String[numLines][];

            // initialize a bufferedReader for reading the actual lines
            BufferedReader br = new BufferedReader(new FileReader(PHRASE_PATH));
            // the current line we are reading for
            int curLine = 0;
            // while the current line is not null (i.e, we have no reached the end of the file)
            while ((line = br.readLine()) != null) {
                // add the line - use uppercase to stay consistent
                line = line.toUpperCase();
                phrases[curLine++] = split(line);
            } // while loop for each line
        } catch (IOException e) {
            c.print(e.getMessage());
        } // try/catch for BufferedReader
    } // loadPhrases method

    // displays the statistics of the two players
    private void drawStats() {
        // set the color to white and use the normal prompt font
        c.setColor(Color.WHITE);
        c.setFont(promptFont);

        // display the first player & the amount of money they have
        c.drawString(names[0], 120, 120);
        c.drawString("$" + money[0], 120, 160);

        // display the second player & the amount of money they have
        c.drawString(names[1], 950, 120);
        c.drawString("$" + money[1], 950, 160);
    } // drawStats method

    // loads the phrases for te wheel
    private void loadWheel() {
        try {
            // declare and initialize a BufferedReader for wheel.txt
            BufferedReader br = new BufferedReader(new FileReader(WHEEL_PATH));

            // calculate the number of lines by looping through each line
            int numLines = 0;
            while (br.readLine() != null) {
                numLines++;
            }

            // initialize wheelParts to the number of lines
            wheelParts = new String[numLines];
            // reset the reader to the start of the file
            br = new BufferedReader(new FileReader(WHEEL_PATH));
            // read each line into wheelParts
            for (int i = 0; i < numLines; i++) {
                wheelParts[i] = br.readLine();
            }

            // initialize finalWheelParts to the number of lines
            finalWheelParts = new String[numLines];
            // reset the reader to read the file for the final wheel
            br = new BufferedReader(new FileReader(FINAL_WHEEL_PATH));
            // read each line into finalWheelParts
            for(int i = 0; i < numLines; i++){
                finalWheelParts[i] = br.readLine();
            }
        } catch (IOException e) {
            c.print(e.getMessage());
        } // try/catch for the BufferedReader reading text files for the wheel
    } // loadWheel method

    // pauses the program for [millis] milliseconds
    private void pause(int millis) {
        try {
            // sleep for [millis] milliseconds
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            c.print(e.getMessage());
        } // try/catch for Thread.Sleep
    } // pause method

    // spins the wheel and returns the section that it landed on
    private String spinWheel() {
        // draw the background & the current statistics
        background.drawBackground();
        drawStats();

        // animate the wheel at angle 0
        curWheel.animate(0);

        // draw circle for the <ENTER> icon at the center of the wheel
        c.setColor(new Color(0, 0, 150));
        c.fillOval(640 - 50, 450 - 50, 100, 100);

        // use a white colour in the normal prompt font
        c.setColor(Color.WHITE);
        c.setFont(promptFont);

        // draw the <ENTER> word in the icon at the center of the wheel
        c.drawString("ENTER", 605, 460);

        // in a small font, get the user to press <ENTER> to spin the wheel
        c.setFont(smallPrompt);
        c.setColor(Color.WHITE);
        c.drawString(names[curPlayer] + ", Press <ENTER> to spin the wheel", 400, 795);
        // if they press anything other than <ENTER>, prompt them to press <ENTER>
        while (c.getChar() != '\n');

        // pause for 200 milliseconds, then spin the wheel
        pause(200);
        // redraw the background & statistics
        background.drawBackground();
        drawStats();

        // the amount of time to spin the wheel fast, then medium, then slow to simulate a wheel slowing down
        int[] numTicks = {(int) (Math.random() * 75) + 100, (int) (Math.random() * 40) + 30, (int) (Math.random() * 50) + 50};
        // the current angle the wheel is at
        int curAngle = 0;
        // spin the wheel for numTicks[0] frames, each time drawing the wheel and incrementing the angle by a lot
        for (int i = 0; i < numTicks[0]; i++) {
            curWheel.animate(curAngle);
            pause(50);
            curAngle += 15;
        } // for loop for the first part of spinning the wheel

        // spin the wheel for numTicks[1] frames, each time drawing the wheel and incrementing the angle by a medium amount
        for (int i = 0; i < numTicks[1]; i++) {
            curWheel.animate(curAngle);
            pause(50);
            curAngle += 7;
        } // for loop for the second part of spinning the wheel

        // spin the wheel for numTicks[2] frames, each time drawing the wheel and incrementing the angle by a small amount
        for (int i = 0; i < numTicks[2]; i++) {
            curWheel.animate(curAngle);
            pause(50);
            curAngle += 3;
        } // for loop for the third part of spinning the wheel

        // animate the final position of the wheel
        curWheel.animate(curAngle);

        // pause for 400 seconds to let the user see the wheel
        pause(400);

        // get the section that the wheel landed on
        String result = curWheel.curMessage(curAngle);

        // redraw the background & set the font to a large Serif font
        background.drawBackground();
        drawStats();
        c.setFont(new Font("Serif", Font.BOLD, 200));

        // have the phrase telling the user what result they got flash 4 times
        for (int i = 0; i < 4; i++) {
            // erase the phrase and pause
            c.setColor(Color.BLACK);
            c.fillRect(390, 300, 700, 250);
            pause(300);

            // draw the phrase in a big yellow font, then pause
            c.setColor(Color.YELLOW);
            c.drawString(result, 400, 500);
            pause(300);
        } // for loop for flashing the phrase

        // return the result
        return result;
    } // spinWheel method

    // draws the blanks and uncovered parts of the phrase - white rectangles if it has not been uncovered and blue rectangles if it has
    private void drawBlanks(String[] words) {
        // the rows in which we can display phrase
        int[] row = {400, 500, 600, 700, 800};
        // the number of characters to display per row
        int numPerRow = 12;
        // the resulting width of each character
        int width = 900 / numPerRow;
        // the increments in which to display the character - this adds a margin between each character
        int margin = width + 5;
        // the height of rectangle
        int height = 90;
        // the current row and column to display the character
        int curRow = 0;
        int curCol = 0;

        // set the font to the font for displaying a phrase
        c.setFont(phraseFont);
        for (int i = 0; i < words.length; i++) {
            // try to draw the current word - if it ends up going past the number of characters per row, then wrap to the next row
            if (words[i].length() + curCol >= numPerRow) {
                curCol = 0;
                curRow++;
            } // if statement for going to the next row

            // for each letter in the current word
            for (int let = 0; let < words[i].length(); let++) {
                if (words[i].charAt(let) == '_') {
                    // if it is a blank, then draw a white rounded rectangle in its place
                    c.setColor(Color.WHITE);
                    c.fillRoundRect(curCol * margin + 110, row[curRow] - height, width, height, 20, 20);
                } else {
                    // if it is not a blank, draw a cyan rectangle along with the current letter
                    c.setColor(Color.CYAN);
                    c.fillRoundRect(curCol * margin + 110, row[curRow] - height, width, height, 20, 20);
                    c.setColor(Color.BLACK);
                    c.drawString(words[i].substring(let, let + 1), curCol * margin + 130, row[curRow] - height / 4);
                } // if/else for if the current letter is a blank

                // go to the next position for the next character
                curCol++;
            } // for loop for drawing each letter of the current word
            // draw the space for the current word by skipping the current character
            ++curCol;
        } // for loop for drawing the phrase
    } // drawBlanks method

    // sort the letters in [uncovered] so that they are in increasing order
    private void sortUncovered() {
        // a string in sorted order
        String sorted = "";

        // for each letter from the first to the last
        for (int let = 0; let < ALPHABET.length(); let++) {
            // if the letter exists in [uncovered], add it to sorted
            if (uncovered.indexOf(ALPHABET.charAt(let)) != -1) {
                sorted += ALPHABET.charAt(let);
            } // if the current letter is in [uncovered]
        } // for loop for adding each letter

        // set uncovered equal to its sorted counterpart
        uncovered = sorted;
    } // sortUncovered method

    // uncovers the phrase one letter at a time, returning the number of letters that were uncovered
    private int uncoverPhrases() {
        // draw the background & the statistics
        background.drawBackground("LETTER GUESSING", 220);
        drawStats();

        // draw the blanks for the current phrase
        drawBlanks(curPhrase);

        // prompt the user in a white font to choose 3 letters to uncover
        c.setFont(promptFont);
        c.setColor(Color.WHITE);
        c.drawString("Choose 3 letters to uncover, then press <ENTER>", 350, 700);

        // sort the currently uncovered phrases and display it to the user
        sortUncovered();
        c.drawString("Uncovered: " + uncovered, 120, 780);

        // array of the 3 uncovered letters
        char[] toUncover = new char[3];
        // the character that we are choosing for right now
        int choosing = 0;
        // the current choice
        char choice;
        // while they have not pressed enter or they haven't chosen 3 letters yet
        while (((choice = c.getChar()) != '\n') || choosing < 3) {
            if (!((choice >= 'a' && choice <= 'z') || (choice >= 'A' && choice <= 'Z') || choice == '\b')) {
                // if they didn't enter a letter, nor used the backspace, tell them to choose a letter
                new Message("Please pick a letter");
            } else if (choice == '\b') {
                // if they pressed the backspace, erase a letter
                if (choosing > 0) {
                    choosing--;
                } // if statement for erasing a letter if it exists
            } else {
                // if there are less than 3 letters, then add it
                if (choosing < 3) {
                    // converts the character [choice] into uppercase
                    toUncover[choosing] = ("" + choice).toUpperCase().charAt(0);

                    // we now choose the next letter
                    choosing++;
                } // if statement for adding the current letter if there are less than 3
            } // if/else if/else block for the character the user entered

            // clear the bottom right of the screen
            c.setColor(Color.BLACK);
            c.fillRect(1000, 750, 140, 50);

            // sets the color to white
            c.setColor(Color.WHITE);

            // display the letters that the user has chosen so far
            int curX = 1100; // the current x coordinate to display the letter at
            // for each of the letters the user has chosen - iterate from the last to first letter as we write from right to left
            for (int i = choosing - 1; i >= 0; i--) {
                // display the current letter
                c.drawString("" + toUncover[i], curX, 790);
                // update the x coordinate to display the letter at
                curX -= 20;
            } // for loop for displaying each letter
        } // while loop for getting 3 letters from the user

        // for each letter to uncover
        for (int i = 0; i < toUncover.length; i++) {
            // if uncovered doesn't have the current letter
            if (uncovered.indexOf(toUncover[i]) == -1) {
                // then add the current letter
                uncovered = uncovered + toUncover[i];
            } // if statement for adding each letter to uncover
        } // for loop for uncovering letters

        // number of letters that were revealed
        int newReveal = 0;
        // iterate through each word, and each letter in each word
        for (int i = 0; i < curPhrase.length; i++) {
            for (int let = 0; let < curPhrase[i].length(); let++) {
                // if the current character is a blank and [uncovered] contains the corresponding character in the answer
                if (curPhrase[i].charAt(let) == '_' && uncovered.indexOf(ansPhrase[i].charAt(let)) != -1) {
                    // then uncover the letter

                    // newWord is the same as curPhrase[i], but with the current letter uncovered
                    String newWord = curPhrase[i].substring(0, let) + ansPhrase[i].charAt(let);
                    // add the rest of the letters in the current word
                    if (let + 1 < curPhrase[i].length()) newWord += curPhrase[i].substring(let + 1);
                    // replace the current phrase with newWord, equivalent of replacing the current blank with its corresponding letter
                    curPhrase[i] = newWord;

                    // increment the number of letters that were revealed
                    newReveal++;
                } // if statement for uncovering the current blank

                // redraw the blanks & uncovered phrases
                drawBlanks(curPhrase);

                // wait 0.1 seconds before moving on to the next character
                pause(100);
            } // for loop - loops through each character in a word
        } // for loop - loops through each word in the current phrase

        // return the number of letters that were revealed
        return newReveal;
    } // uncoverPhrases

    // prompts the user to guess the phrases - returns whether or not the guessed phrase is correct
    private boolean guessPhrase() {
        // draw the background && player statistics
        background.drawBackground("WHEEL OF FORTUNE", 200);
        drawStats();

        // draw the blanks for the current phrase
        drawBlanks(curPhrase);

        // prompt the current user to guess the phrase
        String line = names[curPlayer] + ", test your luck, guess the phrase: ";
        c.setFont(promptFont);
        c.setColor(Color.WHITE);
        c.drawString(line, 250, 700);

        // inform the user to use <ENTER> to submit the phrase
        c.setFont(smallPrompt);
        c.drawString("Press <ENTER> to submit", 500, 790);

        // continually the text console for what the user entered
        c.setFont(promptFont);
        // the portion of the line that is dedicated to prompting the user
        int promptLen = line.length();

        // the entered character
        char ent;
        // while the entered character is not <ENTER>
        while ((ent = c.getChar()) != '\n') {
            // if the entered character is backspace
            if (ent == '\b') {
                // remove a letter, making sure not to remove the prompt
                if (line.length() > promptLen) {
                    // update the line without the last character
                    line = line.substring(0, line.length() - 1);
                } // if statement for removing a letter
            } else if (ent >= 'a' && ent <= 'z' || ent >= 'A' && ent <= 'Z' || ent == ' ') {
                // if the entered character is a letter

                // if the length of the line is not too big,
                if (line.length() < 70) {
                    // add the letter
                    line = line + ent;
                } // if statement for adding a letter
            } else {
                // inform the user that their input was invalid
                new Message("Please enter a letter or <ENTER>");
            } // if/else if/else block for different types of user input

            // redraw the current line
            c.setColor(Color.BLACK);
            c.fillRect(250, 650, 900, 60);
            c.setColor(Color.WHITE);
            c.drawString(line, 250, 700);
        } // while loop for continually getting user input

        // split the words the user entered
        String[] words = split(line.toUpperCase().substring(promptLen));
        // if the number of words aren't equal, there is not way they are equal
        if (words.length != ansPhrase.length) return false;
        // loop through each word
        for (int i = 0; i < ansPhrase.length; i++) {
            // if they aren't equal, ignoring case, then return false
            if (!words[i].toUpperCase().equals(ansPhrase[i].toUpperCase())) {
                return false;
            } // if statement for if they aren't equal
        } // for loop for each word

        // if we pass the for loop, each word is equal - return true
        return true;
    } // guessPhrase method

    // plays one normal turn for a player - is not a turn for the final round. Returns if the player guessed the phrase
    private void turn() {
        // spin the wheel
        String result = spinWheel();
        if (result.equals("broke")) {
            // if the result is "broke", then they lose all their money and we end the round
            money[curPlayer] = 0;
        } else {
            // otherwise, uncover 3 letters
            int numUncovered = uncoverPhrases();

            // draw the background & statistics
            background.drawBackground();
            drawStats();

            // calculate the amount of money they gained from uncovering letters
            int amtGain = Integer.parseInt(result.substring(1)) * numUncovered;

            // inform the users of how much the current user made
            c.setColor(Color.BLACK);
            c.fillRect(200, 200, 800, 300);
            c.setFont(new Font("SansSerif", Font.BOLD, 60));
            c.setColor(Color.YELLOW);
            c.drawString(names[curPlayer] + " uncovered ", 210, 300);
            c.drawString(numUncovered + " letters, ", 210, 380);
            c.drawString("Gaining them $" + result.substring(1) + " x " + numUncovered + " = $" + amtGain + "!", 210, 460);

            // prompt the user to continue by pressing a key
            c.setColor(Color.WHITE);
            c.setFont(smallPrompt);
            c.drawString("Press any key to continue", 400, 790);
            c.getChar();

            // give the current player [amtGain] dollars
            money[curPlayer] += amtGain;

            // if they successfully guess the phrase
            if (guessPhrase()) {
                // give the player $20000
                money[curPlayer] += 20000;

                // uncover all of the blanks by setting the current phrase equal to ansPhrase
                curPhrase = ansPhrase;

                // redraw the background & draw the blanks (all of the letters should be uncovered at this point)
                background.drawBackground();
                drawStats();
                drawBlanks(curPhrase);

                // draw the prompt, informing the users that their guess is correct
                c.setColor(Color.WHITE);
                c.setFont(promptFont);
                c.drawString("Correct! " + names[curPlayer] + " gains $20000!", 250, 700);

                // prompt the user to continue & return true
                c.setFont(smallPrompt);
                c.drawString("Press any key to continue", 400, 790);
                c.getChar();
            } else {
                // redraw the bottom part of the screen
                c.setColor(Color.BLACK);
                c.fillRect(200, 670, 950, 120);

                // informt he user that their guess was incorrect
                c.setColor(Color.WHITE);
                c.setFont(promptFont);
                c.drawString("Unfortunately, that is incorrect", 400, 700);
                c.setFont(smallPrompt);
                c.drawString("Press any key to continue", 400, 790);
                c.getChar();
            } // if/else block for whether or not the user guessed the phrase
        } // if/else for whether or not the spinner landed on "broke"
    } // turn method

    // plays a round
    private void round() {
        // reset the uncovered letters
        uncovered = "";

        // draw the background and statistics
        background.drawBackground();
        drawStats();

        // the String to display to inform the users of the current round
        String roundDisplay;
        // the x coordinate to display the string at
        int displayX;

        if(!finalRound){
            // if it is not the final round, simply display "Round [current round]"
            roundDisplay = "Round " + (curRound + 1);
            displayX = 300;
        } else {
            // if it is the final round, display "Final Round"
            roundDisplay = "Final Round";
            displayX = 110;
        } // if/else block for the current round display

        // display the current round, one letter at a time
        c.setFont(new Font("Serif", Font.BOLD, 200));
        c.setColor(Color.YELLOW);

        // for each letter in string to display
        for(int i = 0; i < roundDisplay.length(); i++){
            // draw the substring up to the current letter
            c.drawString(roundDisplay.substring(0, i + 1), displayX, 500);
            // pause for 0.3 seconds before displaying the next letter
            pause(300);
        } // for loop for displaying each letter in roundDisplay

        // pause for a second
        pause(1000);

        // calculate the phrase for the users to guess
        ansPhrase = phrases[(int) (Math.random() * phrases.length)];

        // initialize the current phrase
        curPhrase = new String[ansPhrase.length];
        // for each word in ansPhrase
        for (int i = 0; i < ansPhrase.length; i++) {
            // initialize the current word
            curPhrase[i] = "";
            // add an underscore for each letter in the corresponding word in ansPhrase
            for (int let = 0; let < ansPhrase[i].length(); let++) {
                curPhrase[i] += "_";
            } // for loop for adding underscores (blanks) to curPhrase
        } // for loop for adding blanks to curPhrase

        // if it isn't the final round
        if(!finalRound){
            // initialize the wheel to a normal wheel
            curWheel = new Wheel(640, 450, 320, wheelParts, c);

            // calculate the starting player
            curPlayer = curRound % 2;

            boolean allUncovered = false; // whether or not the user has guessed the phrase
            // while the phrase has not been guessed or completely uncovered
            while (!allUncovered) {
                // run the current turn
                turn();

                allUncovered = true;
                // loop for each word, seeing if there is a blank - if so, then allUncovered is also
                for(int i = 0; i < curPhrase.length && allUncovered; i++){
                    allUncovered = curPhrase[i].indexOf('_') == -1;
                }

                // update the current player
                curPlayer = (curPlayer + 1) % 2;
            } // while loop for running each turn
        } else {
            // otherwise, we run the final round
            // calculate whose turn it is by which player has more money
            if(money[0] > money[1]){
                curPlayer = 0;
            } else {
                curPlayer = 1;
            } // if/else block for calculating the current player

            // initialize the wheel to the final wheel parts
            curWheel = new Wheel(640, 450, 320, finalWheelParts, c);

            // get the result from spinning the wheel
            String result = spinWheel();

            // calculate the prize amount
            int prizeAmt;

            // if the result is "winner" - the user has a chance to win a million dollars
            if(result.equals("winner")){
                prizeAmt = 1000000;
            } else {
                // otherwise, the prize is the amount listed on the wheel
                prizeAmt = Integer.parseInt(result.substring(1));
            } // if/else block for calculating the prize amount

            // uncover 3 letters from the player
            uncoverPhrases();

            // if the user guesses the phrase
            if(guessPhrase()){
                // we give the current player prizeAmt dollars
                money[curPlayer] += prizeAmt;

                // redraw the background and statistics
                background.drawBackground();
                drawStats();

                // inform the users that the current player has guessed right and won $prizeAmt
                c.setFont(new Font("Serif", Font.BOLD, 100));
                c.setColor(Color.YELLOW);
                c.drawString(names[curPlayer] + " has won", 200, 450);
                c.drawString("$" + prizeAmt, 200, 560);

                // prompt the user to press a key to continue
                c.setColor(Color.WHITE);
                c.setFont(smallPrompt);
                c.drawString("Press any key to continue", 400, 790);
                c.getChar();
            } else {
                // inform the user that their guess was incorrect, and prompt them to continue
                c.setColor(Color.BLACK);
                c.fillRect(200, 670, 950, 120);
                c.setColor(Color.WHITE);
                c.setFont(promptFont);
                c.drawString("Unfortunately, that is incorrect", 400, 700);
                c.setFont(smallPrompt);
                c.drawString("Press any key to continue", 400, 790);
                c.getChar();
            } // if/else block for whether or not the user guessed the phrase
        } // if/else block for weather or not this is the final round
    } // round method

    // draws a person
    private void drawPerson(int x, int y, int width, int height, boolean winner, String name, int money){
        // draw their name above the person
        c.setColor(Color.WHITE);
        c.setFont(new Font("Serif", Font.BOLD, 24));
        c.drawString(name, x, y - 10);

        // display the amount of money the current player made
        c.drawString("$" + money, x + width / 4, y + height + 30);

        // fill a semi-oval for the body
        c.setColor(new Color(66,133,244));
        c.fillArc(x, y + width / 4, width, 2 * (height - width / 4), 0, 180);

        // fill an oval for the head
        c.setColor(new Color(255, 209, 102));
        c.fillOval(x + width / 4, y, width / 2, width / 2);

        // the the player is a winner, draw a gold medal - otherwise, draw a silver medal
        if(winner) c.setColor(Color.YELLOW);
        else c.setColor(Color.GRAY);
        c.fillOval(x + width / 2 - 30, y + height / 2, 60, 60);
    } // drawPerson method

    // displays the winner
    private void displayWinner(){
        // draw the background
        background.drawBackground();

        // calculate the winner based on who has more money
        int winner;
        if(money[0] > money[1]) {
            winner = 0;
        } else {
            winner = 1;
        } // if/else for calculating the winner

        // display the winner higher than the loser
        if(winner == 0){
            drawPerson(130, 250, 250, 300, true, names[0], money[0]);
            drawPerson(890, 380, 250, 300, false, names[1], money[1]);
        } else {
            drawPerson(130, 380, 250, 300, false, names[0], money[0]);
            drawPerson(890, 250, 250, 300, true, names[1], money[1]);
        } // if/else block for displaying the two people

        // display congratulations to the winner & inform them of how much money they made
        c.setFont(new Font("Serif", Font.BOLD, 60));
        c.setColor(Color.YELLOW);
        c.drawString("Congratulations " + names[winner] + "", 150, 170);
        c.drawString("for winning $" + money[winner] + "!", 150, 775);

        // get the user to press any key to continue
        c.setFont(smallPrompt);
        c.setColor(Color.WHITE);
        c.drawString("Press any key to continue", 800, 790);

        // initialize a string array of empty strings
        String[] emptyParts = new String[15];
        for(int i = 0; i < emptyParts.length; i++){
            emptyParts[i] = "";
        } // for loop for filling the emptyParts string array with blank strings

        // initialize a wheel with blank strings and animate the wheel in a separate thread
        Wheel emptyWheel = new Wheel(640, 500, 150, emptyParts, c);
        Thread wheelAnimate = new Thread(emptyWheel);
        wheelAnimate.start();

        // wait for input
        c.getChar();

        // stop the wheel and wait for the thread to die before continuing
        emptyWheel.stop();
        try{
            wheelAnimate.join();
        } catch(InterruptedException e){
            c.print(e.getMessage());
        } // try/catch for thread join
    } // displayWinner method

    // appends the scores to src/data/scores.txt
    private void writeScores(){
        try{
            // open an appending printwriter to src/data/scores.txt
            PrintWriter pw = new PrintWriter(new FileWriter(SCORE_PATH, true));
            // append two lines, one for each player
            pw.println(names[0] + ":" + money[0]);
            pw.println(names[1] + ":" + money[1]);
            pw.close();
        } catch(IOException e){
            c.print(e.getMessage());
        } // try/catch for writing new scores
    } // writeScores method

    // player method
    public void play() {
        // load the phrases and the text for the wheel
        loadPhrases();
        loadWheel();

        // draw the background and get the player names
        background.drawBackground();
        names = getNames();

        // player 3 normal rounds
        finalRound = false;
        for (int i = 0; i < 3; i++) {
            // play a round and increment the current round counter
            round();
            curRound++;
        } // for loop for 3 normal rounds

        // play one final round
        finalRound = true;
        round();

        // write the scores
        writeScores();

        // display the winner
        displayWinner();
    } // play method

    // TODO remove testing
    // TESTING ----------------------------------------------------------------------------------------------------------------------------
    public static void main(String[] args) {
        // 1280 x 800
        Console c = new Console(40, 160);
        Game g = new Game(c);
        g.play();
    } // testing main method
} // Game class