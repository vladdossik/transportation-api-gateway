package transportation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kafka.properties.ssl")
public class KafkaSSLProperties {

  private String keyPassword;
  private String keyStoreLocation;
  private String keyStorePassword;
  private String keyStoreType;
  private String trustStoreLocation;
  private String trustStorePassword;
  private String trustStoreType;
  private String protocol;

}
