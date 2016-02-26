package ru.simplex_software.security.ulogin;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * AuthenticationToken for UloginAuthentifiactionProvider
 */
public class ULoginAuthToken extends AbstractAuthenticationToken {
    private String token;
    public ULoginUser ULoginUser;

    public ULoginAuthToken(String token) {
        super(null);
        this.token=token;
    }

    public ULoginUser getULoginUser() {
        return ULoginUser;
    }

    public void setULoginUser(ULoginUser ULoginUser) {
        this.ULoginUser = ULoginUser;
    }

    public String getToken() {
        return token;
    }

    public Object getCredentials() {
        return token;
    }


    public Object getPrincipal() {
        return ULoginUser;
    }
}
