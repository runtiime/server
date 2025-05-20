package sejong.capston.yechef.domain.Member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sejong.capston.yechef.domain.Member.Member;
import sejong.capston.yechef.domain.Member.repository.MemberRepository;
import sejong.capston.yechef.global.login.dto.KakaoResponseDto;
import sejong.capston.yechef.global.login.dto.LoginResultDto;
import sejong.capston.yechef.domain.Member.dto.LoginMemberDto;
import sejong.capston.yechef.global.login.jwt.JwtProvider;
import sejong.capston.yechef.global.login.service.KakaoOAuthService;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final KakaoOAuthService kakaoOAuthService;

    public LoginResultDto loginWithKakao(String code) {

        // Access Token 발급
        String accessToken = kakaoOAuthService.getKakaoAccessToken(code);
        log.info("카카오 Access Token : {} ", accessToken);

        // 카카오 유저 정보 요청
        KakaoResponseDto userInfo = kakaoOAuthService.getMemberInfoFromToken(accessToken);
        log.info("카카오 유저 정보 : {} ", userInfo);

        // 로그인 or 회원가입
        Member member = loginOrRegister(userInfo);
        log.info("로그인 or 회원가입 완료 : {}",member);

        // JWT 생성
        String jwt = jwtProvider.createAccessToken(LoginMemberDto.fromEntity(member));
        log.info("jwt 생성 완료 : {}", jwt);

        return new LoginResultDto(jwt, member.getId(), member.getNickname());
    }

    private Member loginOrRegister(KakaoResponseDto userInfo) {
        Member member = memberRepository.findByOauthId(userInfo.getOauthId());
        if (member == null) {
            return register(userInfo);
        }
        return member;
    }

    private Member register(KakaoResponseDto userInfo) {
        Member newMember = Member.builder()
                .oauthId(userInfo.getOauthId())
                .nickname(userInfo.getNickname())
                .role(Member.Role.USER)
                .build();
        return memberRepository.save(newMember);
    }
}
