package sejong.capston.yechef.domain.Member;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(
    name = "회원 관리 API",
    description = "회원 관리 API 제공"
)
public class MemberController {
  private final MemberService memberService;

  @GetMapping("/{memberId}")
  public ResponseEntity<MemberDTO> getMemberInfo(
      @PathVariable("memberId") Long memberId) {
    MemberDTO memberDto = memberService.getMemberInfo(memberId);
    return ResponseEntity.ok(memberDto);
  }

}
