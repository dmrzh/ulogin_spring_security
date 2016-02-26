package ru.simplex_software.security.ulogin;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Получает ulogin token из запроса.
 */
public class UloginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * создаёт UloginAuthenticationFilter
     * @param url который слушает этот фильтр.
     */

    public UloginAuthenticationFilter(String url) {
        super(new AntPathRequestMatcher(url, "POST"));

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }
        String token = request.getParameterValues("token")[0];

        ULoginAuthToken authRequest = new ULoginAuthToken(token);

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
