package com.example.smartgridmetering.Service.kafka;

import com.example.smartgridmetering.Model.EnergyConsumption;
import com.example.smartgridmetering.Model.User;
import com.example.smartgridmetering.Repository.EnergyConsumptionRepository;
import com.example.smartgridmetering.Repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class EnergyConsumer {

    private final EnergyConsumptionRepository repository;
    private final UserRepository userRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public EnergyConsumer(EnergyConsumptionRepository repository,  UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "energy-group")
    public void consume(String message) throws JsonProcessingException {
        JsonNode node = mapper.readTree(message);

        Long userId = node.get("userId").asLong();
        double consumed = node.get("energyConsumed").asDouble();
        LocalDateTime datetime = LocalDateTime.parse(node.get("datetime").asText());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        EnergyConsumption entry = new EnergyConsumption();
        entry.setUser(user);
        entry.setEnergyConsumed(BigDecimal.valueOf(consumed));
        entry.setDatetime(datetime);

        repository.save(entry);

        System.out.println("Saved event to DB: " + entry.toString());
        System.out.println("Consumed & saved: " + message);
    }
}
