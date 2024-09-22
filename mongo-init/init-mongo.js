// mongo-init.js
// kline_data koleksiyonunu oluştur ve doğrulama kuralları ekle

db = db.getSiblingDB('testdb');
db.createCollection("kline_data", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["symbol", "interval", "openTime", "open", "high", "low", "close", "volume", "closeTime"],
      properties: {
        symbol: {
          bsonType: "string",
          description: "Must be a string and is required"
        },
        interval: {
          bsonType: "string",
          enum: ["1s","1m", "3m", "5m", "15m", "30m", "1h", "2h", "4h", "6h", "8h", "12h", "1d", "3d", "1w", "1M"],
          description: "Must be a string and one of the specified intervals"
        },
        openTime: {
          bsonType: "long",
          description: "Must be a long and is required"
        },
        open: {
          bsonType: "double",
          description: "Must be a double and is required"
        },
        high: {
          bsonType: "double",
          description: "Must be a double and is required"
        },
        low: {
          bsonType: "double",
          description: "Must be a double and is required"
        },
        close: {
          bsonType: "double",
          description: "Must be a double and is required"
        },
        volume: {
          bsonType: "double",
          description: "Must be a double and is required"
        },
        closeTime: {
          bsonType: "long",
          description: "Must be a long and is required"
        }
      }
    }
  },
  validationLevel: "strict",
  validationAction: "error"
});

// İndeksler oluştur
db.kline_data.createIndex({ symbol: 1, interval: 1, openTime: 1 }, { unique: true });
db.kline_data.createIndex({ closeTime: 1 });

// Örnek belge ekle
db.kline_data.insertOne({
  symbol: "BTCUSDT",
  interval: "1m",
  openTime: NumberLong(1695264000000),
  open: 20000.0,
  high: 20050.0,
  low: 19950.0,
  close: 20025.0,
  volume: 10.5,
  closeTime: NumberLong(1695264060000)
});

db = db.getSiblingDB('graylog');

db.createUser({
  user: "graylog",
  pwd: "graylog_password",
  roles: [
    {
      role: "readWrite",
      db: "graylog"
    }
  ]
});
