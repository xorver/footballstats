package footballstats.statparser;


import footballstats.gui.LogReceiver;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
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
    private int PROXY_COUNT=5;
    private final int PROXY_TIMEOUT=500;
    private final int NORMAL_REQ_TIMEOUT=10000;

    private List<InetSocketAddress> proxies = new ArrayList<>();

    public HttpClientAdapter() {
        findRandomProxies(PROXY_COUNT);
    }

    public String doGetRequestByProxy(String addr){
        int randPrx = (int)Math.floor(Math.random()*proxies.size());
        try {
            if(proxies.isEmpty()) {
                PROXY_COUNT+=2;
                LogReceiver.getInstance().info("Regenerating proxies...");
                findRandomProxies(PROXY_COUNT);
            }
            Proxy proxy = new Proxy(Proxy.Type.HTTP, proxies.get(randPrx));
            HttpURLConnection con = (HttpURLConnection) new URL(addr).openConnection(proxy);
            con.setConnectTimeout(NORMAL_REQ_TIMEOUT);

            LogReceiver.getInstance().info("Performing http request, url: " + addr + ", using proxy nr "+randPrx+", Ans code: "+con.getResponseCode());

            return doGetRequest(con);
        } catch (IOException | IllegalStateException e) {
            LogReceiver.getInstance().error("Cannot get page: " + addr);
            LogReceiver.getInstance().warning("Banning proxy with number: " + randPrx);
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
            LogReceiver.getInstance().warning("Cannot get http data");
            throw new IllegalStateException("Cannot get http data");
        }
    }

    private void findRandomProxies(int n){
        try {
            proxies.clear();
            URL obj = new URL(PROXY_ADDRESS_PROVIDER);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            String page = doGetRequest(con);

            Pattern pattern = Pattern.compile(PROXY_REGEX);
            Matcher matcher = pattern.matcher(page);
            LogReceiver.getInstance().info("Found proxy list");

            List<InetSocketAddress> all = new ArrayList<>();

            while(matcher.find())
                all.add(new InetSocketAddress(matcher.group(1),Integer.parseInt(matcher.group(2))));

//            Collections.shuffle(all);

            for(int i=0;i<all.size() && n>0;i++) {
                LogReceiver.getInstance().info("Found proxy: "+all.get(i).getHostName()+":"+all.get(i).getPort());
                if(isValidProxy(all.get(i))) {
                    LogReceiver.getInstance().info("Proxy test connection success, need "+n+" more...");
                    proxies.add(all.get(i));
                    n--;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogReceiver.getInstance().error("Cannot obtain proxy list");
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
