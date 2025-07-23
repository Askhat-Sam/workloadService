package integration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.workload.microservice.WorkloadServiceApplication;
import dev.workload.microservice.model.ActionType;
import dev.workload.microservice.model.TrainingWorkload;
import dev.workload.microservice.model.dto.TrainingWorkloadRequest;
import dev.workload.microservice.repository.interfaces.TrainingWorkloadRepository;
import org.junit.jupiter.api.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = WorkloadServiceApplication.class)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WorkloadListenerServiceIntegrationTest {

    @Container
    static final RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3.11.11-alpine");

    @Container
    static final MongoDBContainer mongoDB = new MongoDBContainer("mongo:7.0.9");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitmq::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitmq::getAdminPassword);
        registry.add("spring.data.mongodb.uri", mongoDB::getReplicaSetUrl);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${rabbitmq.update.queue-name}")
    private String updateQueue;

    @Value("${rabbitmq.get-summary.queue-name}")
    private String summaryQueue;

    @Autowired
    private TrainingWorkloadRepository repository;

    private TrainingWorkloadRequest trainingWorkloadRequest;

    @BeforeEach
    public void  generateTrainingWokrloadRequest(){
         trainingWorkloadRequest = TrainingWorkloadRequest.builder()
                .trainerUsername("Vladislav.Bekmeev")
                .trainerFirstName("Vladislav")
                .trainerLastName("Bekmeev")
                .isActive(true)
                .trainingDate(LocalDate.of(2025, 12, 31))
                .trainingDuration(44L)
                .actionType(ActionType.ADD)
                .build();
    }


    @Test
    @Order(1)
    void shouldAddTrainingWorkloadViaListener() {
        // Simulate the request from GymService microservice though rabbitMQ
        rabbitTemplate.convertAndSend(updateQueue, trainingWorkloadRequest);

        // Await 1 second so the message from rabbitMq will be consumed by workload-service listener
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}

        TrainingWorkload stored = repository.get("Vladislav.Bekmeev");
        assertNotNull(stored);
        assertEquals(44L, stored.getTrainingDuration().get(2025).get(12));
    }

    @Test
    @Order(2)
    void shouldReturnTrainingSummaryViaListener() {
        TrainingWorkload response = (TrainingWorkload) rabbitTemplate.convertSendAndReceive(summaryQueue, trainingWorkloadRequest);

        assertThat(response).isNotNull();
        assertThat(response.getTrainerUsername()).isEqualTo("Vladislav.Bekmeev");
        assertThat(response.getTrainingDuration().get(2025).get(12)).isEqualTo(44L);
    }
}
