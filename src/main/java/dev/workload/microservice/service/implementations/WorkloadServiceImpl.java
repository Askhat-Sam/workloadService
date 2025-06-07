package dev.workload.microservice.service.implementations;

import dev.workload.microservice.model.TrainingWorkload;
import dev.workload.microservice.model.TrainingWorkloadData;
import dev.workload.microservice.repository.implementations.InMemoryTrainingWorkloadRepository;
import dev.workload.microservice.service.interfaces.WorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WorkloadServiceImpl implements WorkloadService {
    private final InMemoryTrainingWorkloadRepository inMemoryTrainingWorkloadRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public WorkloadServiceImpl(InMemoryTrainingWorkloadRepository inMemoryTrainingWorkloadRepository) {
        this.inMemoryTrainingWorkloadRepository = inMemoryTrainingWorkloadRepository;
    }

    @Override
    public TrainingWorkload addTrainingWorkload(TrainingWorkloadData trainingWorkloadData) {
        // Check if the trainer exist in DB
        TrainingWorkload trainingWorkload;
        if (inMemoryTrainingWorkloadRepository.get(trainingWorkloadData.getTrainerUsername()) != null) {
            trainingWorkload = inMemoryTrainingWorkloadRepository.get(trainingWorkloadData.getTrainerUsername());
            trainingWorkload.updateTrainingDuration(trainingWorkloadData);
            logger.info("Workload for the trainer : [" + trainingWorkloadData.getTrainerUsername() + "] has been updated " + inMemoryTrainingWorkloadRepository.get(trainingWorkloadData.getTrainerUsername()));
        } else { // if trainer not exist in DB
            // static method TrainingWorkload.createWorkload(trainingWorkloadRequest)
            trainingWorkload =TrainingWorkload.createTrainingWorkload(trainingWorkloadData);
            inMemoryTrainingWorkloadRepository.save(trainingWorkload);
            logger.info("Workload for the trainer : [" + trainingWorkloadData.getTrainerUsername() + "] has been added " + inMemoryTrainingWorkloadRepository.get(trainingWorkloadData.getTrainerUsername()));
        }
        return trainingWorkload;
    }

    @Override
    public TrainingWorkload getWorkloadSummary(String username)  {

        // Get TrainingWorkload by username
        TrainingWorkload trainingWorkload = inMemoryTrainingWorkloadRepository.get(username);

        // Check if the trainer has any training records
        if (trainingWorkload != null) {
            logger.info("Summary of training workload for trainer [" + username + "] has been queried and equal to - " + trainingWorkload);
            return trainingWorkload;
        } else{ // Return null if the trainer has no training records
            logger.info("Trainer: " + username + "] has no training records in DB");
            return null;
        }

    }
}

