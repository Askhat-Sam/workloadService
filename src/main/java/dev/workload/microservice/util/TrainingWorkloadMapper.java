package dev.workload.microservice.util;

import dev.workload.microservice.model.TrainingWorkloadData;
import dev.workload.microservice.model.dto.TrainingWorkloadRequest;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface TrainingWorkloadMapper {
    TrainingWorkloadData trainingWokrloddRequestToTrainingWorkloadData(TrainingWorkloadRequest trainingWorkloadRequest);
}
