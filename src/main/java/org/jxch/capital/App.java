package org.jxch.capital;

import com.alibaba.fastjson2.JSON;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jxch.capital.api.YahooApi;
import org.jxch.capital.api.dto.DownloadStockCsvParam;
import org.jxch.capital.api.dto.HistoryRes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.Calendar;
import java.util.List;

@Slf4j
@Configuration
@ComponentScan
@EnableAspectJAutoProxy
public class App {
    @Getter
    private static ApplicationContext context;


    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -3);
        List<HistoryRes> historyRes = YahooApi.downloadStockCsv(DownloadStockCsvParam.builder()
                .code("QQQ")
                .start(calendar.getTime())
                .build());
        log.info(JSON.toJSONString(historyRes));
    }


}
