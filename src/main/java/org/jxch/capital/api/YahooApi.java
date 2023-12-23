package org.jxch.capital.api;

import com.alibaba.fastjson2.JSONObject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jxch.capital.api.dto.QuoteRes;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Objects;

@Slf4j
public class YahooApi {
    private static final String COOKIE = System.getProperty("cookie", "GUC=AQEBCAFlULBleUIhXwTg&s=AQAAAC76j7jx&g=ZU9e-w; A1=d=AQABBK0Lr2ICEI-bBud4SuIDLaB4bqaMNbAFEgEBCAGwUGV5Zc3ibmUB_eMBAAcIrQuvYqaMNbA&S=AQAAAggpViKy189d2OkdWsFnK_Y; A3=d=AQABBK0Lr2ICEI-bBud4SuIDLaB4bqaMNbAFEgEBCAGwUGV5Zc3ibmUB_eMBAAcIrQuvYqaMNbA&S=AQAAAggpViKy189d2OkdWsFnK_Y; A1S=d=AQABBK0Lr2ICEI-bBud4SuIDLaB4bqaMNbAFEgEBCAGwUGV5Zc3ibmUB_eMBAAcIrQuvYqaMNbA&S=AQAAAggpViKy189d2OkdWsFnK_Y; gpp=DBAA; gpp_sid=-1; gam_id=y-rpk1JRJE2uL6EDgVwC0_in.GgdLv339c~A; axids=gam=y-rpk1JRJE2uL6EDgVwC0_in.GgdLv339c~A&dv360=eS00Mm1IMHpoRTJ1RXhhNVlTUGtSVWRPdkZCWExrbjFkNH5B; tbla_id=ba643052-93dc-4ae8-a277-ee1cb7f7dab5-tuctc48e47a; cmp=t=1699788664&j=0&u=1---; PRF=t%3DZURN.SW%252BSREN.SW%26newChartbetateaser%3D0%252C1700910092680");
    private static final String USER_AGENT = System.getProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0");
    private static final String HTTP_PROXY = System.getProperty("http.proxy", "127.0.0.1");
    private static final Integer HTTP_PORT = Integer.parseInt(System.getProperty("http.port", "10809"));
    private static final Boolean ENABLE_PROXY = Boolean.parseBoolean(System.getProperty("proxy.enable", "true"));
    private static final String V7_FINANCE_QUOTE = System.getProperty("api.quote", "https://query1.finance.yahoo.com/v7/finance/quote?crumb=mdeVssfeRhi");


    @NonNull
    public static OkHttpClient yahooBaseClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (ENABLE_PROXY) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(HTTP_PROXY, HTTP_PORT));
            builder.proxy(proxy);
        }

        return builder.build();
    }

    @NonNull
    public static Request.Builder yahooBaseRequest() {
        return new Request.Builder()
                .addHeader("cookie", COOKIE)
                .addHeader("user-agent", USER_AGENT);
    }

    public static QuoteRes quote(String... symbols) {
        try {
            Request request = yahooBaseRequest()
                    .url(V7_FINANCE_QUOTE + "&symbols=" + String.join(",", symbols))
                    .build();

            try (Response response = yahooBaseClient().newCall(request).execute()) {
                return JSONObject.parseObject(Objects.requireNonNull(response.body()).string(), QuoteRes.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
