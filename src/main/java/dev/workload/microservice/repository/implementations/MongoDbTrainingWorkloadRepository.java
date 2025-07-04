package dev.workload.microservice.repository.implementations;

import dev.workload.microservice.model.TrainingWorkload;
import dev.workload.microservice.model.TrainingWorkloadDocument;
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
        TrainingWorkloadDocument trainingWorkloadDocument = TrainingWorkloadDocument.fromTrainingWorkloadToTrainingWorkloadDocument(trainingWorkload);
        mongoTemplate.save(trainingWorkloadDocument);
    }

    @Override
    public TrainingWorkload get(String username) {
        Query query = new Query(Criteria.where("trainerUsername").is(username));
        TrainingWorkloadDocument document = mongoTemplate.findOne(query, TrainingWorkloadDocument.class);
        return document!=null?document.fromTrainingWorkloadDocumentToTrainingWorkload():null;
    }
}
