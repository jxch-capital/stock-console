package org.jxch.capital;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jxch.capital.api.YahooApi;
import org.jxch.capital.api.dto.QuoteRes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Calendar;

@Slf4j
@Configuration
@ComponentScan
@EnableAspectJAutoProxy
public class App {
    @Getter
    private static ApplicationContext context;


    public static void main(String[] args) {
        QuoteRes quote = YahooApi.quote("QQQ");
        log.info(JSON.toJSONString(quote));
    }


}
