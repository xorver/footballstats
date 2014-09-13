package footballstats.statparser;

import footballstats.core.Match;
import footballstats.core.MatchId;
import footballstats.core.Score;
import footballstats.core.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tomasz Lichon on 06.07.14.
 */
public class AnnabetStatParser implements StatParser {

    private final String FOOTBALL_MATCHES_URL = "http://annabet.com/pl/soccerstats/upcoming/";
    private final String FOOTBALL_TEAM_URL = "http://annabet.com/pl/soccerstats/h2h.php?team1={{id}}&team2=1";
    private final String FOOTBALL_TEAMS_URL = "http://annabet.com/pl/soccerstats/h2h.php?team1={{id1}}&team2={{id2}}";

    private final String HOCKEY_MATCHES_URL = "http://annabet.com/pl/hockeystats/upcoming/";
    private final String HOCKEY_TEAM_URL = "http://annabet.com/pl/hockeystats/h2h.php?team1={{id}}&team2=1";
    private final String HOCKEY_TEAMS_URL = "http://annabet.com/pl/hockeystats/h2h.php?team1={{id1}}&team2={{id2}}";

    private final String MATCH_REGEX = "<a [^>]*><img [^>]*> ([^<]+)</a></td><td><a.*?href=\"/pl/.*?/h2h.php\\?team1=(\\d+)&team2=(\\d+)\">";
    private final String DATE_TIME_REGEX = "<td>(\\d+\\.\\d+)\\. (\\d+:\\d+)</td>";
    private final String FULL_REGEX = DATE_TIME_REGEX + ".*?" + MATCH_REGEX;

    private final String TEAM_NAME_REGEX = "<title>(.*?) -";
    private final String SCORE_REGEX = "<b>(\\d+) - (\\d+)\\*?</b></a> .*?<td.*?>(.*?)</td>";

    private final String TEAMS_NAME_REGEX = "<title>(.*?) - (.*?)( :|,)";
    private final String SCORES_REGEX = "title=\"([^<]*?) - ([^<]*?) (O |Ø |\")[^<]*?<b>(\\d+) - (\\d+)(d)?";

    private String teamUrl;
    private String teamsUrl;
    private String allMatchesUrl;
    private HttpClientAdapter clientAdapter;
    private String fullPage;

    public AnnabetStatParser(HttpClientAdapter clientAdapter,SportType type) {
        this.clientAdapter = clientAdapter;
        if(type == SportType.FOOTBALL) {
            this.allMatchesUrl = FOOTBALL_MATCHES_URL;
            this.teamUrl = FOOTBALL_TEAM_URL;
            this.teamsUrl = FOOTBALL_TEAMS_URL;
        }
        else if(type == SportType.HOCKEY) {
            this.allMatchesUrl = HOCKEY_MATCHES_URL;
            this.teamUrl = HOCKEY_TEAM_URL;
            this.teamsUrl = HOCKEY_TEAMS_URL;
        }
        updateStats();
    }

    @Override
    public void updateStats() {
        fullPage = clientAdapter.doGetRequestByProxy(allMatchesUrl);
    }

    @Override
    public List<String> getDates() {
        List<String> dates = new ArrayList<>();
        Pattern pattern = Pattern.compile(DATE_TIME_REGEX);
        Matcher matcher = pattern.matcher(fullPage);
        while(matcher.find())
            if(!dates.contains(matcher.group(1)))
                dates.add(matcher.group(1));

        return dates;
    }

    @Override
    public List<MatchId> getDayMatches(String date) {
        this.updateStats();
        List<MatchId> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile(FULL_REGEX,Pattern.DOTALL);
        Matcher matcher = pattern.matcher(fullPage);
        while(matcher.find())
            if(date.equals(matcher.group(1)))
                matches.add(new MatchId(matcher.group(4),matcher.group(5)));
        return matches;
    }

    @Override
    public Match getMatch(MatchId id) {
        Pattern pattern = Pattern.compile(FULL_REGEX,Pattern.DOTALL);
        Matcher matcher = pattern.matcher(fullPage);
        while(matcher.find())
            if(id.team1Id.equals(matcher.group(4)) && id.team2Id.equals(matcher.group(5))) {
                TeamPair pair = getTeams(id.team1Id,id.team2Id);
                return new Match(matcher.group(3), matcher.group(1), matcher.group(2), pair.t1, pair.t2);
            }
        return null;
    }

    public TeamPair getTeams(String id1, String id2) {
        String teamPage = clientAdapter.doGetRequestByProxy(teamsUrl.replace("{{id1}}", id1).replace("{{id2}}", id2));
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
                scores1.add(new Score(Integer.valueOf(matcher.group(4)), matcher.group(6) != null ? Integer.valueOf(matcher.group(4)) : Integer.valueOf(matcher.group(5)) ));
            else if(team1Name.equals(matcher.group(2)))
                scores1.add(new Score(Integer.valueOf(matcher.group(5)), matcher.group(6) != null ? Integer.valueOf(matcher.group(5)) : Integer.valueOf(matcher.group(4))));
            else if(team2Name.equals(matcher.group(1)))
                scores2.add(new Score(Integer.valueOf(matcher.group(4)), matcher.group(6) != null ? Integer.valueOf(matcher.group(4)) : Integer.valueOf(matcher.group(5))));
            else if(team2Name.equals(matcher.group(2)))
                scores2.add(new Score(Integer.valueOf(matcher.group(5)), matcher.group(6) != null ? Integer.valueOf(matcher.group(5)) : Integer.valueOf(matcher.group(4))));
        return new TeamPair(new Team(team1Name,scores1), new Team(team2Name,scores2));
    }
    @Override
    public Team getTeam(String id) {
        String teamPage = clientAdapter.doGetRequestByProxy(teamUrl.replace("{{id}}", id));
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
