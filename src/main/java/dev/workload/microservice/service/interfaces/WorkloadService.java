package dev.workload.microservice.service.interfaces;

import dev.workload.microservice.model.TrainingWorkload;
import dev.workload.microservice.model.TrainingWorkloadData;
import dev.workload.microservice.model.dto.TrainingWorkloadRequest;
import dev.workload.microservice.model.dto.TrainingWorkloadTotalRequest;

public interface WorkloadService {
    TrainingWorkload addTrainingWorkload(TrainingWorkloadData trainingWorkloadData);

    TrainingWorkload getWorkloadSummary(String username);
}
