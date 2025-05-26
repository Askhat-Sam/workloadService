package dev.workload.microservice.controller;

import dev.workload.microservice.entity.TrainingWorkloadRequest;
import dev.workload.microservice.service.interfaces.WorkloadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workload-service/")
public class WorkloadController {
    private final WorkloadService workloadService;

    public WorkloadController(WorkloadService workloadService) {
        this.workloadService = workloadService;
    }

    @PostMapping("/updateWorkload")
    public String updateWorkload(@RequestBody TrainingWorkloadRequest trainingWorkloadRequest){

        System.out.println("Workload microservice has been CALLED!!!!!!!!!!!!!!!!!!");
        workloadService.addTrainingWorkload(trainingWorkloadRequest);

        return "This is reply from workload microservice";
    }

}
