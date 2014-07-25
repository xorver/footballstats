package footballstats.core;

import footballstats.gui.LogReceiver;
import footballstats.statparser.StatParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tomasz Lichon on 06.07.14.
 */
public class FullRoster {

    private StatParser statParser;
    private Map<MatchId,Match> matchCache = new HashMap<>();

    public FullRoster(StatParser statParser) {
        this.statParser = statParser;
    }

    public void updateStats(){
        statParser.updateStats();
    }

    public List<String> getDayList(){
        return statParser.getDates();
    }

    public String dayMatches(String date, int pastMatches){
        StringBuilder builder = new StringBuilder();
        builder.append(date).append("\n");
        List<MatchId> matches = statParser.getDayMatches(date);
        LogReceiver.getInstance().info("Found "+matches.size() +" matches for "+date);
        int i=1;
        for(MatchId id : statParser.getDayMatches(date)){
            Match match;
            if(matchCache.containsKey(id))
                match = matchCache.get(id);
            else {
                match = statParser.getMatch(id);
                if(match.getTeam1().statEmpty())
                    continue;
                matchCache.put(id,match);
            }
            LogReceiver.getInstance().info("Got " +(i++) +"/"+matches.size()+" match");

            builder.append("\n");
            builder.append(String.format("Liga: %s\n", match.getLeague()));
            builder.append(String.format("%-40s %-10s %-10s %-10s\n", "Zespół", "Z", "P", "R"));
            builder.append(String.format("%-40s %-10s %-10s %-10s\n",
                    match.getTeam1().getName(),
                    match.getTeam1().getWin(pastMatches),
                    match.getTeam1().getLoose(pastMatches),
                    match.getTeam1().getDraw(pastMatches)
            ));
            builder.append(String.format("%-40s %-10s %-10s %-10s\n",
                    match.getTeam2().getName(),
                    match.getTeam2().getWin(pastMatches),
                    match.getTeam2().getLoose(pastMatches),
                    match.getTeam2().getDraw(pastMatches)
            ));
        }
        return builder.toString();
    }
}
