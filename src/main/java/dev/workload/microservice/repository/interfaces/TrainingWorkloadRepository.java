package dev.workload.microservice.repository.interfaces;

import dev.workload.microservice.model.TrainingWorkload;

public interface TrainingWorkloadRepository {
    void save(TrainingWorkload trainingWorkload);
    TrainingWorkload get(String username);
}
