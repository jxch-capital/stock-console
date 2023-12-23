package org.jxch.capital;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
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
        YahooApi.enableProxy("127.0.0.1", 10809);

        List<HistoryRes> historyRes = YahooApi.downloadStockCsv(DownloadStockCsvParam.builder()
                .code("QQQ")
                .start(DateUtil.offset(Calendar.getInstance().getTime(), DateField.MONTH, -3))
                .build());

        log.info(JSON.toJSONString(historyRes));
    }


}
