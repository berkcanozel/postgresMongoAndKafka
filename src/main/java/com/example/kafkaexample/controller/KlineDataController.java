package com.example.kafkaexample.controller;

import com.example.kafkaexample.data.mongo.entity.KlineData;
import com.example.kafkaexample.service.binance.BinanceDataUpdaterService;
import com.example.kafkaexample.service.binance.BinanceKlineDataService;
import com.example.kafkaexample.service.binance.BinanceNewService;
import com.example.kafkaexample.service.binance.BinanceWebSocketService;
import com.example.kafkaexample.service.binance.DenemeWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kline")
public class KlineDataController {

    @Autowired
    private BinanceKlineDataService binanceKlineDataService;

    @Autowired
    private BinanceNewService binanceNewService;

    @Autowired
    private BinanceWebSocketService binanceWebSocketService;

    @Autowired
    private DenemeWebSocket denemeWebSocket;

    @Autowired
    private BinanceDataUpdaterService dataUpdaterService;

    @PostMapping("/update-missing-data")
    public ResponseEntity<String> updateMissingData() {
        try {
            dataUpdaterService.updateMissingData();
            return ResponseEntity.ok("Eksik veriler başarıyla güncellendi.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Veri güncelleme işlemi sırasında hata oluştu.");
        }
    }


    @GetMapping("/startWebSocketForRealData")
    public void startWebSocketForRealData() {
        // WebSocket bağlantısını başlat
        System.out.println("WebSocket bağlantısını başlatıyor...");
        binanceWebSocketService.connectWebSocket();
        System.out.println("WebSocket bağlantısı başlatıldı.");
    }

    @GetMapping("/dememe")
    public void dememe() {
        denemeWebSocket.start();
    }



    @GetMapping("/fetchAllHistoricalDataFromBinance")
    public void fetchAllHistoricalDataFromBinance() {
        // Tarihsel veriyi çek
        System.out.println("Tarihsel veriyi çekmeye başlıyor...");
        binanceNewService.fetchAndStoreHistoricalData();
        System.out.println("Tarihsel veri çekme tamamlandı.");
    }

    @GetMapping("/testFetch")
    public String fetchKlineData(@RequestParam String symbol, @RequestParam String interval) {
        binanceKlineDataService.saveKlineData(binanceKlineDataService.fetchAndSaveKlineData(symbol, interval,false));
        return "Veri alındı ve kaydedildi.";
    }

    @GetMapping("/testAll")
    public List<KlineData> getAllKlineData() {
        return binanceKlineDataService.getAllKlineData();
    }


}

