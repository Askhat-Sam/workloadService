package dev.workload.microservice.controller;

import dev.workload.microservice.model.TrainingWorkload;
import dev.workload.microservice.model.TrainingWorkloadData;
import dev.workload.microservice.model.dto.TrainingWorkloadRequest;
import dev.workload.microservice.service.interfaces.WorkloadService;
import dev.workload.microservice.util.TrainingWorkloadMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workload-service/")
public class WorkloadController {
    private final WorkloadService workloadService;
    private final TrainingWorkloadMapper trainingWorkloadMapper;
    private static final Logger logger = LoggerFactory.getLogger(WorkloadController.class);

    public WorkloadController(WorkloadService workloadService, TrainingWorkloadMapper trainingWorkloadMapper) {
        this.workloadService = workloadService;
        this.trainingWorkloadMapper = trainingWorkloadMapper;
    }

    @PostMapping("/updateWorkload")
    public ResponseEntity<String> updateWorkload(@RequestBody TrainingWorkloadRequest trainingWorkloadRequest) throws InterruptedException {
        // Map the incoming TrainingWorkloadRequest to TrainingWorkloadData
        TrainingWorkloadData trainingWorkloadData = trainingWorkloadMapper.trainingWokrloddRequestToTrainingWorkloadData(trainingWorkloadRequest);

        logger.info("Workload for the trainer : " + trainingWorkloadData.getTrainerUsername() + " has been updated");

        // Add training workload to DB
        workloadService.addTrainingWorkload(trainingWorkloadData);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);

    }

    @PostMapping("/getWorkloadSummary")
    public TrainingWorkload getWorkloadSummary(@RequestParam String username) {
        TrainingWorkload trainingWorkload = workloadService.getWorkloadSummary(username);
        logger.info("Workload for the trainer : " + username + " has been retrieved from DB and equal to - " + trainingWorkload);
        return trainingWorkload;
    }

}
