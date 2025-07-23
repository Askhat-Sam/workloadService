package integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.workload.microservice.WorkloadServiceApplication;
import dev.workload.microservice.model.ActionType;
import dev.workload.microservice.model.TrainingWorkload;
import dev.workload.microservice.model.dto.TrainingWorkloadRequest;
import dev.workload.microservice.repository.interfaces.TrainingWorkloadRepository;
import dev.workload.microservice.service.interfaces.WorkloadService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration(classes = WorkloadServiceApplication.class)
@Testcontainers
public class WorkloadServiceIntegrationTestController {
    @Autowired
    private MockMvc mockMvc;
    @Container
    static final RabbitMQContainer rabbitmq = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.11.11-alpine"));

    @DynamicPropertySource
    static void overridePropertiesRabbitMQ(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitmq::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitmq::getAdminPassword);
    }

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.9");

    @DynamicPropertySource
    static void overridePropertiesMongoDB(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeAll
    static void beforeAll(){
        mongoDBContainer.start();
        rabbitmq.start();
    }

    @AfterAll
    static void afterAll(){
        mongoDBContainer.stop();
        rabbitmq.stop();
    }
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WorkloadService workloadService;
    @Value("${test.jwt}")
    private String jwt;

    @Autowired
    private TrainingWorkloadRepository repository;

    @Test
    void updateWorkloadCorrectlyPersistedIntoDatabase() throws Exception {
        // Create TrainingWorkloadRequest using builder pattern
        TrainingWorkloadRequest trainingWorkloadRequest = TrainingWorkloadRequest.builder()
                .trainerUsername("Vladislav.Bekmeev")
                .trainerFirstName("Vladislav")
                .trainerLastName("Bekmeev")
                .isActive(true)
                .trainingDate(LocalDate.of(2025, 12, 31))
                .trainingDuration(44L)
                .actionType(ActionType.ADD)
                .build();

        String jsonRequest = objectMapper.writeValueAsString(trainingWorkloadRequest);


        mockMvc.perform(post("/workload-service/updateWorkload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content(jsonRequest))
                .andExpect(status().isOk());


        mockMvc.perform(post("/workload-service/getWorkloadSummary")
                        .header("Authorization", "Bearer " + jwt)
                        .param("username", "Vladislav.Bekmeev")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainingDuration.2025.12").value(44));

        // Assert that training workload is persisted into mongoDB database
        TrainingWorkload stored = repository.get("Vladislav.Bekmeev");
        assertNotNull(stored);
        assertEquals(44L, stored.getTrainingDuration().get(2025).get(12));
    }
    @Test
    void callingGetWorkloadSummaryForNotExistingTrainingShouldReturnFailureMessage() throws Exception {
        // Try to request getWorkloadSummary for not existing trainer
        mockMvc.perform(post("/workload-service/getWorkloadSummary")
                        .header("Authorization", "Bearer " + jwt)
                        .param("username", "Vladislav.Bekmeev777")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        // Assert that training workload is not exist in mongoDB database
        TrainingWorkload stored = repository.get("Vladislav.Bekmeev777");
        assertNull(stored);
    }
}
