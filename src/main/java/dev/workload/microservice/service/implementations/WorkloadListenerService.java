package dev.workload.microservice.service.implementations;

import dev.workload.microservice.model.TrainingWorkload;
import dev.workload.microservice.model.TrainingWorkloadData;
import dev.workload.microservice.model.dto.TrainingWorkloadRequest;
import dev.workload.microservice.service.interfaces.WorkloadService;
import dev.workload.microservice.util.TrainingWorkloadMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class WorkloadListenerService {
    private final WorkloadService workloadService;
    private final TrainingWorkloadMapper trainingWorkloadMapper;

    public WorkloadListenerService(WorkloadService workloadService, TrainingWorkloadMapper trainingWorkloadMapper) {
        this.workloadService = workloadService;
        this.trainingWorkloadMapper = trainingWorkloadMapper;
    }

    @RabbitListener(queues = "${rabbitmq.update.queue-name}", containerFactory = "rabbitListenerContainerFactory")
    public void addTrainingWorkload(TrainingWorkloadRequest request) {
        TrainingWorkloadData data = trainingWorkloadMapper.trainingWokrloddRequestToTrainingWorkloadData(request);
        workloadService.addTrainingWorkload(data);
    }

    @RabbitListener(queues = "${rabbitmq.get-summary.queue-name}", containerFactory = "rabbitListenerContainerFactory")
    public TrainingWorkload getWorkloadSummary(TrainingWorkloadRequest request) {
        return workloadService.getWorkloadSummary(request.getTrainerUsername());
    }
}
