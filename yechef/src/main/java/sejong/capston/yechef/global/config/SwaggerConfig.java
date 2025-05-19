package sejong.capston.yechef.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    final String securitySchemeName = "BearerAuth";

    return new OpenAPI()
        .info(new Info()
            .title("My API Docs")
            .version("1.0")
            .description("API 문서 설명"))
        .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)) // 🔐 보안 적용
        .components(new Components()
            .addSecuritySchemes(securitySchemeName,
                new SecurityScheme()
                    .name(securitySchemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")  // JWT 형식 명시
            )
        )
        .servers(List.of(
            new Server().url("http://localhost:8080").description("Local 개발 서버"),
            new Server().url("http://43.203.72.240:8080").description("운영 서버")
        ));
  }
}