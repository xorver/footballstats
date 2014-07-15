package footballstats.statparser;

import org.apache.log4j.Logger;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tomasz Lichon on 06.07.14.
 */
public class HttpClientAdapter {

//    private static final Logger logger = Logger.getLogger(HttpClientAdapter.class);

    public String doGetRequest(String addr){
        try {
            URL obj = new URL(addr);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

//            System.out.println("[info] Performing http request, url: " + url);

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
            throw new IllegalStateException("No connection with: "+addr);
        }
    }
}
