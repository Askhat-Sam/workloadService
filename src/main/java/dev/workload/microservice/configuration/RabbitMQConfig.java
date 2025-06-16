package dev.workload.microservice.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitMQConfig {
    @Value("${rabbitmq.update.queue-name}")
    private String updateQueue;
    @Value("${rabbitmq.update.exchange-name}")
    private String exchangeName;
    @Value("${rabbitmq.update.routing-key}")
    private String updateRoutingKey;
    @Value("${rabbitmq.get-summary.queue-name}")
    private String summaryRequestQueue;
    @Value("${rabbitmq.get-summary.routing-key}")
    private String summaryRequestRoutingKey;

    // Spring bean for update queue
    @Bean
    public Queue updateQueue() {
        return new Queue(updateQueue, true);
    }

    // Spring bean for summary request queue
    @Bean
    public Queue summaryRequestQueue() {
        return new Queue(summaryRequestQueue, true);
    }

    // Spring bean for rabbitmq exchange of type direct
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    // Binding between update queue and exchange using routing key routingKey
    @Bean
    public Binding updateBinding() {
        return BindingBuilder.bind(updateQueue()).to(exchange()).with(updateRoutingKey);
    }
    // Binding between summary request queue and exchange using routing key routingKey
    @Bean
    public Binding summaryRequestBinding() {
        return BindingBuilder.bind(summaryRequestQueue()).to(exchange()).with(summaryRequestRoutingKey);
    }
    // JSON converter for sending/receiving POJOs
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter();
        jsonConverter.setClassMapper(classMapper());
        return jsonConverter;
    }

    //Providing custom mapping
    @Bean
    public DefaultClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("dev.gymService.model.dto.TrainingWorkloadRequest", dev.workload.microservice.model.dto.TrainingWorkloadRequest.class);
        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }
}
