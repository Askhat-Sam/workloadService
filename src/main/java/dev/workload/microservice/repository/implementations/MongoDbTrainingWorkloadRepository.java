package dev.workload.microservice.repository.implementations;

import dev.workload.microservice.model.TrainingWorkload;
import dev.workload.microservice.repository.interfaces.TrainingWorkloadRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class MongoDbTrainingWorkloadRepository implements TrainingWorkloadRepository   {
    private final MongoTemplate mongoTemplate;

    public MongoDbTrainingWorkloadRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(TrainingWorkload trainingWorkload) {
        mongoTemplate.save(trainingWorkload);
    }

    @Override
    public TrainingWorkload get(String username) {
        Query query = new Query(Criteria.where("trainerUsername").is(username));
        return mongoTemplate.findOne(query, TrainingWorkload.class);
    }
}
