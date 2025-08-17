package pay.domain.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    public static final String DEPOSIT_NOTIFICATION_TOPIC = "deposit.created.notification";
    public static final String TRANSFER_NOTIFICATION_TOPIC = "transfer.created.notification";
    public static final String SIGN_UP_NOTIFICATION_TOPIC = "signup.notification";
    public static final String SIGN_IN_NOTIFICATION_TOPIC = "signin.notification";

    public KafkaConfig(KafkaProperties kafkaProperties) { this.kafkaProperties = kafkaProperties;}

    @Bean
    NewTopic depositCreatedNotificationTopic(){
        return TopicBuilder.name(DEPOSIT_NOTIFICATION_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    NewTopic transferCreatedNotificationTopic(){
        return TopicBuilder.name(TRANSFER_NOTIFICATION_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    NewTopic signUpNotificationTopic(){
        return TopicBuilder.name(SIGN_UP_NOTIFICATION_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    NewTopic signInNotificationTopic(){
        return TopicBuilder.name(SIGN_IN_NOTIFICATION_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrap().servers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
