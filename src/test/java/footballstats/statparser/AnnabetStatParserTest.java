package footballstats.statparser;

import footballstats.core.Match;
import footballstats.core.MatchId;
import footballstats.core.Team;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class AnnabetStatParserTest {
    private String team1Id = "5130";
    private String team2Id = "5132";
    @Mock private HttpClientAdapter httpClientMock;
    private StatParser underTest;



    @org.junit.Before
    public void setUp() throws Exception {
        String mainPageData = getResourceData("/AnnabetMainPageData");
        String team5130Data = getResourceData("/AnnabetTeam5130Data");
        String team5132Data = getResourceData("/AnnabetTeam5132Data");
        String team5130And5132Data = getResourceData("/AnnabetTeam5130And5132Data");

        MockitoAnnotations.initMocks(this);
        Mockito.when(httpClientMock.doGetRequestByProxy("http://annabet.com/pl/soccerstats/upcoming/")).thenReturn(mainPageData);
        Mockito.when(httpClientMock.doGetRequestByProxy("http://annabet.com/pl/soccerstats/h2h.php?team1=5130&team2=1")).thenReturn(team5130Data);
        Mockito.when(httpClientMock.doGetRequestByProxy("http://annabet.com/pl/soccerstats/h2h.php?team1=5132&team2=1")).thenReturn(team5132Data);
        Mockito.when(httpClientMock.doGetRequestByProxy("http://annabet.com/pl/soccerstats/h2h.php?team1=5130&team2=5132")).thenReturn(team5130And5132Data);

        underTest = new AnnabetStatParser(httpClientMock, SportType.FOOTBALL);
    }

    @org.junit.Test
    public void testUpdateStats() throws Exception {
        assertTrue(0 < underTest.getDates().size());
        Mockito.when(httpClientMock.doGetRequestByProxy("http://annabet.com/pl/soccerstats/upcoming/")).thenReturn("");

        underTest.updateStats();

        assertTrue(0 == underTest.getDates().size());
    }

    @org.junit.Test
    public void testGetDates() throws Exception {

        List<String> dates = underTest.getDates();

        assertEquals(21, dates.size());
        assertTrue(dates.contains("6.7"));
        assertTrue(dates.contains("16.7"));
        assertTrue(dates.contains("30.7"));
    }

    @org.junit.Test
    public void testGetDayMatches() throws Exception {
        MatchId id = new MatchId(team1Id,team2Id);

        List<MatchId> matches6_7 = underTest.getDayMatches("6.7");
        List<MatchId> matches7_7 = underTest.getDayMatches("7.7");

        assertTrue(matches6_7.contains(id));
        assertFalse(matches7_7.contains(id));
    }

    @org.junit.Test
    public void testGetMatch() throws Exception {
        MatchId id = new MatchId(team1Id,team2Id);

        Match match = underTest.getMatch(id);

        assertEquals("Mecze towarzyskie", match.getLeague());
        assertEquals("6.7", match.getDate());
        assertEquals("13:00", match.getTime());
        assertEquals("Avangard Kursk", match.getTeam1().getName());
        assertEquals("Dynamo Bryansk", match.getTeam2().getName());
    }

    @org.junit.Test
    public void testGetTeam() throws Exception {

        Team team = underTest.getTeam(team1Id);

        assertEquals("Avangard Kursk",team.getName());
        assertEquals(4,team.getWin(5));
        assertEquals(1,team.getLoose(5));
        assertEquals(0,team.getDraw(5));
    }

    private String getResourceData(String resourceName) throws FileNotFoundException {
        return new Scanner(new File(getClass().getResource(resourceName).getFile()),"utf-8").useDelimiter("\\A").next();
    }

}