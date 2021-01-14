package com.helenbake.helenbake.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ProfileDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(encoder());
    }

    @Bean
    public JwtAuthenticationFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationFilter();
    }

    private static final String[] SWAGGER_WHITELIST = {
            // -- swagger ui
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**"
    };

    private static final String[] FRONT_END_WHITELIST = {
            "/assets/**/*",
            "/tmp/xmlcallschema/*",
            "/static/**/*",
            "/error.html",
            "/success.html",
            "/failure.html",
            "/main*.*.js",
            "/scripts.*.js",
            "/configuration/security",
            "/polyfills*.*.js",
            "/runtime*.*.js",
            "/styles.*.css"
    };
    private static final String[] UNAUTHENTICATED_ROUTES = new String[] {
            "/api/v1/register/confirmPayment?{.+}&{.+}",
    };
    private static final String[] URL_WHITELIST = {
            // -- swagger ui
            "/login",
            "/dashboard",
            "/admin/users/list",
            "/admin/payments/list",
            "/organization/*/view",
            "/company/*",
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().
                authorizeRequests()
                .antMatchers("/api/v1/token").permitAll()
                .antMatchers("/api/v1/register").permitAll()
                .regexMatchers( "/api/v1/confirm/confirmPayment?([^\\/]+)&([^\\/]+)").permitAll()
                .antMatchers("/api/v1/register/otp").permitAll()
                .antMatchers("/api/v1/users/password/forgot").permitAll()
                .antMatchers("/api/v1/users/password/forgot/*").permitAll()
                .antMatchers("/api/v1/users/password/new").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers(FRONT_END_WHITELIST).permitAll()
                .antMatchers(URL_WHITELIST).permitAll()
                .antMatchers(SWAGGER_WHITELIST).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
}
