package sejong.capston.yechef.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("My API Docs")
            .version("1.0")
            .description("API 문서 설명"))
        .servers(List.of(
            new Server().url("http://localhost:8080").description("Local 개발 서버"),
            new Server().url("http://43.203.72.240:8080").description("운영 서버")
        ));

  }
}





