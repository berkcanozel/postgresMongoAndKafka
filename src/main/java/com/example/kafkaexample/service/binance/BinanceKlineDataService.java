package com.example.kafkaexample.service.binance;
import com.example.kafkaexample.data.mongo.entity.KlineData;
import com.example.kafkaexample.data.mongo.repository.BinanceKlineDataRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BinanceKlineDataService {

    private final BinanceService binanceService;


    @Autowired
    private BinanceKlineDataRepository klineDataRepository;

    public List<KlineData> fetchAndSaveKlineData(String symbol, String interval, boolean oneTimeActive) {
        String response = null;
        if(oneTimeActive){
            response = binanceService.getKlineData(symbol, interval,1);
        }else{
            response = binanceService.getKlineData(symbol, interval);
        }

        JSONArray jsonArray = new JSONArray(response);

        List<KlineData> klineDataList = new ArrayList<>();

        for (Object obj : jsonArray) {
            JSONArray kline = (JSONArray) obj;
            KlineData data = new KlineData();
            data.setSymbol(symbol);
            data.setInterval(interval);
            data.setOpenTime(kline.getLong(0));
            data.setOpen(kline.getDouble(1));
            data.setHigh(kline.getDouble(2));
            data.setLow(kline.getDouble(3));
            data.setClose(kline.getDouble(4));
            data.setVolume(kline.getDouble(5));
            data.setCloseTime(kline.getLong(6));

            klineDataList.add(data);
        }

        return klineDataList;
    }

    public void saveKlineData(List<KlineData> klineDataList){
        klineDataRepository.saveAll(klineDataList);
    }

    public List<KlineData> getAllKlineData() {
        return klineDataRepository.findAll();
    }
}

