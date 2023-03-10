package transportation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
public class TransportationApiGatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(TransportationApiGatewayApplication.class, args);
  }
}
