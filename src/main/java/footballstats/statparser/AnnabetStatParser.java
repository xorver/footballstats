package footballstats.statparser;

import footballstats.core.DayRoster;
import footballstats.core.Match;
import footballstats.core.Team;

import java.util.Date;
import java.util.List;

/**
 * Created by Tomasz Lichon on 06.07.14.
 */
public class AnnabetStatParser implements StatParser {

    private HttpClientAdapter clientAdapter;

    public AnnabetStatParser(HttpClientAdapter clientAdapter) {
        this.clientAdapter = clientAdapter;
    }

    @Override
    public void updateStats() {

    }

    @Override
    public List<Date> getDates() {
        return null;
    }

    @Override
    public DayRoster getDayRoster(Date date) {
        return null;
    }

    @Override
    public Match getMatch(String id) {
        return null;
    }

    @Override
    public Team getTeam(String id) {
        return null;
    }
}
