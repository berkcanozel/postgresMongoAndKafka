package com.example.kafkaexample.data.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Document(collection = "kline_data")
public class KlineData {

    @Id
    private String id;

    private String symbol;
    private String interval;
    private Long openTime;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Double volume;
    private Long closeTime;

    // Getters and Setters
}

