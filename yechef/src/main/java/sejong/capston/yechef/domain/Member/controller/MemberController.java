package sejong.capston.yechef.domain.Member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sejong.capston.yechef.domain.Member.service.MemberService;
import sejong.capston.yechef.domain.Member.dto.MemberCreateDto;
import sejong.capston.yechef.domain.Member.dto.MemberDto;
import sejong.capston.yechef.domain.Member.dto.MemberUpdateDto;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "회원 관리 API", description = "회원 관리 API 제공")
public class MemberController {

  private final MemberService memberService;

  @Operation(summary = "회원 생성")
  @PostMapping
  public ResponseEntity<MemberDto> createMember(@RequestBody MemberCreateDto createDto) {
    MemberDto result = memberService.createMember(createDto);
    return ResponseEntity.created(URI.create("/api/users/" + result.getId())).body(result);
  }

  @Operation(summary = "회원 정보 조회")
  @GetMapping("/{memberId}")
  public ResponseEntity<MemberDto> getMemberInfo(@PathVariable("memberId") Long memberId) {
    MemberDto memberDto = memberService.getMemberInfo(memberId);
    return ResponseEntity.ok(memberDto);
  }

  @Operation(summary = "회원 정보 수정")
  @PutMapping("/{memberId}")
  public ResponseEntity<MemberDto> updateMember(@PathVariable Long memberId,
      @RequestBody MemberUpdateDto updateDto) {
    MemberDto result = memberService.updateMember(memberId, updateDto);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "회원 삭제")
  @DeleteMapping("/{memberId}")
  public ResponseEntity<Void> deleteMember(@PathVariable Long memberId) {
    memberService.deleteMember(memberId);
    return ResponseEntity.noContent().build();
  }
}
