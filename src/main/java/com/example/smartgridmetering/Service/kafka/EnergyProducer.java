package com.example.smartgridmetering.Service.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EnergyProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topic;

    public EnergyProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Simulate energy meter data
    @Scheduled(fixedRate = 5000) // every 5 seconds
    public void sendEnergyReading() {
        double energyConsumed = Math.random() * 5; // kWh in last interval
        int userId = (int)(Math.random() * 2) + 1;
        String message = String.format(
                "{\"userId\":%d,\"datetime\":\"%s\",\"energyConsumed\":%.2f}",
                userId,
                LocalDateTime.now(),
                energyConsumed
        );
        kafkaTemplate.send(topic, message);
        System.out.println("Produced: " + message);
    }
}
