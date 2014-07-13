package footballstats.core;

import footballstats.statparser.StatParser;

import java.util.List;

/**
 * Created by Tomasz Lichon on 06.07.14.
 */
public class FullRoster {

    private StatParser statParser;

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
        for(MatchId id : statParser.getDayMatches(date)){
            Match match = statParser.getMatch(id);
            builder.append("\n");
            builder.append("\t").append("Liga: ").append(match.getLeague()).append("\n");
            builder.append("\t").append("Zespół\t\tZwycięstwa\t\tPorażki\t\tRemisy\n");
            builder.append("\t").append(match.getTeam1().getName()).append("\t\t")
                    .append(match.getTeam1().getWin(pastMatches)).append("\t\t")
                    .append(match.getTeam1().getLoose(pastMatches)).append("\t\t")
                    .append(match.getTeam1().getDraw(pastMatches)).append("\t\t\n");
            builder.append("\t").append(match.getTeam2().getName()).append("\t\t")
                    .append(match.getTeam2().getWin(pastMatches)).append("\t\t")
                    .append(match.getTeam2().getLoose(pastMatches)).append("\t\t")
                    .append(match.getTeam2().getDraw(pastMatches)).append("\t\t\n");
        }
        return builder.toString();
    }
}
