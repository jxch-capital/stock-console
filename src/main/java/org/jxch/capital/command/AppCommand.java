package org.jxch.capital.command;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.fusesource.jansi.Ansi;
import org.jxch.capital.api.YahooApi;
import org.jxch.capital.api.dto.QuoteParam;
import org.jxch.capital.api.dto.QuoteRes;
import org.jxch.capital.api.dto.QuoteResponseResultItem;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.fusesource.jansi.Ansi.ansi;

@Slf4j
@Command(name = "stock", mixinStandardHelpOptions = true, version = "stock v0.1.0", description = "实时显示股票信息.")
public class AppCommand implements Callable<Integer> {
    @Option(names = {"-i", "--interval"}, description = "刷新间隔")
    private Integer interval = 500;

    @Option(names = {"-s", "--symbols"}, description = "股票代码")
    private String symbols;

    @Option(names = {"-h", "--host"}, description = "代理服务器")
    private String host;

    @Option(names = {"-p", "--port"}, description = "代理端口")
    private Integer port;

    public List<String> getSymbols() {
        return Arrays.asList(this.symbols.split(","));
    }

    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    @Override
    public Integer call() throws Exception {
        if (Objects.nonNull(host)) {
            YahooApi.enableProxy(host, port);
        }
        System.out.println(ansi().eraseScreen());
        executorService.scheduleAtFixedRate(() -> {
            StringBuilder tableAnsi = new StringBuilder();
            tableAnsi.append(ansi().cursor(0, 0).fg(Ansi.Color.BLUE).a(Ansi.Attribute.UNDERLINE).bold().a(
                    String.format("%-10s%-10s%10s%10s%25s%10s\n", "Symbol", "Price", "Diff", "Status", "Name", "Update")).reset());

            QuoteRes quote = YahooApi.quote(QuoteParam.builder().symbols(getSymbols()).build());
            quote.getQuoteResponse().getResult().stream()
                    .sorted(Comparator.comparing(QuoteResponseResultItem::getRegularMarketChange).reversed())
                    .forEach(item -> tableAnsi.append(ansi()
                            .fg(item.getRegularMarketChange() > 0 ? Ansi.Color.GREEN : Ansi.Color.RED)
                            .a(String.format("%-10s", item.getSymbol()))
                            .a(String.format("%-10s", String.format("%.2f", item.getRegularMarketPrice())))
                            .a(String.format("%10s", String.format("%.2f%% %-2s", item.getRegularMarketChangePercent(), item.getRegularMarketChange() > 0 ? "▲" : "▼")))
                            .reset()
                            .a(String.format("%10s", item.getMarketState()))
                            .a(String.format("%25s", item.getLongName()))
                            .a(String.format("%10s", DateUtil.format(new Date(item.getRegularMarketTime() * 1000), "HH:mm:ss")))
                    ).append("\n"));

            System.out.println(tableAnsi);
        }, 0, interval, TimeUnit.MILLISECONDS);

        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        return 0;
    }
}
