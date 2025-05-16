package sejong.capston.yechef.global.login.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import sejong.capston.yechef.global.exception.BaseException;
import sejong.capston.yechef.global.exception.error.ErrorCode;
import sejong.capston.yechef.global.config.KakaoConfig;
import sejong.capston.yechef.global.login.dto.KakaoResponseDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoOAuthService {
    private final KakaoConfig kakaoConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void validateConfig() {
        kakaoConfig.validate(); // 애플리케이션 시작 시 호출
    }

    public String getKakaoAccessToken(String code) {
        String requestURL = kakaoConfig.getToken_uri();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoConfig.getClientId());
        params.add("redirect_uri", kakaoConfig.getRedirect_uri());
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, createHeaders());

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    requestURL, HttpMethod.POST, requestEntity, String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("access_token").asText();
        }catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("카카오 토큰 요청 실패 - 서버 오류: {}", e.getMessage());
            throw BaseException.from(ErrorCode.KAKAO_TOKEN_REQUEST_FAILED);
        } catch (Exception e) {
            log.error("카카오 토큰 요청 실패: {}", e.getMessage());
            throw BaseException.from(ErrorCode.KAKAO_TOKEN_REQUEST_FAILED);
        }
    }

    public KakaoResponseDto getUserInfoFromToken(String accessToken) {
        String url = kakaoConfig.getUser_info_uri();
        KakaoResponseDto kakaoResponseDto = null;

        // HTTP 요청 헤더 설정
        HttpHeaders headers = createHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            // API 요청 실행
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            // JSON 응답 파싱
            JsonNode root = objectMapper.readTree(response.getBody());
            String username = root.path("properties").path("nickname").asText();
            Long oauthId = root.path("id").asLong();

            // 사용자 정보 저장
            kakaoResponseDto = new KakaoResponseDto(oauthId,username);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("카카오 API 호출 실패 - 서버 오류: {}", e.getMessage());
            throw BaseException.from(ErrorCode.KAKAO_USER_INFO_REQUEST_FAILED);
        } catch (Exception e) {
            log.error("카카오 사용자 정보 요청 중 예기치 못한 오류 발생: {}", e.getMessage());
            throw BaseException.from(ErrorCode.KAKAO_USER_INFO_REQUEST_FAILED);
        }

        return kakaoResponseDto;
    }
    public String getKakaoAuthUrl() {
        return kakaoConfig.getAuthorization_uri()
                + "?client_id=" + kakaoConfig.getClientId()
                + "&redirect_uri=" + kakaoConfig.getRedirect_uri()
                + "&response_type=code"
                + "&scope=" + kakaoConfig.getScope();

    }
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }
}
