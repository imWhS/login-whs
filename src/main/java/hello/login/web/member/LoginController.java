package hello.login.web.member;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm) {
        return "login/loginForm";
    }

//    @PostMapping("/login")
    public String login(
            @Validated @ModelAttribute LoginForm form,
            BindingResult bindingResult,
            HttpServletResponse response
    ) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        //로그인 폼 필드로 입력받은 정보로 회원을 찾지 못한 경우: loginId, password와 같은 필드 오류가 아닌 글로벌 오류로 처리
        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀 번호를 다시 확인해주세요.");
            return "login/loginForm";
        }

        //로그인 성공 처리 - 세션 쿠키
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);

        return "redirect:/";
    }

//    @PostMapping("/login")
    public String loginV2(
            @Validated @ModelAttribute LoginForm form,
            BindingResult bindingResult,
            HttpServletResponse response
    ) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        //로그인 폼 필드로 입력받은 정보로 회원을 찾지 못한 경우: loginId, password와 같은 필드 오류가 아닌 글로벌 오류로 처리
        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀 번호를 다시 확인해주세요.");
            return "login/loginForm";
        }

        //로그인 성공 처리 - 세션 쿠키
        sessionManager.createSession(loginMember, response);
        return "redirect:/";
    }

    @PostMapping("/login")
    public String loginV3(
            @Validated @ModelAttribute LoginForm form,
            BindingResult bindingResult,
            HttpServletRequest request
    ) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        //로그인 폼 필드로 입력받은 정보로 회원을 찾지 못한 경우: loginId, password와 같은 필드 오류가 아닌 글로벌 오류로 처리
        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀 번호를 다시 확인해주세요.");
            return "login/loginForm";
        }

        //로그인 성공 처리 - 세션 쿠키
        HttpSession session = request.getSession(); //세션이 이미 존재하면 기존 세션 사용, 존재하지 않으면 세선 생성
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "redirect:/";
    }

//    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        expireCookie(response, "memberId");
        return "redirect:/";
    }

//    @PostMapping("/logout")
    public String logoutV2(HttpServletRequest request) {
        sessionManager.expire(request);
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request) {
        HttpSession session = request.getSession(false); //세션을 지우는 게 목적이기에, 굳이 세션을 생성할 필요는 없다.

        if (session != null) {
            session.invalidate();
        }

        return "redirect:/";
    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
