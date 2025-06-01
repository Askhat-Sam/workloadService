package dev.workload.microservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.HashMap;
import java.util.Map;

public class TrainingWorkload {
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

    public TrainingWorkload(String trainerUsername, String trainerFirstName, String trainerLastName, boolean trainerStatus, Map<Integer, Map<Integer, Long>> trainingDuration) {
        this.trainerUsername = trainerUsername;
        this.trainerFirstName = trainerFirstName;
        this.trainerLastName = trainerLastName;
        this.trainerStatus = trainerStatus;
        this.trainingDuration = trainingDuration;
    }

    public void updateTrainingDuration(String actionType, Integer year, Integer month, Long trainingDurationValue) {
        // Check if the year exists, if not, initialize it with an empty map for months
        trainingDuration.putIfAbsent(year, new HashMap<>());

        // Get current value of the training duration for the month. If there's no current value, initialize it to 0
        Map<Integer, Long> monthlyMap = trainingDuration.get(year);
        Long currentDuration = monthlyMap.getOrDefault(month, 0L);

        // Add or subtract the trainingDurationValue based on actionType value
        switch (actionType) {
            case "ADD" -> monthlyMap.put(month, currentDuration + trainingDurationValue);
            case "DELETE" -> monthlyMap.put(month, Math.max(0, currentDuration - trainingDurationValue)); // Used max() to handle cases when the trainingDuration becomes negative
            default -> throw new IllegalArgumentException("Unknown action type: " + actionType);
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

