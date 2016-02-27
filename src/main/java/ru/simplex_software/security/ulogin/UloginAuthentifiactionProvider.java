package ru.simplex_software.security.ulogin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * AuthenticationProvider проверяет корректность ulogin-токена и заполняет principal ( ULoginUser )
 */
public class UloginAuthentifiactionProvider implements AuthenticationProvider {
    private static final Logger LOG= LoggerFactory.getLogger(UloginAuthentifiactionProvider.class);

    /**
     * Домен сайта, который производит аутентификацию.
     */
    private String host;

    public UloginAuthentifiactionProvider(String host) {
        this.host=host;
    }

 
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }
        ULoginAuthToken uLoginAuthenticationToken=(ULoginAuthToken)authentication;
        try {
            URL uloginUrl = new URL("http://ulogin.ru/token.php?token=" + uLoginAuthenticationToken.getCredentials() + "&host="+host);
            URLConnection urlConnection = uloginUrl.openConnection();

            JsonReader jsonReader = Json.createReader(urlConnection.getInputStream());
            JsonObject obj = jsonReader.readObject();

            if (obj == null) {
                throw new BadCredentialsException("ulogin did't return json object");
            }

            String identity = obj.getJsonString("identity").getString();
            LOG.info(identity);
            if (identity == null) {
                throw new BadCredentialsException("null returned object");
            }

            ULoginUser ULoginUser = new ULoginUser();
            ULoginUser.setIdentity(identity);
            ULoginUser.setProfile(obj.getJsonString("profile").getString());
            uLoginAuthenticationToken.setULoginUser(ULoginUser);

        }catch (Exception ex){
            throw new AuthenticationServiceException(ex.getMessage());
        }
        return uLoginAuthenticationToken;
    }


    public boolean supports(Class<?> authentication) {
        return ULoginAuthToken.class.isAssignableFrom(authentication);
    }
}
