package component;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.workload.microservice.WorkloadServiceApplication;
import dev.workload.microservice.model.ActionType;
import dev.workload.microservice.model.dto.TrainingWorkloadRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration(classes = WorkloadServiceApplication.class)
@Testcontainers
public class WorkloadServiceComponentTest {
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
        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitmq::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitmq::getAdminPassword);
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
    ObjectMapper objectMapper;
    @Value("${test.jwt}")
    private String jwt;

    @Test
    void testWorkloadService() throws Exception {
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

        String requestBodyUpdateWorkload = objectMapper.writeValueAsString(trainingWorkloadRequest);

        // Call "/workload-service/updateWorkload" endpoint and ensure that the ok status is retuned
        mockMvc.perform(post("/workload-service/updateWorkload")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType("application/json")
                        .content(requestBodyUpdateWorkload))
                .andExpect(status().isOk())
                .andReturn();

        // Request trainerWorkload by trainer username
        mockMvc.perform(post("/workload-service/getWorkloadSummary")
                        .header("Authorization", "Bearer " + jwt)
                        .param("username", trainingWorkloadRequest.getTrainerUsername())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainingDuration.2025.12").value(44))
                .andReturn();

        // Add additional trainign to the same trainer and ensure that total workload duration is correctly summed for certain month
        TrainingWorkloadRequest secondTrainingWorkloadRequest = TrainingWorkloadRequest.builder()
                .trainerUsername("Vladislav.Bekmeev")
                .trainerFirstName("Vladislav")
                .trainerLastName("Bekmeev")
                .isActive(true)
                .trainingDate(LocalDate.of(2025, 12, 1))
                .trainingDuration(66L)
                .actionType(ActionType.ADD)
                .build();

        String requestBodyUpdateWorkloadSecond = objectMapper.writeValueAsString(secondTrainingWorkloadRequest);

        mockMvc.perform(post("/workload-service/updateWorkload")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType("application/json")
                        .content(requestBodyUpdateWorkloadSecond))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(post("/workload-service/getWorkloadSummary")
                        .header("Authorization", "Bearer " + jwt)
                        .param("username", trainingWorkloadRequest.getTrainerUsername())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainingDuration.2025.12").value(110))
                .andReturn();
    }




}
