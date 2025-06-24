package dev.workload.microservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;
@Document
@CompoundIndexes({
        @CompoundIndex(name = "first_last_name_idx", def = "{'trainerFirstName': 1, 'trainerLastName': 1}")
})
public class TrainingWorkload {
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

    public TrainingWorkload() {
    }

    public static TrainingWorkload createTrainingWorkload(TrainingWorkloadData trainingWorkloadData){
        Map<Integer, Map<Integer, Long>> durationMap = new HashMap<>();

        int year = trainingWorkloadData.getTrainingDate().getYear();
        int month = trainingWorkloadData.getTrainingDate().getMonthValue();

        durationMap.computeIfAbsent(year, y -> new HashMap<>()).merge(month, trainingWorkloadData.getTrainingDuration(), Long::sum);

        return new TrainingWorkload(trainingWorkloadData.getTrainerUsername(),
                trainingWorkloadData.getTrainerFirstName(),
                trainingWorkloadData.getTrainerLastName(),
                trainingWorkloadData.isActive(),
                durationMap);
    }


    public TrainingWorkload(String trainerUsername, String trainerFirstName, String trainerLastName, boolean trainerStatus, Map<Integer, Map<Integer, Long>> trainingDuration) {
        this.trainerUsername = trainerUsername;
        this.trainerFirstName = trainerFirstName;
        this.trainerLastName = trainerLastName;
        this.trainerStatus = trainerStatus;
        this.trainingDuration = trainingDuration;
    }

    public void updateTrainingDuration(TrainingWorkloadData trainingWorkloadData) {
        // Check if the year exists, if not, initialize it with an empty map for months
        trainingDuration.putIfAbsent(trainingWorkloadData.getTrainingDate().getYear(), new HashMap<>());

        // Get current value of the training duration for the month. If there's no current value, initialize it to 0
        Map<Integer, Long> monthlyMap = trainingDuration.get(trainingWorkloadData.getTrainingDate().getYear());
        Long currentDuration = monthlyMap.getOrDefault(trainingWorkloadData.getTrainingDate().getMonth().getValue(), 0L);

        // Add or subtract the trainingDurationValue based on actionType value
        switch (trainingWorkloadData.getActionType()) {
            case ADD ->  monthlyMap.put(trainingWorkloadData.getTrainingDate().getMonth().getValue(), currentDuration + trainingWorkloadData.getTrainingDuration());
            case DELETE -> monthlyMap.put(trainingWorkloadData.getTrainingDate().getMonth().getValue(), Math.max(0, currentDuration - trainingWorkloadData.getTrainingDuration())); // Used max() to handle cases when the trainingDuration becomes negative
            default -> throw new IllegalArgumentException("Unknown action type: " + trainingWorkloadData.getActionType()); // is required considering the usage of enum ActionType?
        }
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
    @Override
    public String toString() {
        return "TrainingWorkload{" +
                "trainerUsername='" + trainerUsername + '\'' +
                ", trainerFirstName='" + trainerFirstName + '\'' +
                ", trainerLastName='" + trainerLastName + '\'' +
                ", trainerStatus=" + trainerStatus +
                ", trainingDuration=" + trainingDuration +
                '}';
    }
}

