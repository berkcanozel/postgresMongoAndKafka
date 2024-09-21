package com.example.kafkaexample.data.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
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

    @Indexed(unique = true)
    private long openTime;

    private double open;

    private double high;

    private double low;

    private double close;

    private double volume;

    private long closeTime;

    // Getters and Setters
    // ...
}

