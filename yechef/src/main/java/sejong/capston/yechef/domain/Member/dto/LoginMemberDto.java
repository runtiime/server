package sejong.capston.yechef.domain.Member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import sejong.capston.yechef.domain.Member.Member;

import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
public class LoginMemberDto {

    private Long id;
    private String username;
    private Member.Role role;

    private LoginMemberDto(Long id, String username, Member.Role role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
    public static LoginMemberDto fromEntity(Member member) {
        return new LoginMemberDto(
                member.getId(),
                member.getNickname(),
                member.getRole()
        );
    }

    public static LoginMemberDto fromJwt(Long id, String username, Member.Role role) {
        return new LoginMemberDto(id, username, role);
    }

    public List<GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.role.name()));
    }
}
