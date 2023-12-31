package org.jxch.capital.api.dto;

import cn.hutool.core.annotation.Alias;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class HistoryRes {
    @Alias("Date")
    private Date date;
    @Alias("Open")
    private Double open;
    @Alias("High")
    private Double high;
    @Alias("Low")
    private Double low;
    @Alias("Close")
    private Double close;
    @Alias("Adj Close")
    private Double adjClose;
    @Alias("Volume")
    private Long volume;
}
