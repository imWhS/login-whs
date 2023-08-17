package hello.login.domain.login;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    /**
     * @return null일 경우 로그인 실패로 간주
     */
    public Member login(String loginId, String password) {
        //loginId와 일치하는 회원 조회
        return memberRepository
                .findByLoginId(loginId).filter(
                        m -> m.getPassword().equals(password))
                .orElse(null);
    }

}
