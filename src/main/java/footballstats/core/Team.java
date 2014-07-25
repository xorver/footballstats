package footballstats.core;

import java.util.List;

/**
 * Created by Tomasz Lichon on 06.07.14.
 */
public class Team {
    private String name;
    private List<Score> recentScores;

    public Team(String name, List<Score> recentScores) {
        this.name = name;
        this.recentScores = recentScores;
    }

    public String getName() {
        return name;
    }

    public int getWin(int pastMatches) {
        int wins=0;
        for( Score s : recentScores.subList(0,pastMatches > recentScores.size() ? recentScores.size() : pastMatches))
            if(s.ourTeam > s.awayTeam)
                wins++;
        return wins;
    }

    public int getLoose(int pastMatches) {
        int loose=0;
        for( Score s : recentScores.subList(0,pastMatches > recentScores.size() ? recentScores.size() : pastMatches))
            if(s.ourTeam < s.awayTeam)
                loose++;
        return loose;
    }

    public int getDraw(int pastMatches) {
        int draws=0;
        for( Score s : recentScores.subList(0,pastMatches > recentScores.size() ? recentScores.size() : pastMatches))
            if(s.ourTeam == s.awayTeam)
                draws++;
        return draws;
    }

    public boolean statEmpty() {
        return recentScores.isEmpty();
    }
}
