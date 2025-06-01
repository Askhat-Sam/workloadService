package dev.workload.microservice.service.implementations;

import dev.workload.microservice.model.TrainingWorkload;
import dev.workload.microservice.model.dto.TrainingWorkloadRequest;
import dev.workload.microservice.model.dto.TrainingWorkloadTotalRequest;
import dev.workload.microservice.repository.InMemoryStorage;
import dev.workload.microservice.service.interfaces.WorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WorkloadServiceImpl implements WorkloadService {
    private final InMemoryStorage inMemoryStorage;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public WorkloadServiceImpl(InMemoryStorage inMemoryStorage) {
        this.inMemoryStorage = inMemoryStorage;
    }

    @Override
    public TrainingWorkload addTrainingWorkload(TrainingWorkloadRequest trainingWorkloadRequest) {
        // Check if the trainer exist in DB
        Map<String, TrainingWorkload> trainingWorkloadMap = inMemoryStorage.getTrainingWorkloadStorage();
        TrainingWorkload trainingWorkload = new TrainingWorkload();

        if (trainingWorkloadMap.containsKey(trainingWorkloadRequest.getTrainerUsername())) {
            trainingWorkloadMap.get(trainingWorkloadRequest.getTrainerUsername())
                    .updateTrainingDuration(trainingWorkloadRequest.getActionType().toString(),
                            trainingWorkloadRequest.getTrainingDate().getYear(),
                            trainingWorkloadRequest.getTrainingDate().getMonth().getValue(),
                            trainingWorkloadRequest.getTrainingDuration());
            logger.info("Workload for the trainer : [" + trainingWorkloadRequest.getTrainerUsername() + "] has been updated" + trainingWorkloadMap.get(trainingWorkloadRequest.getTrainerUsername()));
        } else {
            Map<Integer, Map<Integer, Long>> trainingDuration = new HashMap<>();
            // Check if the year exists, if not add a new map for that year
            trainingDuration.putIfAbsent(trainingWorkloadRequest.getTrainingDate().getYear(), new HashMap<>());

            // Put the month-duration pair
            trainingDuration.get(trainingWorkloadRequest.getTrainingDate().getYear()).put(trainingWorkloadRequest.getTrainingDate().getMonth().getValue(), trainingWorkloadRequest.getTrainingDuration());

            trainingWorkload.setTrainerUsername(trainingWorkloadRequest.getTrainerUsername());
            trainingWorkload.setTrainerFirstName(trainingWorkloadRequest.getTrainerFirstName());
            trainingWorkload.setTrainerLastName(trainingWorkloadRequest.getTrainerLastName());
            trainingWorkload.setTrainerStatus(trainingWorkloadRequest.isActive());
            trainingWorkload.setTrainingDuration(trainingDuration);
            trainingWorkloadMap.put(trainingWorkloadRequest.getTrainerUsername(), trainingWorkload);
            logger.info("Workload for the trainer : [" + trainingWorkloadRequest.getTrainerUsername() + "] has been added and equal to "+ trainingWorkloadMap.get(trainingWorkloadRequest.getTrainerUsername()));
        }
        return trainingWorkload;
    }

    @Override
    public Long getMonthlyWorkload(TrainingWorkloadTotalRequest trainingWorkloadTotalRequest) {
        TrainingWorkload trainingWorkload  =  inMemoryStorage.getTrainingWorkloadStorage().get(trainingWorkloadTotalRequest.getTrainerUsername());

        // Check if the trainer has any training records
        if (trainingWorkload!=null){
            Long monthlyWorkload = trainingWorkload.getTrainingDuration().get(trainingWorkloadTotalRequest.getYear()).get(trainingWorkloadTotalRequest.getMonth());
            logger.info("Monthly workload for the trainer : [" + trainingWorkloadTotalRequest.getTrainerUsername() + "] has been queried and equal to - " + (monthlyWorkload != null ? monthlyWorkload : 0));
            return monthlyWorkload;
        }
        // Return 0 if the trainer has no training records
        return 0L;
    }
}

