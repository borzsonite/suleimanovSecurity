package edu.sergeyrozhkov.springsecuritydemo.config;

import edu.sergeyrozhkov.springsecuritydemo.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http // пишем http конфигурация
                .csrf().disable()
                .authorizeRequests() // запросы будут авторизовываться т.е. будет решаться кто и куда будет иметь доступ
                .antMatchers("/").permitAll()// по адресу "/"доступ получают все
                .antMatchers(HttpMethod.GET, "/api/**").hasAnyRole(Role.ADMIN.name(), Role.USER.name()) // по любому адресу типа bla-bla/api/bla/bla-bla/etc  с методом GET получают доступ админ и юзер
                .antMatchers(HttpMethod.POST, "/api/**").hasRole(Role.ADMIN.name())// аналогично с методом POST админ
                .antMatchers(HttpMethod.DELETE, "/api/**").hasRole(Role.ADMIN.name() ) //аналогично с методом DELETE админ
                .anyRequest() // каждый запрос
                .authenticated()// должен быть аутентифицирован
                .and() // с
                .httpBasic(); // помощью base64
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.builder()
                        .username("admin")
                        .password(passwordEncoder().encode("admin"))
                        .roles(Role.ADMIN.name())
                        .build(),
                User.builder()
                        .username("user")
                        .password(passwordEncoder().encode("user"))
                        .roles(Role.USER.name())
                        .build());
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}

