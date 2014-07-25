package footballstats.core;

/**
 * Created by Tomasz Lichon on 11.07.14.
 */
public class MatchId {
    public String team1Id;
    public String team2Id;

    public MatchId(String team1Id, String team2Id) {
        this.team1Id = team1Id;
        this.team2Id = team2Id;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MatchId)
            return team1Id.equals(((MatchId) obj).team1Id) && team2Id.equals(((MatchId) obj).team2Id);
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(team1Id);
    }
}
