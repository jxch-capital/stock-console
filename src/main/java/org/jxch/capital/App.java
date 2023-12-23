package org.jxch.capital;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jxch.capital.api.YahooApi;
import org.jxch.capital.api.dto.DownloadStockCsvParam;
import org.jxch.capital.api.dto.HistoryRes;
import org.jxch.capital.command.AppCommand;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import picocli.CommandLine;

import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import static org.fusesource.jansi.Ansi.ansi;

@Slf4j
@Configuration
@ComponentScan
@EnableAspectJAutoProxy
public class App {
    @Getter
    private static ApplicationContext context;

    public static void main(String[] args) {
        System.exit(new CommandLine(new AppCommand()).execute(args));
    }
}
