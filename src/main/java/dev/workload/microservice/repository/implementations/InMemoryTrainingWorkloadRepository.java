package dev.workload.microservice.repository.implementations;

import dev.workload.microservice.model.TrainingWorkload;
import dev.workload.microservice.repository.interfaces.TrainingWorkloadRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


public class InMemoryTrainingWorkloadRepository implements TrainingWorkloadRepository {
    private final Map<String, TrainingWorkload> inMemoryStorage = new HashMap<>();

    @Override
    public void save(TrainingWorkload trainingWorkload) {
        inMemoryStorage.put(trainingWorkload.getTrainerUsername(), trainingWorkload);
    }

    @Override
    public TrainingWorkload get(String username) {
        return inMemoryStorage.get(username);
    }

    @Override
    public String toString() {
        return "InMemoryTrainingWorkloadRepository{" +
                "inMemoryStorage=" + inMemoryStorage +
                '}';
    }
}
