package sejong.capston.yechef.domain.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.capston.yechef.domain.util.exception.CustomException;
import sejong.capston.yechef.domain.util.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

  private final MemberRepository memberRepository;

  @Transactional(readOnly = true)
  public MemberDTO getMemberInfo(Long memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> {
          log.error("회원 조회 실패: memberId: {}", memberId);
          return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        });
    log.info("회원 정보 조회 성공: userId: {}", memberId);
    return MemberDTO.builder()
        .id(member.getId())
        .nickname(member.getNickname())
        .email(member.getEmail())
        .build();
  }

}
