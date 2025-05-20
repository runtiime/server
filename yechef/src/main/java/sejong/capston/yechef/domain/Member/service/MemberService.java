package sejong.capston.yechef.domain.Member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.capston.yechef.domain.Member.Member;
import sejong.capston.yechef.domain.Member.dto.MemberCreateDto;
import sejong.capston.yechef.domain.Member.dto.MemberDto;
import sejong.capston.yechef.domain.Member.dto.MemberUpdateDto;
import sejong.capston.yechef.domain.Member.repository.MemberRepository;
import sejong.capston.yechef.global.exception.BaseException;
import sejong.capston.yechef.global.exception.error.ErrorCode;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

  private final MemberRepository memberRepository;

  @Transactional
  public MemberDto createMember(MemberCreateDto createDto) {
    Member member = Member.builder()
        .nickname(createDto.getNickname())
        //.email(createDto.getEmail())
        .build();
    memberRepository.save(member);
    log.info("회원 생성 성공: id: {}", member.getId());
    return toDto(member);
  }

  @Transactional(readOnly = true)
  public MemberDto getMemberInfo(Long memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> {
          log.error("회원 조회 실패: memberId: {}", memberId);
          return BaseException.from(ErrorCode.MEMBER_NOT_FOUND);
        });
    log.info("회원 정보 조회 성공: userId: {}", memberId);
    return toDto(member);
  }

  @Transactional
  public MemberDto getMemberId(String nickname) {
    Member member = memberRepository.findByNickname(nickname)
            .orElseThrow(() -> {
              log.error("회원 조회 실패: name: {}", nickname);
              return BaseException.from(ErrorCode.MEMBER_NOT_FOUND);
            });
    return toDto(member);
  }

  @Transactional
  public MemberDto updateMember(Long memberId, MemberUpdateDto updateDto) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> BaseException.from(ErrorCode.MEMBER_NOT_FOUND));

    member.update(updateDto.getNickname());
    log.info("회원 정보 수정 성공: id: {}", memberId);
    return toDto(member);
  }

  @Transactional
  public void deleteMember(Long memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> BaseException.from(ErrorCode.MEMBER_NOT_FOUND));
    memberRepository.delete(member);
    log.info("회원 삭제 성공: id: {}", memberId);
  }

  private MemberDto toDto(Member member) {
    return MemberDto.builder()
        .id(member.getId())
        .nickname(member.getNickname())
        .build();
  }
}
