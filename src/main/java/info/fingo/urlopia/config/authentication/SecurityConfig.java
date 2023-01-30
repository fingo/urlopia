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

    private static final String[] PUBLIC_ROUTES = {
            "/", "/static/**", "/health/**", "/api/v2/info/**", "/api/v2/slack/**",
            "/api/v2/proxy/**", "/login_check/**", "/icons/**", "/error", "/index.html"
    };
    private static final String[] ALLOWED_EXTENSIONS = {
            "/*.png", "/*.xml", "/*.ico", "/*.webmanifest", "/*.svg",
    };

    private final Environment environment;
    private final AuthorizationFilterProvider authorizationFilterProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        var activeProfiles = Arrays.asList(environment.getActiveProfiles());

        corsAndCsrfConfiguration(http);

        sessionConfiguration(http);

        filtersConfiguration(http);

        exceptionHandlingConfiguration(http);

        if (activeProfiles.contains("developer")) {
            enableH2ConsoleAccess(http);
        }

        requestAuthorizationConfiguration(http);
    }

    private void corsAndCsrfConfiguration(HttpSecurity http) throws Exception {
        http.cors();
        http.csrf().disable();
    }

    private void sessionConfiguration(HttpSecurity http) throws Exception {
        http
           .sessionManagement()
           .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    private void filtersConfiguration(HttpSecurity http){
        var authFilter = authorizationFilterProvider.getAuthorizationFilter();
        http
           .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
    }

    private void exceptionHandlingConfiguration(HttpSecurity http) throws Exception {
        http
           .exceptionHandling()
           .accessDeniedHandler(new AccessDeniedExceptionHandler());
    }

    private void requestAuthorizationConfiguration(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(PUBLIC_ROUTES).permitAll()
                .antMatchers(ALLOWED_EXTENSIONS).permitAll()
                .anyRequest().authenticated();
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
