package org.jxch.capital.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DownloadStockCsvParam {
    private String code;
    private Date start;
    @Builder.Default
    private Date end = Calendar.getInstance().getTime();
    @Builder.Default
    private String interval = "1d";
    @Builder.Default
    private String events = "history";
    @Builder.Default
    private boolean includeAdjustedClose = true;

    public String getPeriod1Param() {
        return String.valueOf(this.start.getTime() / 1000);
    }

    public String getPeriod2Param() {
        return String.valueOf(this.end.getTime() / 1000);
    }

    public String getIntervalParam() {
        return this.interval;
    }

    public String getEventsParam() {
        return this.events;
    }

    public String getIncludeAdjustedCloseParam() {
        return String.valueOf(this.includeAdjustedClose);
    }
}
