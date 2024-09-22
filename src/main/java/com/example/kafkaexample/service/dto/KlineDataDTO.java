package com.example.kafkaexample.service.dto;

import com.example.kafkaexample.data.mongo.entity.KlineData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KlineDataDTO {
    private Long openTime;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Double volume;
    private Long closeTime;
    private Double quoteAssetVolume;
    private Integer numberOfTrades;
    private Double takerBuyBaseAssetVolume;
    private Double takerBuyQuoteAssetVolume;
    private Double ignore;

    public static KlineDataDTO fromEntity(KlineData entity) {
        KlineDataDTO dto = new KlineDataDTO();
        dto.setOpenTime(entity.getOpenTime());
        dto.setOpen(entity.getOpen());
        dto.setHigh(entity.getHigh());
        dto.setLow(entity.getLow());
        dto.setClose(entity.getClose());
        return dto;
    }
}
