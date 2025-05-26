package dev.workload.microservice.repository;

import dev.workload.microservice.entity.TrainingWorkload;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
@Repository
public class InMemoryStorage {
    private final Map<String, TrainingWorkload> trainingWorkloadStorage = new HashMap<>();

    public Map<String, TrainingWorkload> getTrainingWorkloadStorage() {
        return trainingWorkloadStorage;
    }
}
