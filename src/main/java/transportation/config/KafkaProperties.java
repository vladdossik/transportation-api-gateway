package transportation.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaProperties {

  private String bootstrapAddress;
  private String groupId;
  private String apiTimeout;
  private String autoCommitInterval;
  private String enableAutoCommit;
  private String autoOffsetReset;
  private String securityProtocol;
}
