package footballstats.statparser;


import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tomasz Lichon on 06.07.14.
 */


public class HttpClientAdapter {

    private final String PROXY_ADDRESS_PROVIDER = "http://www.us-proxy.org/";
    private final String ALL_MATCHES_URL = "http://annabet.com/pl/soccerstats/upcoming/";
    private final String PROXY_REGEX = "(\\d+\\.\\d+\\.\\d+\\.\\d+)</td><td>(\\d+)";
    private final int PROXY_COUNT=5;
    private final int PROXY_TIMEOUT=500;

    private List<InetSocketAddress> proxies = new ArrayList<>();

    public HttpClientAdapter() {
        findRandomProxies(PROXY_COUNT);
    }

    public String doGetRequestByProxy(String addr){
        int randPrx = (int)Math.floor(Math.random()*proxies.size());
        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, proxies.get(randPrx));
            HttpURLConnection con = (HttpURLConnection) new URL(addr).openConnection(proxy);

            System.out.println("[info] Performing http request, url: " + addr + ", using proxy nr "+randPrx+", Ans code: "+con.getResponseCode());

            return doGetRequest(con);
        } catch (IOException | IllegalStateException e) {
            System.out.println("[error] Cannot get page: " + addr);
            proxies.remove(randPrx);
        }
        return "";
    }

    private String doGetRequest(HttpURLConnection con) {
        try{
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            if(con.getResponseCode()!=200)
                throw new IllegalStateException("Wrong http status code");

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (IOException e) {
            System.out.println("[error] Cannot get page data ");
            throw new IllegalStateException("Cannot get http data");
        }
    }

    private void findRandomProxies(int n){
        try {
            URL obj = new URL(PROXY_ADDRESS_PROVIDER);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            String page = doGetRequest(con);

            Pattern pattern = Pattern.compile(PROXY_REGEX);
            Matcher matcher = pattern.matcher(page);
            System.out.println("[info] Found proxy list");
            while(matcher.find() && n>0){
                System.out.println("[info] Found proxy: "+matcher.group(1)+":"+matcher.group(2));
                InetSocketAddress addr = new InetSocketAddress(matcher.group(1),Integer.parseInt(matcher.group(2)));
                if(isValidProxy(addr)) {
                    proxies.add(addr);
                    n--;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Cannot obtain proxy list");
        }
    }

    private boolean isValidProxy(InetSocketAddress address){
        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
            HttpURLConnection con = (HttpURLConnection) new URL(ALL_MATCHES_URL).openConnection(proxy);
            con.setConnectTimeout(PROXY_TIMEOUT);
            doGetRequest(con);

        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
