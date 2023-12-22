package org.jxch.capital;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {
    private static final String API_ENDPOINT = "https://query1.finance.yahoo.com/v7/finance/quote?lang=ko-KR&region=KR&corsDomain=finance.yahoo.com";
    private static final String COOKIE = "GUC=AQEBCAFlULBleUIhXwTg&s=AQAAAC76j7jx&g=ZU9e-w; A1=d=AQABBK0Lr2ICEI-bBud4SuIDLaB4bqaMNbAFEgEBCAGwUGV5Zc3ibmUB_eMBAAcIrQuvYqaMNbA&S=AQAAAggpViKy189d2OkdWsFnK_Y; A3=d=AQABBK0Lr2ICEI-bBud4SuIDLaB4bqaMNbAFEgEBCAGwUGV5Zc3ibmUB_eMBAAcIrQuvYqaMNbA&S=AQAAAggpViKy189d2OkdWsFnK_Y; A1S=d=AQABBK0Lr2ICEI-bBud4SuIDLaB4bqaMNbAFEgEBCAGwUGV5Zc3ibmUB_eMBAAcIrQuvYqaMNbA&S=AQAAAggpViKy189d2OkdWsFnK_Y; gpp=DBAA; gpp_sid=-1; gam_id=y-rpk1JRJE2uL6EDgVwC0_in.GgdLv339c~A; axids=gam=y-rpk1JRJE2uL6EDgVwC0_in.GgdLv339c~A&dv360=eS00Mm1IMHpoRTJ1RXhhNVlTUGtSVWRPdkZCWExrbjFkNH5B; tbla_id=ba643052-93dc-4ae8-a277-ee1cb7f7dab5-tuctc48e47a; cmp=t=1699788664&j=0&u=1---; PRF=t%3DZURN.SW%252BSREN.SW%26newChartbetateaser%3D0%252C1700910092680";
    private static final String CRUMB = "mdeVssfeRhi";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0";

    public static void main(String[] args) {
        long interval = Long.parseLong(args[0]);
        String symbols = Arrays.stream(args).skip(1).collect(Collectors.joining(","));

        HttpHost proxy = new HttpHost("127.0.0.1", 10809);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().setProxy(proxy).build()) {
            HttpGet request = new HttpGet(API_ENDPOINT + "&crumb=" + CRUMB + "&symbols=" + URLEncoder.encode(symbols, StandardCharsets.UTF_8));

            request.addHeader("cookie", COOKIE);
            request.addHeader("user-agent", USER_AGENT);

            while (true) {
                var result = httpClient.execute(request);
                String json = EntityUtils.toString(result.getEntity());
                print(new JSONObject(json));
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void print(JSONObject jsonObject) {
        JSONArray result = jsonObject.getJSONObject("quoteResponse").getJSONArray("result");
        result = sort(result);

        StringBuilder sb = new StringBuilder();
        sb.append("\033[H\033[2J");
        sb.append(String.format(ConsoleColors.CYAN_UNDERLINED + "%-15s%10s%10s%11s%10s%15s%12s %-20s\033[0m\n", "Name", "Symbol", "Price", "Diff", "Percent", "Delay", "MarketState", "Long Name"));

        long currentTimeMillis = System.currentTimeMillis();

        for (int i = 0; i < result.length(); i++) {
            JSONObject data = result.getJSONObject(i);

            String shortName = data.optString("shortName");
            shortName = shortName.length() > 14 ? shortName.substring(0, 14) : shortName;

            String longName = data.optString("longName", shortName);
            String symbol = data.optString("symbol");
            String marketState = data.optString("marketState");
            long regularMarketTime = data.optLong("regularMarketTime");

            double regularMarketPrice = data.optDouble("regularMarketPrice");
            double regularMarketDayHigh = data.optDouble("regularMarketDayHigh");
            double regularMarketDayLow = data.optDouble("regularMarketDayLow");
            double regularMarketChange = data.optDouble("regularMarketChange");
            double regularMarketChangePercent = data.optDouble("regularMarketChangePercent");

            String color = regularMarketChange == 0 ? "" : regularMarketChange > 0 ? ConsoleColors.GREEN_BOLD_BRIGHT : ConsoleColors.RED_BOLD_BRIGHT;

            sb.append(String.format("%-15s", shortName));
            sb.append(String.format("%10s", symbol));

            if (regularMarketDayHigh == regularMarketPrice || regularMarketDayLow == regularMarketPrice) {
                sb.append(String.format(ConsoleColors.WHITE_BOLD + color + "%10.2f" + ConsoleColors.RESET, regularMarketPrice));
            } else {
                sb.append(String.format(ConsoleColors.WHITE_BOLD + "%10.2f" + ConsoleColors.RESET, regularMarketPrice));
            }

            sb.append(String.format(color + "%11s" + ConsoleColors.RESET, String.format("%.2f", regularMarketChange) + " " + (regularMarketChange > 0 ? "▲" : regularMarketChange < 0 ? "▼" : "-")));
            sb.append(String.format(color + "%10s" + ConsoleColors.RESET, String.format("(%.2f%%)", regularMarketChangePercent)));
            sb.append(String.format("%15s", prettyTime(currentTimeMillis - (regularMarketTime * 1000))));
            sb.append(String.format("%12s", marketState));
            sb.append(String.format(" %-20s\n", longName));
        }
        System.out.print(sb);
    }

    private static String prettyTime(long millis) {
        return String.format("%dm, %ds",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    private static JSONArray sort(JSONArray result) {
        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<>();
        for (int i = 0; i < result.length(); i++) {
            jsonValues.add(result.getJSONObject(i));
        }

        jsonValues.sort((a, b) -> {
            double valA = a.optDouble("regularMarketChangePercent");
            double valB = b.optDouble("regularMarketChangePercent");
            return Double.compare(valB, valA);
        });

        for (JSONObject jsonValue : jsonValues) {
            sortedJsonArray.put(jsonValue);
        }

        return sortedJsonArray;
    }
}