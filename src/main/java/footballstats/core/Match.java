package footballstats.core;

/**
 * Created by Tomasz Lichon on 06.07.14.
 */
public class Match {
    private String league;
    private String date;
    private String time;
    private Team team1;
    private Team team2;

    public Match(String league, String date, String time, Team team1, Team team2) {
        this.league = league;
        this.date = date;
        this.time = time;
        this.team1 = team1;
        this.team2 = team2;
    }

    public String getLeague() {
        return league;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Team getTeam1() {
        return team1;
    }

    public Team getTeam2() {
        return team2;
    }
}
