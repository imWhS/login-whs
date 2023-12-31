package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;

    private final SessionManager sessionManager;

//    @GetMapping("/")
    public String home() {
        return "home";
    }

//    @GetMapping("/")
    public String homeLogin(
            @CookieValue(name = "memberId", required = false) Long memberId, //로그인하지 않은 사용자에 의해서도 호출될 수 있기에 required를 false로 지정
            Model model
    ) {
        //로그인하지 않은 사용자가 요청한 경우
        if (memberId == null) {
            return "home";
        }

        //로그인한 사용자가 요청한 경우
        Member loginMember = memberRepository.findById(memberId);

        //오래 전에 만들어진 쿠키 등의 이유로 로그인한 사용자 정보가 데이터베이스에 존재하지 않는 경우
        if (loginMember == null) {
            return "home";
        }

        //정상적인 사용자인 경우 로그인한 사용자 전용 홈 뷰 템플릿으로 이동
        model.addAttribute("member", loginMember);

        return "loginHome";
    }

//    @GetMapping("/")
    public String homeLoginV2(
            HttpServletRequest request,
            Model model
    ) {
        //세션 관리자에서 회원 조회
        Member member = (Member) sessionManager.getSession(request);

        //세션이 존재하지 않는 비로그인 회원인 경우 로그인하지 않은 사용자 전용 홈 뷰 템플릿으로 이동
        if (member == null) {
            return "home";
        }

        //로그인한 사용자가 요청한 경우
        model.addAttribute("member", member);

        //정상적인 사용자인 경우 로그인한 사용자 전용 홈 뷰 템플릿으로 이동
        return "loginHome";
    }

//    @GetMapping("/")
    public String homeLoginV3(
            HttpServletRequest request,
            Model model
    ) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return "home";
        }

        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        //세션이 존재하지 않는 비로그인 회원인 경우 로그인하지 않은 사용자 전용 홈 뷰 템플릿으로 이동
        if (member == null) {
            return "home";
        }

        //로그인한 사용자가 요청한 경우
        model.addAttribute("member", member);

        //정상적인 사용자인 경우 로그인한 사용자 전용 홈 뷰 템플릿으로 이동
        return "loginHome";
    }

    @GetMapping("/")
    public String homeLoginV3Spring(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
            Model model
    ) {

        //세션이 존재하지 않는 비로그인 회원인 경우 로그인하지 않은 사용자 전용 홈 뷰 템플릿으로 이동
        if (member == null) {
            return "home";
        }

        //로그인한 사용자가 요청한 경우
        model.addAttribute("member", member);

        //정상적인 사용자인 경우 로그인한 사용자 전용 홈 뷰 템플릿으로 이동
        return "loginHome";
    }

}