import hsa.Console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Game {
    int[] money;
    Console c;
    char[] uncovered;
    String[] name;
    String[][] phrases;

    private String[] getNames(){
        return null;
    }

    private static String[] split(String toSplit){
        int startInd=0, numSpaces=0;
        for(int i = 0; i < toSplit.length(); i++){
            if(toSplit.charAt(i) == ' '){
                numSpaces++;
            }
        }
        String[] parts = new String[numSpaces + 1];
        int curInd = 0;
        for(int i = 0; i < toSplit.length(); i++){
            if(toSplit.charAt(i) == ' '){
                parts[curInd++] = toSplit.substring(startInd, i);
                startInd = i + 1;
            }
        }
        parts[curInd] = toSplit.substring(startInd);
        return parts;
    }

    private void loadPhrase(){
        try {
            BufferedReader lineReader = new BufferedReader(new FileReader("Data/phrases.txt"));
            int numLines = 0;
            String line;
            while((line=lineReader.readLine()) != null){
                numLines++;
            }
            BufferedReader br = new BufferedReader(new FileReader("Data/phrases.txt"));
            phrases = new String[numLines][];
            int curLine = 0;
            while((line=br.readLine()) != null){
                phrases[curLine++] = split(line);
            }
        } catch (IOException e){
            c.print(e.getMessage());
        }
    }

    private String spinWheel(){
        return null;
    }

    private void uncoverPhrases(){

    }

    private boolean guessPhrase(){
        return false;
    }

    private String wheelTurn(){
        return null;
    }

    private void turn(int player){

    }

    private void round(boolean finalRound){

    }

    private void displayResults(){

    }

    private void writeScores(){

    }

    public void play(){

    }

    public static void main(String[] args){

    }
}
