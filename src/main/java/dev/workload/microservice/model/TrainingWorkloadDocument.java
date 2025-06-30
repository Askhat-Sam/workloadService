package dev.workload.microservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document("trainingWorkloads")
@CompoundIndexes({
        @CompoundIndex(name = "first_last_name_idx", def = "{'trainerFirstName': 1, 'trainerLastName': 1}")
})
public class TrainingWorkloadDocument {
    @Id
    @NotBlank
    private String trainerUsername;
    @NotBlank
    private String trainerFirstName;
    @NotBlank
    private String trainerLastName;
    @NotBlank
    private boolean trainerStatus;
    @NotNull
    private Map<Integer, Map<Integer, Long>> trainingDuration;

    public TrainingWorkloadDocument(String trainerUsername, String trainerFirstName, String trainerLastName, boolean trainerStatus, Map<Integer, Map<Integer, Long>> trainingDuration) {
        this.trainerUsername = trainerUsername;
        this.trainerFirstName = trainerFirstName;
        this.trainerLastName = trainerLastName;
        this.trainerStatus = trainerStatus;
        this.trainingDuration = trainingDuration;
    }

    public TrainingWorkloadDocument() {
    }

    public String getTrainerUsername() {
        return trainerUsername;
    }

    public void setTrainerUsername(String trainerUsername) {
        this.trainerUsername = trainerUsername;
    }

    public String getTrainerFirstName() {
        return trainerFirstName;
    }

    public void setTrainerFirstName(String trainerFirstName) {
        this.trainerFirstName = trainerFirstName;
    }

    public String getTrainerLastName() {
        return trainerLastName;
    }

    public void setTrainerLastName(String trainerLastName) {
        this.trainerLastName = trainerLastName;
    }

    public boolean isTrainerStatus() {
        return trainerStatus;
    }

    public void setTrainerStatus(boolean trainerStatus) {
        this.trainerStatus = trainerStatus;
    }

    public Map<Integer, Map<Integer, Long>> getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(Map<Integer, Map<Integer, Long>> trainingDuration) {
        this.trainingDuration = trainingDuration;
    }

    // Mapper method from TrainingWorkload to TrainingWorkloadDocument
    public static TrainingWorkloadDocument fromTrainingWorkloadToTrainingWorkloadDocument(TrainingWorkload trainingWorkload) {
        return new TrainingWorkloadDocument(
                trainingWorkload.getTrainerUsername(),
                trainingWorkload.getTrainerFirstName(),
                trainingWorkload.getTrainerLastName(),
                trainingWorkload.isTrainerStatus(),
                trainingWorkload.getTrainingDuration()
        );
    }
    // Mapper method from TrainingWorkloadDocument to TrainingWorkload
    public TrainingWorkload fromTrainingWorkloadDocumentToTrainingWorkload() {
        return new TrainingWorkload(
                this.trainerUsername,
                this.trainerFirstName,
                this.trainerLastName,
                this.trainerStatus,
                this.trainingDuration
        );
    }
}