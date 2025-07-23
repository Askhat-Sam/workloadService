package integration.service;

import dev.workload.microservice.WorkloadServiceApplication;
import dev.workload.microservice.model.ActionType;
import dev.workload.microservice.model.TrainingWorkload;
import dev.workload.microservice.model.TrainingWorkloadData;
import dev.workload.microservice.service.interfaces.WorkloadService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = WorkloadServiceApplication.class)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WorkloadServiceIntegrationTestService {

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.9");

    @DynamicPropertySource
    static void overridePropertiesMongoDB(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private WorkloadService workloadService;

    private static final String TRAINER_USERNAME = "Vladislav.Bekmeev";

    private TrainingWorkloadData generateTrainingData(long duration) {
        return TrainingWorkloadData.builder()
                .trainerUsername(TRAINER_USERNAME)
                .trainerFirstName("Vladislav")
                .trainerLastName("Bekmeev")
                .isActive(true)
                .trainingDate(LocalDate.of(2025, 12, 31))
                .trainingDuration(duration)
                .actionType(ActionType.ADD)
                .build();
    }

    @Test
    @Order(1)
    void shouldAddTrainingWorkload() {
        TrainingWorkloadData data = generateTrainingData(44L);

        TrainingWorkload result = workloadService.addTrainingWorkload(data);

        assertThat(result).isNotNull();
        assertThat(result.getTrainerUsername()).isEqualTo(TRAINER_USERNAME);

        Map<Integer, Map<Integer, Long>> durationMap = result.getTrainingDuration();
        assertThat(durationMap).containsKey(2025);
        assertThat(durationMap.get(2025)).containsEntry(12, 44L);
    }

    @Test
    @Order(2)
    void shouldReturnWorkloadSummary() {
        TrainingWorkload summary = workloadService.getWorkloadSummary(TRAINER_USERNAME);

        assertThat(summary).isNotNull();
        assertThat(summary.getTrainerUsername()).isEqualTo(TRAINER_USERNAME);
        assertThat(summary.getTrainingDuration().get(2025).get(12)).isEqualTo(44L);
    }

    @Test
    @Order(3)
    void shouldReturnNullIfTrainerNotExist() {
        TrainingWorkload result = workloadService.getWorkloadSummary("Vladislav.Bekmeev777");

        assertThat(result).isNull();
    }
}

