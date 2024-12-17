package com.rasimalimgulov.plannerusers.security;
import com.rasimalimgulov.plannerutils.converter.CkJwtConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new CkJwtConverter());


        http.authorizeHttpRequests(x -> x.requestMatchers("/admin/*").hasRole("Admin_Realm")
                        .requestMatchers("/auth/*").hasRole("User_Realm")
                     .anyRequest().authenticated())
                .oauth2ResourceServer(x -> x.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)));
        return http.build();
    }

}
