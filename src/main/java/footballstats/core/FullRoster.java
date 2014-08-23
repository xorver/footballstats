package footballstats.core;

import footballstats.gui.LogReceiver;
import footballstats.statparser.StatParser;

import java.util.*;

/**
 * Created by Tomasz Lichon on 06.07.14.
 */
public class FullRoster {

    private StatParser statParser;
    private Map<MatchId,Match> matchCache = new HashMap<>();
    private Map<String,List<Match>> matchByLeague = new HashMap<>();

    public FullRoster(StatParser statParser) {
        this.statParser = statParser;
    }


    public List<String> getDayList(){
        return statParser.getDates();
    }

    @SuppressWarnings("unchecked")
    public String dayMatches(String date, int pastMatches){
        matchByLeague.clear();
        StringBuilder builder = new StringBuilder();
        List<MatchId> matches = statParser.getDayMatches(date);
        LogReceiver.getInstance().info("Found "+matches.size() +" matches for "+date);
        int i=1;
        for(MatchId id : statParser.getDayMatches(date)) {
            Match match;
            if (matchCache.containsKey(id))
                match = matchCache.get(id);
            else {
                match = statParser.getMatch(id);
                if (match.getTeam1().statEmpty())
                    continue;
                matchCache.put(id, match);
            }
            LogReceiver.getInstance().info("Got " + (i++) + "/" + matches.size() + " match");

            if(matchByLeague.containsKey(match.getLeague()))
                matchByLeague.get(match.getLeague()).add(match);
            else{
                List<Match> list = new ArrayList<>();
                list.add(match);
                matchByLeague.put(match.getLeague(),list);
            }
        }
        builder.append(String.format("%-40s %-10s %-10s %-10s\n", "Zespół", "Z", "P", "R"));

        List<Map.Entry<String,List<Match> >> leaguesInAlphabeticalOrder = new ArrayList(matchByLeague.entrySet());
        Collections.sort(leaguesInAlphabeticalOrder, (o1, o2) -> (o1.getKey().compareTo( o2.getKey() )) );
        for(Map.Entry<String,List<Match> > entry : leaguesInAlphabeticalOrder) {
            for(Match match : entry.getValue()) {
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
        }
        LogReceiver.getInstance().info("Stat generation finished");

        return builder.toString();
    }
}
