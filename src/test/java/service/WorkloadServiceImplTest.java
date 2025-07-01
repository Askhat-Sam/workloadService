package service;

import dev.workload.microservice.model.TrainingWorkload;
import dev.workload.microservice.model.TrainingWorkloadData;
import dev.workload.microservice.repository.interfaces.TrainingWorkloadRepository;
import dev.workload.microservice.service.implementations.WorkloadServiceImpl;
import dev.workload.microservice.service.interfaces.WorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkloadServiceImplTest {
    private TrainingWorkloadRepository repository;
    private WorkloadService workloadService;
    private TrainingWorkload trainingWorkload;
    private TrainingWorkloadData trainingWorkloadData;

    @BeforeEach
    void setUp() {
        repository = mock(TrainingWorkloadRepository.class);
        workloadService = new WorkloadServiceImpl(repository);

        trainingWorkload = new TrainingWorkload();
        trainingWorkload.setTrainerUsername("john.doe");

        trainingWorkloadData = new TrainingWorkloadData();
        trainingWorkloadData.setTrainerUsername("john.doe");
        trainingWorkloadData.setTrainingDuration(60L);
        trainingWorkloadData.setTrainingDate(LocalDate.parse("2025-01-02"));
    }

    @Test
    void testAddTrainingWorkload_WhenTrainerExists_ShouldUpdateWorkload() {
        // Given
        TrainingWorkload mockTrainingWorkload = mock(TrainingWorkload.class);
        when(repository.get("john.doe")).thenReturn(mockTrainingWorkload);
        doNothing().when(mockTrainingWorkload).updateTrainingDuration(trainingWorkloadData);

        // When
        TrainingWorkload result = workloadService.addTrainingWorkload(trainingWorkloadData);

        // Then
        verify(mockTrainingWorkload, times(1)).updateTrainingDuration(trainingWorkloadData);
        verify(repository, times(1)).save(mockTrainingWorkload);
        assertEquals(mockTrainingWorkload, result);
    }

    @Test
    void testAddTrainingWorkload_WhenTrainerNotExists_ShouldCreateAndSaveWorkload() {
        // Given
        when(repository.get("jane.smith")).thenReturn(null);

        TrainingWorkload newTrainingWorkload = new TrainingWorkload();
        trainingWorkload.setTrainerUsername("jane.smith");
        mockStaticCreateWorkload(trainingWorkloadData, newTrainingWorkload);

        // When
        TrainingWorkload result = workloadService.addTrainingWorkload(trainingWorkloadData);

        // Then
        verify(repository).save(newTrainingWorkload);
        assertEquals(newTrainingWorkload, result);
    }

    @Test
    void testGetWorkloadSummary_WhenExists() {
        // Given
        when(repository.get("john.doe")).thenReturn(trainingWorkload);

        // When
        TrainingWorkload result = workloadService.getWorkloadSummary("john.doe");

        // Then
        assertNotNull(result);
        assertEquals(trainingWorkload, result);
    }

    @Test
    void testGetWorkloadSummary_WhenNotExists() {
        // Given
        when(repository.get("unknown")).thenReturn(null);

        // When
        TrainingWorkload result = workloadService.getWorkloadSummary("unknown");

        // Then
        assertNull(result);
    }

    private void mockStaticCreateWorkload(TrainingWorkloadData input, TrainingWorkload output) {
        mockStatic(TrainingWorkload.class).when(() ->
                TrainingWorkload.createTrainingWorkload(input)
        ).thenReturn(output);
    }
}
