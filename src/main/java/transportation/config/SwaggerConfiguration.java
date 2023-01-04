package transportation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

  @Bean
  public OpenAPI getOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Api для взаимодействия с сервисами платформы")
                .description(
                    "Сервис предоставляет полный набор инструментов для работы пользователя с платформой")
        );
  }
}
