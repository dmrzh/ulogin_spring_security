package ru.simplex_software.security.ulogin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

/**
 * Example Spring security configuration.
 */
@EnableWebSecurity
public class ExampleSecurityConfig extends WebSecurityConfigurerAdapter {
    AuthenticationManager authManager;
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new UloginAuthentifiactionProvider("example.org"))
        ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        UloginAuthenticationFilter uloginFilter = new UloginAuthenticationFilter("/ulogin");
        uloginFilter.setAuthenticationManager(authenticationManager());

        HttpSecurity httpSecurity = http.
                addFilterBefore(uloginFilter, AnonymousAuthenticationFilter.class);
//        httpSecurity.csrf().disable();
        //httpSecurity.headers().frameOptions().sameOrigin();
        httpSecurity.authorizeRequests().antMatchers("/login.html").permitAll()
                .anyRequest().authenticated() ;
        httpSecurity.formLogin().loginPage("/login.html");

    }
}
