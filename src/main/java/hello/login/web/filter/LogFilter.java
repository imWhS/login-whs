package hello.login.web.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("LogFilter.init()");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("LogFilter.doFilter()");

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        //요청 구분을 위한 트랜잭션 UUID 로깅
        String uuid = UUID.randomUUID().toString();

        //모든 클라이언트의 요청 URI 로깅
        String requestURI = httpRequest.getRequestURI();

        try {
            log.info("REQUEST [{}] [{}]", uuid, requestURI);
            chain.doFilter(request, response); //다음 필터 호출. 체인에 더 이상 호출할 필터가 존재하지 않으면 요청 처리를 위한 서블릿 호출
        } catch (Exception e) {
            throw e;
        } finally {
            //요청 처리를 위해 호출된 서블릿의 실행이 끝난 후의 응답 처리
            log.info("RESPONSE [{}] [{}]", uuid, requestURI);
        }
    }

    @Override
    public void destroy() {
        log.info("LogFilter.destroy()");
    }
}
