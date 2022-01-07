/*
 * Shane Chen & Daniel Ye
 * Ms. Basaraba
 * January 7
 * Class for leaderboard entries (for sorting and organization)
 *
 */

public class LeaderboardEntry {

    // data --> public for ease of access
    public final String entryName;
    public final int score;

    // constructor
    public LeaderboardEntry(String eName, int eScore) {
        entryName = eName;
        score = eScore;
    }
}
