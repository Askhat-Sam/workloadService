package dev.workload.microservice.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TrainingWorkloadTotalRequest {
    @NotBlank
    private String trainerUsername;
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be no earlier than 1900")
    @Max(value = 2100, message = "Year must be no later than 2100")
    private Integer year;
    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    public TrainingWorkloadTotalRequest(String trainerUsername, Integer year, Integer month) {
        this.trainerUsername = trainerUsername;
        this.year = year;
        this.month = month;
    }

    public TrainingWorkloadTotalRequest() {
    }

    public String getTrainerUsername() {
        return trainerUsername;
    }

    public void setTrainerUsername(String trainerUsername) {
        this.trainerUsername = trainerUsername;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    @Override
    public String toString() {
        return "TrainingWorkloadTotalRequest{" +
                "trainerUsername='" + trainerUsername + '\'' +
                ", year=" + year +
                ", month=" + month +
                '}';
    }
}
