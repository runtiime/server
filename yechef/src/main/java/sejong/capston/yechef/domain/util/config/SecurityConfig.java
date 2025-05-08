package sejong.capston.yechef.domain.util.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            // CSRF 비활성화
            .csrf(csrf -> csrf.disable())

            // URL별 권한 설정
            .authorizeHttpRequests(auth ->
                    auth
                            .requestMatchers("/bot/chat", "/bot/chat/**").permitAll()   // GPT용 엔드포인트만 인증 없이 허용
                            .anyRequest().authenticated()                 // 나머지 요청은 로그인(인증) 필요
            )

            // 기본 폼 로그인·HTTP Basic 비활성화
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

    return http.build();
  }
}


//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//  /*
//  @Bean
//  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//    http
//
//        .authorizeHttpRequests(auth -> auth
//            .requestMatchers(
//                "/login",
//                "/v3/api-docs/**",
//                "/swagger-ui.html",
//                "/swagger-ui/**"
//            ).permitAll()
//            .anyRequest().authenticated()
//        );
//    return http.build();
//  }*/
//
//  @Bean
//  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//    http
//        // CSRF 끄기
//        .csrf(csrf -> csrf.disable())
//
//        // 폼 로그인 기능 완전 비활성화
//        .formLogin(form -> form.disable())
//
//        // HTTP Basic 인증 비활성화
//        .httpBasic(basic -> basic.disable())
//
//        // 모든 요청을 인증 없이 허용
//        .authorizeHttpRequests(auth -> auth
//            .anyRequest().permitAll()
//        );
//
//    return http.build();
//  }
//}
