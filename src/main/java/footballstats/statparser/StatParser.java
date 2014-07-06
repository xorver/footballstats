package footballstats.statparser;

import footballstats.core.DayRoster;
import footballstats.core.Match;
import footballstats.core.Team;

import java.util.Date;
import java.util.List;

/**
 * Created by Tomasz Lichon on 06.07.14.
 */
public interface StatParser {

    void updateStats();
    List<Date> getDates();
    DayRoster getDayRoster(Date date);
    Match getMatch(String id);
    Team getTeam(String id);
}
