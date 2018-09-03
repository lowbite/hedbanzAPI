package com.hedbanz.hedbanzAPI.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Configuration
    @Order(3)
    public static class MainSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                        .anyRequest()
                        .permitAll()
                        .and()
                    .formLogin()
                        .disable()
                    .csrf()
                        .disable();
        }
    }

    @Configuration
    @Order(2)
    public static class GameSecurityConfig extends WebSecurityConfigurerAdapter {
        private final String[] PROTECTED_URLS = new String[]{
                "/user/**",
                "/fcm/**",
                "/rooms/**",
                "/game/**",
                "/socket-messaging/**   "
        };
        @Autowired
        private JwtTokenProvider tokenProvider;
        @Autowired
        private CustomUserDetailsService customUserDetailsService;

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth
                    .userDetailsService(customUserDetailsService)
                    .passwordEncoder(passwordEncoder());
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatchers().antMatchers(PROTECTED_URLS)
                    .and()
                    .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, customUserDetailsService),
                            UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(new ExceptionHandlerFilter(),JwtAuthenticationFilter.class)
                    .cors()
                    .and()
                    .csrf()
                    .disable()
                    .exceptionHandling()
                    .authenticationEntryPoint(forbiddenEntryPoint())
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.PUT, "/user").permitAll()
                    .antMatchers(HttpMethod.POST, "/user").permitAll()
                    .anyRequest().access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
                    .and()
                    .formLogin().disable();

        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/static/**");
        }

        @Bean
        AuthenticationEntryPoint forbiddenEntryPoint() {
            return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
        }

        @Bean(BeanIds.AUTHENTICATION_MANAGER)
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }

    @Configuration
    @Order(1)
    public static class AdminSecurityConfig extends WebSecurityConfigurerAdapter {
        @Autowired
        CustomUserDetailsService userDetailsService;
        @Autowired
        PasswordEncoder passwordEncoder;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService)
                    .passwordEncoder(passwordEncoder);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/admin/**")
                    .authorizeRequests()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .and()
                    .formLogin()
                    .loginPage("/admin/login")
                    .defaultSuccessUrl("/admin/panel")
                    .permitAll()
                    .and()
                    .logout()
                    .permitAll();
        }
    }
}
