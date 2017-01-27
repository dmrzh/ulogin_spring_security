package ru.simplex_software.security.ulogin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
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

            if (obj == null ) {
                throw new BadCredentialsException("ulogin did't return json object");
            }
            if(obj.getJsonString("identity")==null){
                throw new BadCredentialsException("ulogin did't return identity object");
            }

            String identity = obj.getJsonString("identity").getString();
            LOG.info(identity);

            ULoginUser ULoginUser = new ULoginUser();
            ULoginUser.setIdentity(identity);
            ULoginUser.setProfile(getStringProp(obj,"profile"));
            ULoginUser.setFirstName(getStringProp(obj,"first_name"));
            ULoginUser.setLastName(getStringProp(obj,"last_name"));
            ULoginUser.setNickname(getStringProp(obj,"nickname"));
            ULoginUser.setBithDate(getStringProp(obj,"bdate"));
            ULoginUser.setPhone(getStringProp(obj,"phone"));
            ULoginUser.setCity(getStringProp(obj,"city"));
            ULoginUser.setCountry(getStringProp(obj,"country"));
            ULoginUser.setEmail(getStringProp(obj,"email"));
            ULoginUser.setNetwork(getStringProp(obj,"network"));
            ULoginUser.setPhoto(getStringProp(obj,"photo"));
            ULoginUser.setPhotoBig(getStringProp(obj,"photo_big"));
            ULoginUser.setUid(getStringProp(obj,"uid"));

            ULoginUser.setVerified_email("1".equals(getStringProp(obj,"verified_email")));
            ULoginUser.setSex(getSex(obj));

            uLoginAuthenticationToken.setULoginUser(ULoginUser);
            uLoginAuthenticationToken.setAuthenticated(true);

        }catch (Exception ex){
            uLoginAuthenticationToken.setAuthenticated(false);
            LOG.error(ex.getMessage(),ex);
            throw new AuthenticationServiceException(ex.getMessage());
        }
        return uLoginAuthenticationToken;
    }

    public ULoginUser.Sex getSex(JsonObject obj){
        JsonNumber sexNum = obj.getJsonNumber("sex");
        if(sexNum==null){
            return null;
        }
        int i = sexNum.intValue();
        if(i<0||i>3){
            return null;
        }
        return ULoginUser.Sex.values()[i];
    }
    private String getStringProp(JsonObject obj, String prop) {
        JsonString jsonString = obj.getJsonString(prop);
        if(jsonString==null){
            return null;
        }
        return jsonString.getString();
    }


    public boolean supports(Class<?> authentication) {
        return ULoginAuthToken.class.isAssignableFrom(authentication);
    }
}
