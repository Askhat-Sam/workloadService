package dev.workload.microservice.entity;

import java.util.Map;

public class TrainingWorkload {
    private String trainerUserName;
    private String trainerFirstName;
    private String trainerLastName;
    private boolean trainerStatus;
    private Map<Integer, Map<Integer,Long>> trainingDuration;

    public void updateTrainingDuration(String ActionType, Integer year, Integer month, Long trainingDurationValue){
        trainingDuration.get(year).put(month, trainingDurationValue);
    }


    public TrainingWorkload() {
    }

    public TrainingWorkload(String trainerUserName, String trainerFirstName, String trainerLastName, boolean trainerStatus, Map<Integer, Map<Integer, Long>> trainingDuration) {
        this.trainerUserName = trainerUserName;
        this.trainerFirstName = trainerFirstName;
        this.trainerLastName = trainerLastName;
        this.trainerStatus = trainerStatus;
        this.trainingDuration = trainingDuration;
    }

    public String getTrainerUserName() {
        return trainerUserName;
    }

    public void setTrainerUserName(String trainerUserName) {
        this.trainerUserName = trainerUserName;
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
                "trainerUserName='" + trainerUserName + '\'' +
                ", trainerFirstName='" + trainerFirstName + '\'' +
                ", trainerLastName='" + trainerLastName + '\'' +
                ", trainerStatus=" + trainerStatus +
                ", trainingDuration=" + trainingDuration +
                '}';
    }
}
