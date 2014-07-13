package footballstats.statparser;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tomasz Lichon on 06.07.14.
 */
public class HttpClientAdapter {

    private static final Logger logger = Logger.getLogger(HttpClientAdapter.class);

    public String doGetRequest(String url){
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            int responseCode = con.getResponseCode();
            logger.info("Sending 'GET' request to URL : " + url);
            logger.info("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("No connection with"+url);
        }
    }
}
