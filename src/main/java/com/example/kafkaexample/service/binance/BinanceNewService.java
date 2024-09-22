package com.example.kafkaexample.service.binance;

import com.example.kafkaexample.data.mongo.entity.KlineData;
import com.example.kafkaexample.data.mongo.repository.BinanceKlineDataRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class BinanceNewService {

    private static final String BINANCE_BASE_URL = "https://api.binance.com";
    private static final String SYMBOL = "BTCUSDT";
    private static final String INTERVAL = "1m"; // 1 dakika interval
    private static final int LIMIT = 1000; // Binance API'si için maksimum limit

    @Autowired
    private BinanceKlineDataRepository klineDataRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private WebClient webClient = WebClient.builder()
            .baseUrl(BINANCE_BASE_URL)
            .build();

    /**
     * Tarihsel verileri çoklu iş parçacığı kullanarak çeker ve veritabanına kaydeder.
     */
    public void fetchAndStoreHistoricalData() {
        try {
            // Mevcut en son zaman damgasını al
            KlineData lastKline = klineDataRepository.findTopBySymbolAndIntervalOrderByCloseTimeDesc(SYMBOL, INTERVAL);
            long startTime = (lastKline != null) ? lastKline.getCloseTime() + 1 :
                    LocalDate.of(2018, 1, 1)
                            .atStartOfDay()
                            .toInstant(ZoneOffset.UTC)
                            .toEpochMilli();
            long endTime = System.currentTimeMillis();

            // Zaman aralığını 1000 dakikalık bölümlere ayır
            long intervalMillis = 1000L * 60 * 1000; // 1000 dakika = 60,000,000 milisaniye
            List<Callable<Void>> tasks = new ArrayList<>();

            while (startTime < endTime) {
                long taskStartTime = startTime;
                long taskEndTime = Math.min(startTime + intervalMillis, endTime);

                tasks.add(() -> {
                    fetchAndStoreKlines(taskStartTime, taskEndTime);
                    return null;
                });

                startTime = taskEndTime;
            }

            // İş Parçacığı Havuzu Oluştur
            int threadCount = 12; // İşlemcinizin kapasitesine göre
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);

            // Görevleri Paralel Olarak Çalıştır
            List<Future<Void>> futures = executor.invokeAll(tasks);

            // Tüm görevlerin tamamlanmasını bekle
            for (Future<Void> future : futures) {
                future.get(); // Hata kontrolü için
            }

            // İş Parçacığı Havuzunu Kapat
            executor.shutdown();

            System.out.println("Tüm tarihsel veriler başarıyla çekildi ve kaydedildi.");
        } catch (Exception e) {
            System.err.println("Tarihsel veri çekerken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Belirtilen zaman aralığı için kline verilerini çeker ve kaydeder.
     *
     * @param startTime Başlangıç zamanı (milisaniye)
     * @param endTime   Bitiş zamanı (milisaniye)
     */
    private void fetchAndStoreKlines(long startTime, long endTime) {
        try {
            boolean hasMore = true;

            while (hasMore && startTime < endTime) {
                List<KlineData> klineDataList = getKlineData(SYMBOL, INTERVAL, LIMIT, startTime, endTime);
                if (klineDataList.isEmpty()) {
                    hasMore = false;
                } else {
                    // Verileri veritabanına kaydet
                    for(KlineData klineData:klineDataList) {
                        if (klineDataRepository.findByOpenTime(klineData.getOpenTime()).isEmpty()) {
                            klineDataRepository.save(klineData);
                        }
                    }

                    System.out.println("Thread " + Thread.currentThread().getName() + " fetched " + klineDataList.size() + " klines starting at " + Instant.ofEpochMilli(startTime));

                    // Son kline'ın closeTime'ını bir sonraki startTime olarak ayarla
                    startTime = klineDataList.get(klineDataList.size() - 1).getCloseTime() + 1;

                    // Eğer dönen veri sayısı limitten azsa, bu zaman aralığındaki tüm verileri çekmişiz demektir
                    if (klineDataList.size() < LIMIT) {
                        hasMore = false;
                    }

                    // Oran sınırlarını aşmamak için istekler arasında kısa bir gecikme ekle
                    Thread.sleep(200); // 200 milisaniye bekle
                }
            }
        } catch (Exception e) {
            System.err.println("Thread " + Thread.currentThread().getName() + " kline verisi çekerken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Belirli bir zaman aralığında Kline verisini çeker.
     *
     * @param symbol    Ticaret çifti (örn: BTCUSDT)
     * @param interval  Zaman aralığı (örn: 1m, 5m, 1h)
     * @param limit     Çekilecek veri sayısı (maksimum 1000)
     * @param startTime Başlangıç zamanı (milisaniye)
     * @param endTime   Bitiş zamanı (milisaniye)
     * @return KlineData listesi
     */
    private List<KlineData> getKlineData(String symbol, String interval, int limit, long startTime, long endTime) {
        List<KlineData> klineDataList = new ArrayList<>();
        try {
            JsonNode response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v3/klines")
                            .queryParam("symbol", symbol)
                            .queryParam("interval", interval)
                            .queryParam("limit", limit)
                            .queryParam("startTime", startTime)
                            .queryParam("endTime", endTime)
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && response.isArray()) {
                for (JsonNode node : response) {
                    KlineData kline = new KlineData();
                    kline.setSymbol(symbol);
                    kline.setInterval(interval);
                    kline.setOpenTime(node.get(0).asLong());
                    kline.setOpen(node.get(1).asDouble());
                    kline.setHigh(node.get(2).asDouble());
                    kline.setLow(node.get(3).asDouble());
                    kline.setClose(node.get(4).asDouble());
                    kline.setVolume(node.get(5).asDouble());
                    kline.setCloseTime(node.get(6).asLong());

                    klineDataList.add(kline);
                }
            }
        } catch (Exception e) {
            System.err.println("Kline verisi çekerken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
        return klineDataList;
    }

}
