package dev.workload.microservice.repository;

import dev.workload.microservice.model.TrainingWorkload;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
public class InMemoryStorage {
    private final Map<String, TrainingWorkload> trainingWorkloadStorage = new HashMap<>();

    public Map<String, TrainingWorkload> getTrainingWorkloadStorage() {
        return trainingWorkloadStorage;
    }
}