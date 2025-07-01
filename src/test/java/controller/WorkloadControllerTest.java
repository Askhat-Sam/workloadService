package controller;

import dev.workload.microservice.controller.WorkloadController;
import dev.workload.microservice.model.TrainingWorkload;
import dev.workload.microservice.model.TrainingWorkloadData;
import dev.workload.microservice.model.dto.TrainingWorkloadRequest;
import dev.workload.microservice.service.interfaces.WorkloadService;
import dev.workload.microservice.util.TrainingWorkloadMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class WorkloadControllerTest {
    private WorkloadService workloadService;
    private TrainingWorkloadMapper trainingWorkloadMapper;
    private WorkloadController workloadController;

    @BeforeEach
    void setUp() {
        workloadService = mock(WorkloadService.class);
        trainingWorkloadMapper = mock(TrainingWorkloadMapper.class);
        workloadController = new WorkloadController(workloadService, trainingWorkloadMapper);
    }

    @Test
    void updateWorkload_ShouldReturnAcceptedStatus() throws InterruptedException {
        // Given
        TrainingWorkloadRequest request = new TrainingWorkloadRequest();
        TrainingWorkloadData mappedData = new TrainingWorkloadData();
        mappedData.setTrainerUsername("john.doe");

        when(trainingWorkloadMapper.trainingWokrloddRequestToTrainingWorkloadData(request)).thenReturn(mappedData);

        // When
        ResponseEntity<String> response = workloadController.updateWorkload(request);

        // Then
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(trainingWorkloadMapper).trainingWokrloddRequestToTrainingWorkloadData(request);
        verify(workloadService).addTrainingWorkload(mappedData);
    }

    @Test
    void getWorkloadSummary_ShouldReturnCorrectWorkload() {
        // Given
        String username = "john.doe";
        TrainingWorkload expectedWorkload = new TrainingWorkload();
        when(workloadService.getWorkloadSummary(username)).thenReturn(expectedWorkload);

        // When
        TrainingWorkload result = workloadController.getWorkloadSummary(username);

        // Then
        assertEquals(expectedWorkload, result);
        verify(workloadService, times(1)).getWorkloadSummary(username);
    }
}
