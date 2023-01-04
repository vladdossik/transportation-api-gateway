package transportation.config;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;


@Slf4j
@EnableKafka
@Configuration
public class KafkaConfig {

  @Value("${encryption.secret}")
  private String secret;
  @Value("${encryption.salt}")
  private String salt;

  @Bean
  public KafkaProperties kafkaProperties(
      @Value(value = "${kafka.bootstrapAddress}") String bootstrapAddress,
      @Value(value = "${kafka.groupId}") String groupId,
      @Value(value = "${kafka.properties.api.timeout.ms}") String apiTimeout,
      @Value(value = "${kafka.properties.auto.commit.interval.ms}") String autoCommitInterval,
      @Value(value = "${kafka.properties.enable.auto.commit}") String enableAutoCommit,
      @Value(value = "${kafka.properties.auto.offset.reset}") String autoOffsetReset,
      @Value(value = "${kafka.properties.security.protocol}") String securityProtocol
  ) {
    KafkaProperties properties = new KafkaProperties();
    properties.setBootstrapAddress(bootstrapAddress);
    properties.setGroupId(groupId);
    properties.setApiTimeout(apiTimeout);
    properties.setAutoCommitInterval(autoCommitInterval);
    properties.setEnableAutoCommit(enableAutoCommit);
    properties.setAutoOffsetReset(autoOffsetReset);
    properties.setSecurityProtocol(securityProtocol);
    return properties;
  }

  @Bean
  public ProducerFactory<String, KafkaTopicData> producerFactory(
      KafkaProperties kafkaProperties,
      KafkaSSLProperties kafkaSSLProperties
  ) {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapAddress());
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    securitySettings(kafkaProperties, kafkaSSLProperties, configProps);
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  public KafkaTemplate<String, KafkaTopicData> kafkaTemplate(
      ProducerFactory<String, KafkaTopicData> producerFactory
  ) {
    return new KafkaTemplate<>(producerFactory);
  }

  @Bean
  public ConsumerFactory<String, CompositeData> consumerSystemKey(
      KafkaProperties kafkaProperties,
      KafkaSSLProperties kafkaSSLProperties
  ) {
    return new DefaultKafkaConsumerFactory<>(
        getPropertyConsumer(kafkaProperties, kafkaSSLProperties)
    );
  }

  @Bean
  public KafkaListenerErrorHandler kafkaErrorHandler() {
    return ((message, e) -> {

      log.error("Error in kafka request listener: {}", e.getMessage());
      return message + ". " + e.getMessage();
    });
  }

  private Map<String, Object> getPropertyConsumer(
      KafkaProperties kafkaProperties,
      KafkaSSLProperties kafkaSSLProperties
  ) {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapAddress());
    configProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupId());
    configProps.put(ConsumerConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, kafkaProperties.getApiTimeout());
    configProps.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,
        kafkaProperties.getAutoCommitInterval());
    configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
        kafkaProperties.getEnableAutoCommit());
    configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getAutoOffsetReset());
    configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        ErrorHandlingDeserializer.class);
    configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
    configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,

        configProps.put(JsonDeserializer.TRUSTED_PACKAGES,
            "transporation.*"));
    securitySettings(kafkaProperties, kafkaSSLProperties, configProps);
    return configProps;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, CompositeData> kafkaListenerSystemsCompositeKeysContainerFactory(
      @Autowired ConsumerFactory<String, CompositeData> consumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, CompositeData> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    return factory;
  }

  private void securitySettings(
      KafkaProperties kafkaProperties,
      KafkaSSLProperties kafkaSSLProperties,
      Map<String, Object> configProps
  ) {
    String securityProtocol = kafkaProperties.getSecurityProtocol().toUpperCase();
    if (securityProtocol.equals("SSL") || securityProtocol.equals("SASL_SSL")) {
      configProps.put("security.protocol", kafkaProperties.getSecurityProtocol());
      configProps.put("ssl.key.password", kafkaSSLProperties.getKeyPassword());
      configProps.put("ssl.keystore.location", kafkaSSLProperties.getKeyStoreLocation());
      configProps.put("ssl.keystore.password", kafkaSSLProperties.getKeyStorePassword());
      configProps.put("ssl.keystore.type", kafkaSSLProperties.getKeyStoreType());
      configProps.put("ssl.truststore.location", kafkaSSLProperties.getTrustStoreLocation());
      configProps.put("ssl.truststore.password", kafkaSSLProperties.getTrustStorePassword());
      configProps.put("ssl.truststore.type", kafkaSSLProperties.getTrustStoreType());
      configProps.put("ssl.protocol", kafkaSSLProperties.getProtocol());
    }
  }
}