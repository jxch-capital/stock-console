package org.jxch.capital.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuoteParam {
    private List<String> symbols;

    public String getSymbolsParam() {
        return String.join(",", symbols);
    }
}
