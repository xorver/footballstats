package footballstats.statparser;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static junit.framework.TestCase.assertEquals;

public class AnnabetStatParserTest {
    private String team1Id = "5130";
    private String team2Id = "5132";
    @Mock private HttpClientAdapter httpClientMock;
    private AnnabetStatParser underTest;



    @org.junit.Before
    public void setUp() throws Exception {
        String mainPageData = getResourceData("AnnabetMainPageData");
        String team5130Data = getResourceData("AnnabetTeam5130Data");
        String team5132Data = getResourceData("AnnabetTeam5132Data");

        MockitoAnnotations.initMocks(this);
        Mockito.when(httpClientMock.doGetRequest("http://annabet.com/pl/soccerstats/upcoming/")).thenReturn(mainPageData);
        Mockito.when(httpClientMock.doGetRequest("http://annabet.com/pl/soccerstats/h2h.php?team1=5130&team2=1")).thenReturn(team5130Data);
        Mockito.when(httpClientMock.doGetRequest("http://annabet.com/pl/soccerstats/h2h.php?team1=5132&team2=1")).thenReturn(team5132Data);

        underTest = new AnnabetStatParser(httpClientMock);
    }

    @org.junit.Test
    public void testUpdateStats() throws Exception {

    }

    @org.junit.Test
    public void testGetDates() throws Exception {

    }

    @org.junit.Test
    public void testGetDayRoster() throws Exception {

    }

    @org.junit.Test
    public void testGetMatch() throws Exception {

    }

    @org.junit.Test
    public void testGetTeam() throws Exception {

    }

    private String getResourceData(String resourceName) throws FileNotFoundException {
        return new Scanner(new File(getClass().getResource(resourceName).getFile())).useDelimiter("\\A").next();
    }

}