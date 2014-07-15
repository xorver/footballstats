package footballstats.statparser;

import footballstats.core.*;
import sun.misc.Regexp;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tomasz Lichon on 06.07.14.
 */
public class AnnabetStatParser implements StatParser {

    private final String ALL_MATCHES_URL = "http://annabet.com/pl/soccerstats/upcoming/";
    private final String TEAM_URL = "http://annabet.com/pl/soccerstats/h2h.php?team1={{id}}&team2=1";
//    private final String TEAM_URL = "https://bucharest-s05-i01-traffic.cyberghostvpn.com/go/browse.php?u=http%3A%2F%2Fannabet.com%2Fpl%2Fsoccerstats%2Fh2h.php%3Fteam1%3D4%26team2%3D1&b=7&f=norefer";
//    private final String TEAM_URL = "http://2anonymousproxy.com/browse.php?u=http%3A%2F%2Fannabet.com%2Fpl%2Fsoccerstats%2Fh2h.php%3Fteam1%3D{{id}}%26team2%3D1&b=4&f=norefer";

    private final String MATCH_REGEX = "<a [^>]*><img [^>]*> ([^<]+)</a></td><td><a.*?href=\"/pl/.*?/h2h.php\\?team1=(\\d+)&team2=(\\d+)\">";
    private final String DATE_TIME_REGEX = "<td>(\\d+\\.\\d+)\\. (\\d+:\\d+)</td>";
    private final String FULL_REGEX = MATCH_REGEX + ".*?" + DATE_TIME_REGEX;

    private final String TEAM_NAME_REGEX = "<title>(.*?) -";
    private final String SCORE_REGEX = "<b>(\\d+) - (\\d+)\\*?</b></a> .*?<td.*?>(.*?)</td>";

    private HttpClientAdapter clientAdapter;
    private String fullPage;

    public AnnabetStatParser(HttpClientAdapter clientAdapter) {
        this.clientAdapter = clientAdapter;

        updateStats();
    }

    @Override
    public void updateStats() {

        fullPage = clientAdapter.doGetRequest(ALL_MATCHES_URL);
    }

    @Override
    public List<String> getDates() {
        HashSet<String> dates = new HashSet<>();
        Pattern pattern = Pattern.compile(DATE_TIME_REGEX);
        Matcher matcher = pattern.matcher(fullPage);
        while(matcher.find())
            dates.add(matcher.group(1));

        return new ArrayList<>(dates);
    }

    @Override
    public List<MatchId> getDayMatches(String date) {
        List<MatchId> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile(FULL_REGEX,Pattern.DOTALL);
        Matcher matcher = pattern.matcher(fullPage);
        while(matcher.find())
            if(date.equals(matcher.group(4)))
                matches.add(new MatchId(matcher.group(2),matcher.group(3)));
        return matches;
    }

    @Override
    public Match getMatch(MatchId id) {
        Pattern pattern = Pattern.compile(FULL_REGEX,Pattern.DOTALL);
        Matcher matcher = pattern.matcher(fullPage);
        while(matcher.find())
            if(id.team1Id.equals(matcher.group(2)) && id.team2Id.equals(matcher.group(3)))
                return new Match(matcher.group(1),matcher.group(4),matcher.group(5), getTeam(id.team1Id), getTeam(id.team2Id));
        return null;
    }

    @Override
    public Team getTeam(String id) {
        String teamPage = clientAdapter.doGetRequest(TEAM_URL.replace("{{id}}",id));
        Matcher nameMatcher = Pattern.compile(TEAM_NAME_REGEX).matcher(teamPage);
        String teamName = "cannot_parse";
        if(nameMatcher.find())
            teamName = nameMatcher.group(1);

        Pattern pattern = Pattern.compile(SCORE_REGEX);
        Matcher matcher = pattern.matcher(teamPage);

        List<Score> scores = new ArrayList<>();
        while(matcher.find())
            if(teamName.equals(matcher.group(3)))
                scores.add(new Score(Integer.valueOf(matcher.group(2)), Integer.valueOf(matcher.group(1))));
            else
                scores.add(new Score(Integer.valueOf(matcher.group(1)), Integer.valueOf(matcher.group(2))));
        return new Team(teamName,scores);
    }
}
