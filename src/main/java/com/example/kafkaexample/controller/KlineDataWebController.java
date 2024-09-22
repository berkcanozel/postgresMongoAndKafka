package com.example.kafkaexample.controller;

import com.example.kafkaexample.data.mongo.entity.KlineData;
import com.example.kafkaexample.data.mongo.repository.BinanceKlineDataRepository;
import com.example.kafkaexample.service.dto.KlineDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class KlineDataWebController {

    @Autowired
    private BinanceKlineDataRepository klineDataRepository;

    @GetMapping("/kline-data")
    public List<KlineDataDTO> getKlineData(
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime) {

        List<KlineData> klineDataList;

        if (startTime != null && endTime != null) {
            klineDataList = klineDataRepository.findByOpenTimeBetweenOrderByOpenTimeAsc(startTime, endTime);
        } else {
            // Varsayılan olarak son 1000 kaydı döndür
            klineDataList = klineDataRepository.findTop1000ByOrderByOpenTimeDesc();
        }

        // DTO'ya dönüştürerek sadece gerekli alanları döndür
        return klineDataList.stream()
                .map(KlineDataDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
