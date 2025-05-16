package sejong.capston.yechef.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sejong.capston.yechef.global.login.jwt.JwtAuthenticationEntryPoint;
import sejong.capston.yechef.global.login.jwt.JwtFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // CSRF 비활성화 (쿠키 기반 인증에서는 CSRF 보호 필요할 수도 있음)
            .formLogin((auth) -> auth.disable())
            .httpBasic((auth) -> auth.disable())
            .exceptionHandling(e ->
                    e.authenticationEntryPoint(jwtAuthenticationEntryPoint))

            .authorizeHttpRequests(auth -> auth
                // ────── 토큰 없이도 허용할 엔드포인트 ──────
                .requestMatchers(
                        "/auth/**",
                        "/login",   // kakao 회원 가입 위한 외부인의 최초 접근 엔드포인트
                        "/favicon.ico", "/static/**", "/css/**", "/js/**", "/images/**"
                        "bot/chat", "bot/chat/**"   // gpt api 
                ).permitAll()

                // 관리자 전용
                .requestMatchers("/admin").hasRole("ADMIN")

                // 그 외는 인증 필요
                .anyRequest().authenticated()
        );


        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // JWT 사용하므로 세션 사용 안 함


        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가

        return http.build();

    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/favicon.ico",
                "/static/**",
                "/css/**",
                "/js/**",
                "/images/**"
        );
    }

}