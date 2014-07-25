package footballstats.statparser;

import footballstats.core.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tomasz Lichon on 06.07.14.
 */
public class AnnabetStatParser implements StatParser {

    private final String ALL_MATCHES_URL = "http://annabet.com/pl/soccerstats/upcoming/";
    private final String TEAM_URL = "http://annabet.com/pl/soccerstats/h2h.php?team1={{id}}&team2=1";
    private final String TEAMS_URL = "http://annabet.com/pl/soccerstats/h2h.php?team1={{id1}}&team2={{id2}}";

    private final String MATCH_REGEX = "<a [^>]*><img [^>]*> ([^<]+)</a></td><td><a.*?href=\"/pl/.*?/h2h.php\\?team1=(\\d+)&team2=(\\d+)\">";
    private final String DATE_TIME_REGEX = "<td>(\\d+\\.\\d+)\\. (\\d+:\\d+)</td>";
    private final String FULL_REGEX = MATCH_REGEX + ".*?" + DATE_TIME_REGEX;

    private final String TEAM_NAME_REGEX = "<title>(.*?) -";
    private final String SCORE_REGEX = "<b>(\\d+) - (\\d+)\\*?</b></a> .*?<td.*?>(.*?)</td>";

    private final String TEAMS_NAME_REGEX = "<title>(.*?) - (.*?)( :|,)";
    private final String SCORES_REGEX = "title=\"([^<]*?) - ([^<]*?) (Ø|\")[^<]*?<b>(\\d+) - (\\d+)";

    private HttpClientAdapter clientAdapter;
    private String fullPage;

    public AnnabetStatParser(HttpClientAdapter clientAdapter) {
        this.clientAdapter = clientAdapter;

        updateStats();
    }

    @Override
    public void updateStats() {
        fullPage = clientAdapter.doGetRequestByProxy(ALL_MATCHES_URL);
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
            if(id.team1Id.equals(matcher.group(2)) && id.team2Id.equals(matcher.group(3))) {
                TeamPair pair = getTeams(id.team1Id,id.team2Id);
                return new Match(matcher.group(1), matcher.group(4), matcher.group(5), pair.t1, pair.t2);
            }
        return null;
    }

    //todo repair http://annabet.com/pl/soccerstats/h2h.php?team1=232&team2=3061
    public TeamPair getTeams(String id1, String id2) {
        String teamPage = clientAdapter.doGetRequestByProxy(TEAMS_URL.replace("{{id1}}", id1).replace("{{id2}}",id2));
        Matcher nameMatcher = Pattern.compile(TEAMS_NAME_REGEX).matcher(teamPage);
        String team1Name = "cannot_parse";
        String team2Name = "cannot_parse";
        if(nameMatcher.find()) {
            team1Name = nameMatcher.group(1);
            team2Name = nameMatcher.group(2);
        }

        Pattern pattern = Pattern.compile(SCORES_REGEX);
        Matcher matcher = pattern.matcher(teamPage);

        List<Score> scores1 = new ArrayList<>();
        List<Score> scores2 = new ArrayList<>();
        int n=18;
        while(matcher.find() && n-->0)
            if(team1Name.equals(matcher.group(1)))
                scores1.add(new Score(Integer.valueOf(matcher.group(4)), Integer.valueOf(matcher.group(5))));
            else if(team1Name.equals(matcher.group(2)))
                scores1.add(new Score(Integer.valueOf(matcher.group(5)), Integer.valueOf(matcher.group(4))));
            else if(team2Name.equals(matcher.group(1)))
                scores2.add(new Score(Integer.valueOf(matcher.group(4)), Integer.valueOf(matcher.group(5))));
            else if(team2Name.equals(matcher.group(2)))
                scores2.add(new Score(Integer.valueOf(matcher.group(5)), Integer.valueOf(matcher.group(4))));
        return new TeamPair(new Team(team1Name,scores1), new Team(team2Name,scores2));
    }
    @Override
    public Team getTeam(String id) {
        String teamPage = clientAdapter.doGetRequestByProxy(TEAM_URL.replace("{{id}}", id));
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

    private class TeamPair{
        public Team t1;
        public Team t2;

        private TeamPair(Team t1, Team t2) {
            this.t1 = t1;
            this.t2 = t2;
        }
    }
}
