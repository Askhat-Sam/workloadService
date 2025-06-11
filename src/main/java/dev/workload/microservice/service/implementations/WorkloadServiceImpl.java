package dev.workload.microservice.service.implementations;

import dev.workload.microservice.model.TrainingWorkload;
import dev.workload.microservice.model.TrainingWorkloadData;
import dev.workload.microservice.repository.interfaces.TrainingWorkloadRepository;
import dev.workload.microservice.service.interfaces.WorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WorkloadServiceImpl implements WorkloadService {
    private final TrainingWorkloadRepository trainingWorkloadRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public WorkloadServiceImpl(TrainingWorkloadRepository trainingWorkloadRepository) {
        this.trainingWorkloadRepository = trainingWorkloadRepository;
    }

    @Override
    public TrainingWorkload addTrainingWorkload(TrainingWorkloadData trainingWorkloadData) {
        // Check if the trainer exist in DB
        TrainingWorkload trainingWorkload =trainingWorkloadRepository.get(trainingWorkloadData.getTrainerUsername());

        if (trainingWorkload != null) {
            trainingWorkload.updateTrainingDuration(trainingWorkloadData);
            trainingWorkloadRepository.save(trainingWorkload);
            logger.info("Workload for the trainer : [" + trainingWorkloadData.getTrainerUsername() + "] has been updated " + trainingWorkloadRepository.get(trainingWorkloadData.getTrainerUsername()));
        } else { // if trainer not exist in DB
            // static method TrainingWorkload.createWorkload(trainingWorkloadRequest)
            trainingWorkload =TrainingWorkload.createTrainingWorkload(trainingWorkloadData);
            trainingWorkloadRepository.save(trainingWorkload);
            logger.info("Workload for the trainer : [" + trainingWorkloadData.getTrainerUsername() + "] has been added " + trainingWorkloadRepository.get(trainingWorkloadData.getTrainerUsername()));
        }
        return trainingWorkload;
    }

    @Override
    public TrainingWorkload getWorkloadSummary(String username)  {

        // Get TrainingWorkload by username
        TrainingWorkload trainingWorkload = trainingWorkloadRepository.get(username);

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

