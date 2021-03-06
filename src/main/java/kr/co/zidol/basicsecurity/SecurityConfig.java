package kr.co.zidol.basicsecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
    import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Configuration
@EnableWebSecurity  //웹 보안 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {  //스프링 시큐리티의 웹 보안 기능 초기화 및 설정하는 class

    @Autowired
    UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //인가 정책
        http
                .authorizeRequests()
                .anyRequest().authenticated();  //어떤 요청에 대한 인증을 받아야 함(인가 정책)

        http
                .formLogin()    //인증 정책
//                .loginPage("/loginPage")
                .defaultSuccessUrl("/")// 로그인 성공시 이동 페이지
                .failureUrl("/login")//로그인 실패시 이동 페이지
                .usernameParameter("userId")    //default : username html input tag name과 같아야함
                .passwordParameter("passwd")   //default : password html input tag name과 같아야함
                .loginProcessingUrl("/login_proc")         //default : login   form tag action url
//                .successHandler(new AuthenticationSuccessHandler() {
//                    @Override
//                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                        System.out.println("authentication : " + authentication.getName());
//                        response.sendRedirect("/");
//                    }
//                })
//                .failureHandler(new AuthenticationFailureHandler() {
//                    @Override
//                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
//                        System.out.println("exception : " + e.getMessage());
//                        response.sendRedirect("/login");
//                    }
//                })
                .permitAll()
        ;

        http
                .logout()
                .logoutUrl("/logout")       //디폴트 post 방식
                .logoutSuccessUrl("/login")
                .addLogoutHandler(new LogoutHandler() {
                    @Override
                    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                        HttpSession session = request.getSession();
                        session.invalidate();
                    }
                })
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        response.sendRedirect("/login");
                    }
                })
//                .deleteCookies("remember-me")
                .and()
                .rememberMe()
                .rememberMeParameter("remember")    //default remember-me
                .tokenValiditySeconds(3600)
                .userDetailsService(userDetailsService)
                ;

    }
}
