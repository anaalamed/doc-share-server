//package docSharing.filter;
//
//import docSharing.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.GenericFilterBean;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//
//@Component
//public class AuthenticationFilter extends GenericFilterBean {
//    @Autowired
//    private UserService userService;
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
//            throws IOException, ServletException {
//
//        String token = ((HttpServletRequest) servletRequest).getHeader("auth");
//        if (token == null) {
//            throw new RuntimeException("TODO: change that to bad response!!!!");
//        } else if (token != userService.) {
//
//        }
//
//        filterChain.doFilter(servletRequest, servletResponse);
//    }
//}
