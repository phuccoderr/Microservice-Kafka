# Microservice-Kafka

Apache Kafka tutorial with Spring Boot

# Kafka

# Server with KRaft

```
bin\windows\kafka-storage.bat random-uuid
bin\windows\kafka-storage.bat format -t (uuid được tạo) -c config\kraft\server.properties
bin\windows\kafka-server-start.bat config\kraft\server.properties
```

- Lưu ý Server: nếu mún có nhiều 2-3 broker server bạn muốn chạy

```
bin\windows\kafka-storage.bat random-uuid
bin\windows\kafka-storage.bat format -t (uuid được tạo) -c config\kraft\server-1.properties
bin\windows\kafka-server-start.bat config\kraft\server-1.properties
```

# Create Topic

```
bin\windows\kafka-topics.bat --create --topic topic1 --partitions 3 --bootstrap-server localhost:9092
```

- Lưu ý Top: nếu mún chạy thêm local thì gán local của server thứ 2

```
bin\windows\kafka-topics.bat --create --topic topic1 --partitions 3 --replication-factor 3 --bootstrap-server localhost:9092,localhost:9093
```

- Config bản sao

```
bin\windows\kafka-topics.bat --create --topic topic1 --partitions 3 --replication-factor 3 --bootstrap-server localhost:9092 --config min.insync.replicas=2
```

# List Topic

```
bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092
```

# Describe Topic (chi tiết topic)

```
bin\windows\kafka-topics.bat --describe --bootstrap-server localhost:9092
```

# Delete Topic

```
bin\windows\kafka-topics.bat --delete --topic topic1 --bootstrap-server localhost:9092
```

# Producer Message

```
bin\windows\kafka-console-producer.bat --topic topic1 --bootstrap-server localhost:9092
```

# Producer Message Key Value

```
bin\windows\kafka-console-producer.bat --topic topic1 --bootstrap-server localhost:9092 --property "parse.key=true" --property "key.separator=:"
```

# Consumer Message

```
bin\windows\kafka-console-consumer.bat --topic topic1 --from-beginning --bootstrap-server localhost:9092
```

# Consumer Message Key Value

```
bin\windows\kafka-console-consumer.bat --topic topic1 --from-beginning --bootstrap-server localhost:9092 --property print.key=true
Hiện ALL
bin\windows\kafka-console-consumer.bat --topic topic1 --from-beginning --bootstrap-server localhost:9092 --property print.key=true --property print.value=true
```

# Config Topic

```
bin\windows\kafka-configs.sh --bootstrap-server localhost:9092 --alter --entity-type topics --entity-name topic1 --add-config min.insync.replicas=2
```

# Dead Letter Topic

```
bin\windows\kafka-console-consumer.bat --topic topic1.DLT --from-beginning --bootstrap-server localhost:9092 --property print.key=true --property print.value=true
```

- Giải mã base 64: trả về lỗi được mã hoá base64, muốn đọc thì lên web online base-64 decoder

# Docker

- Lưu ý: docker-compose phải ở trong tệp kafka

```
docker-compose -f docker-compose.yml --env-file enviroment.env up
```
