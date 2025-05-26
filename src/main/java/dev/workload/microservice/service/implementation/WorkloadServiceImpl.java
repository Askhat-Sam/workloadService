package dev.workload.microservice.service.implementation;

import dev.workload.microservice.entity.TrainingWorkload;
import dev.workload.microservice.entity.TrainingWorkloadRequest;
import dev.workload.microservice.repository.InMemoryStorage;
import dev.workload.microservice.service.interfaces.WorkloadService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WorkloadServiceImpl implements WorkloadService {
    private final InMemoryStorage inMemoryStorage;

    public WorkloadServiceImpl(InMemoryStorage inMemoryStorage) {
        this.inMemoryStorage = inMemoryStorage;
    }

    @Override
    public TrainingWorkload addTrainingWorkload(TrainingWorkloadRequest trainingWorkloadRequest) {
        // Check if the trainer exist in DB
        Map<String, TrainingWorkload> trainingWorkloadMap = inMemoryStorage.getTrainingWorkloadStorage();
        TrainingWorkload trainingWorkload = new TrainingWorkload();

        if (trainingWorkloadMap.containsKey(trainingWorkloadRequest.getTrainerUsername())) {
            if (trainingWorkloadRequest.getActionType().equals("ADD")){
                trainingWorkloadMap.get(trainingWorkloadRequest.getTrainerUsername()).updateTrainingDuration(trainingWorkloadRequest.getActionType(),
                        trainingWorkloadRequest.getTrainingDate().getYear(), trainingWorkloadRequest.getTrainingDate().getMonth(), trainingWorkloadRequest.getTrainingDuration());
            } else {
                System.out.println("Add functionality for DELETE");
            }
        } else {
            Map<Integer, Map<Integer,Long>> trainingDuration = new HashMap<>();
            // Check if the year exists, if not add a new map for that year
            trainingDuration.putIfAbsent(trainingWorkloadRequest.getTrainingDate().getYear(), new HashMap<>());

            // Put the month-duration pair
            trainingDuration.get(trainingWorkloadRequest.getTrainingDate().getYear()).put(trainingWorkloadRequest.getTrainingDate().getMonth(), trainingWorkloadRequest.getTrainingDuration());

            trainingWorkload.setTrainerUserName(trainingWorkloadRequest.getTrainerUsername());
            trainingWorkload.setTrainerFirstName(trainingWorkloadRequest.getTrainerFirstName());
            trainingWorkload.setTrainerLastName(trainingWorkloadRequest.getTrainerLastName());
            trainingWorkload.setTrainerStatus(trainingWorkloadRequest.isActive());
            trainingWorkload.setTrainingDuration(trainingDuration);
            trainingWorkloadMap.put(trainingWorkloadRequest.getTrainerUsername(), trainingWorkload);
        }
        System.out.println("Current MAP: " + trainingWorkloadMap);
        return trainingWorkload;
    }
}
