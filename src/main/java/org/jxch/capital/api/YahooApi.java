package org.jxch.capital.api;

import cn.hutool.core.text.csv.CsvUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jxch.capital.api.dto.DownloadStockCsvParam;
import org.jxch.capital.api.dto.HistoryRes;
import org.jxch.capital.api.dto.QuoteParam;
import org.jxch.capital.api.dto.QuoteRes;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Objects;

@Slf4j
public class YahooApi {
    private static final String COOKIE = System.getProperty("cookie", "GUC=AQEBCAFlULBleUIhXwTg&s=AQAAAC76j7jx&g=ZU9e-w; A1=d=AQABBK0Lr2ICEI-bBud4SuIDLaB4bqaMNbAFEgEBCAGwUGV5Zc3ibmUB_eMBAAcIrQuvYqaMNbA&S=AQAAAggpViKy189d2OkdWsFnK_Y; A3=d=AQABBK0Lr2ICEI-bBud4SuIDLaB4bqaMNbAFEgEBCAGwUGV5Zc3ibmUB_eMBAAcIrQuvYqaMNbA&S=AQAAAggpViKy189d2OkdWsFnK_Y; A1S=d=AQABBK0Lr2ICEI-bBud4SuIDLaB4bqaMNbAFEgEBCAGwUGV5Zc3ibmUB_eMBAAcIrQuvYqaMNbA&S=AQAAAggpViKy189d2OkdWsFnK_Y; gpp=DBAA; gpp_sid=-1; gam_id=y-rpk1JRJE2uL6EDgVwC0_in.GgdLv339c~A; axids=gam=y-rpk1JRJE2uL6EDgVwC0_in.GgdLv339c~A&dv360=eS00Mm1IMHpoRTJ1RXhhNVlTUGtSVWRPdkZCWExrbjFkNH5B; tbla_id=ba643052-93dc-4ae8-a277-ee1cb7f7dab5-tuctc48e47a; cmp=t=1699788664&j=0&u=1---; PRF=t%3DZURN.SW%252BSREN.SW%26newChartbetateaser%3D0%252C1700910092680");
    private static final String USER_AGENT = System.getProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0");
    private static final String CRUMB = System.getProperty("user-agent", "mdeVssfeRhi");
    private static String httpProxy = System.getProperty("http.proxy", "127.0.0.1");
    private static Integer httpPort = Integer.parseInt(System.getProperty("http.port", "10809"));

    @Setter
    private static Boolean isProxy = false;

    public static void enableProxy(String host, int port) {
        isProxy = true;
        httpProxy = host;
        httpPort = port;
    }


    @NonNull
    public static OkHttpClient yahooBaseClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (isProxy) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpProxy, httpPort));
            builder.proxy(proxy);
        }

        return builder.build();
    }

    @NonNull
    public static HttpUrl.Builder yahooBaseUrl() {
        return new HttpUrl.Builder()
                .scheme("https")
                .addQueryParameter("crumb", CRUMB);
    }

    @NonNull
    public static Request.Builder yahooBaseRequest() {
        return new Request.Builder()
                .addHeader("cookie", COOKIE)
                .addHeader("user-agent", USER_AGENT);
    }

    public static QuoteRes quote(@NonNull QuoteParam param) {
        try {
            Request request = yahooBaseRequest()
                    .url(yahooBaseUrl()
                            .host("query1.finance.yahoo.com")
                            .addPathSegments("/v7/finance/quote")
                            .addQueryParameter("symbols", param.getSymbolsParam())
                            .build())
                    .build();

            try (Response response = yahooBaseClient().newCall(request).execute()) {
                return JSONObject.parseObject(Objects.requireNonNull(response.body()).string(), QuoteRes.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<HistoryRes> downloadStockCsv(@NonNull DownloadStockCsvParam param) {
        try {
            Request request = yahooBaseRequest()
                    .url(yahooBaseUrl()
                            .host("query1.finance.yahoo.com")
                            .addPathSegments("/v7/finance/download/" + param.getCode())
                            .addQueryParameter("period1", param.getPeriod1Param())
                            .addQueryParameter("period2", param.getPeriod2Param())
                            .addQueryParameter("interval", param.getIntervalParam())
                            .addQueryParameter("events", param.getEventsParam())
                            .addQueryParameter("includeAdjustedClose", param.getIncludeAdjustedCloseParam())
                            .build())
                    .build();

            try (Response response = yahooBaseClient().newCall(request).execute()) {
                return CsvUtil.getReader().read(Objects.requireNonNull(response.body()).string(), HistoryRes.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
