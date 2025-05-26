package dev.workload.microservice.service.interfaces;

import dev.workload.microservice.entity.TrainingWorkload;
import dev.workload.microservice.entity.TrainingWorkloadRequest;

public interface WorkloadService {
    TrainingWorkload addTrainingWorkload(TrainingWorkloadRequest trainingWorkload);
}
