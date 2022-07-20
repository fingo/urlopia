package info.fingo.urlopia.config.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;


@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true)
@RequiredArgsConstructor
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtFilter jwtFilter;

    private final Environment environment;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        var activeProfiles = Arrays.asList(environment.getActiveProfiles());

        mainHttpConfiguration(http);

        if (activeProfiles.contains("developer")) {
            enableH2ConsoleAccess(http);
        }
    }

    private void mainHttpConfiguration(HttpSecurity http) throws Exception {
        http.cors();
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.authorizeRequests()
                .antMatchers("/api/session/**").permitAll()
                .antMatchers("/api/v2/session/**").permitAll()
                .antMatchers("/api/v2/info/**").permitAll()
                .antMatchers("/api/v2/slack/**").permitAll()
                .antMatchers("/api/v2/proxy/**").permitAll()
                .anyRequest().authenticated();
        http.exceptionHandling().accessDeniedHandler(new AccessDeniedExceptionHandler());
    }

    private void enableH2ConsoleAccess(HttpSecurity http) throws Exception {
        http
                .headers()
                .frameOptions().disable()
                .and()
                .authorizeRequests()
                .antMatchers("/h2-console/**").permitAll();
    }

}
