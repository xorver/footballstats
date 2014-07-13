package footballstats.statparser;

import footballstats.core.Match;
import footballstats.core.MatchId;
import footballstats.core.Team;

import java.util.Date;
import java.util.List;

/**
 * Created by Tomasz Lichon on 06.07.14.
 */
public interface StatParser {

    void updateStats();
    List<String> getDates();
    List<MatchId> getDayMatches(String date);
    Match getMatch(MatchId id);
    Team getTeam(String id);
}
