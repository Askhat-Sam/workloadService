package dev.workload.microservice.controller;

import dev.workload.microservice.model.dto.TrainingWorkloadRequest;
import dev.workload.microservice.model.dto.TrainingWorkloadTotalRequest;
import dev.workload.microservice.service.interfaces.WorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workload-service/")
public class WorkloadController {
    private final WorkloadService workloadService;
    private static final Logger logger = LoggerFactory.getLogger(WorkloadController.class);

    public WorkloadController(WorkloadService workloadService) {
        this.workloadService = workloadService;
    }

    @PostMapping("/updateWorkload")
    public ResponseEntity<String> updateWorkload(@RequestBody TrainingWorkloadRequest trainingWorkloadRequest) {

        // Get transactionId
        String transactionId = MDC.get("transactionId");

        logger.info("Received request to update workload | transactionId={}", transactionId);

        // Add training workload to DB
        workloadService.addTrainingWorkload(trainingWorkloadRequest);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/getMonthlyWorkload")
    public Long getMonthlyWorkload(@RequestBody TrainingWorkloadTotalRequest trainingWorkloadTotalRequest) {
        return workloadService.getMonthlyWorkload(trainingWorkloadTotalRequest);
    }

}
