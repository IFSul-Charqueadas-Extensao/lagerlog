package com.maltepuro.lagerlog.config;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.maltepuro.lagerlog.service.UsuarioDetailsService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Value("${jwt.public.key}")
    private RSAPublicKey key;
    @Value("${jwt.private.key}")
    private RSAPrivateKey priv;
    
    private static final String [] ENDPOINTS_RECURSOS = {
        "/css/**",
        "/js/**",
        "/img/**",
        "favicon.ico"
    };

    private static final String [] ENDPOINTS_BASE = {
        "/",
        "/auth/**",
        "/error"
    };

    private static final String [] ENDPOINTS_OPERADOR = {
        "/produto/listarProdutos"
    };

    private static final String [] ENDPOINTS_SUPERVISOR = {
        "/cadastros",
        "/produto/**",
        "/usuario/**",
        "/estoque/**"
    };

    private static final String [] ENDPOINTS_ADMINISTRADOR = {

    };
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
            .csrf(csrf -> csrf.disable())
            // .cors(cors -> cors.disable())
            .formLogin(formLogin -> formLogin
                .loginProcessingUrl("/auth/login")
                .loginPage("/auth/login")
                .failureUrl("/auth/login/failure")
                .defaultSuccessUrl("/auth/login/success", true)
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .authorizeHttpRequests(auth -> auth
                .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                .requestMatchers(ENDPOINTS_RECURSOS).permitAll()
                .requestMatchers("/").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers(ENDPOINTS_OPERADOR).hasRole("OPERADOR")
                .requestMatchers(ENDPOINTS_SUPERVISOR).hasRole("SUPERVISOR")
                .requestMatchers(ENDPOINTS_ADMINISTRADOR).hasRole("ADMINISTRADOR")
                .anyRequest().authenticated()
                // .anyRequest().permitAll()
                
            )
            .httpBasic(Customizer.withDefaults())
            .oauth2ResourceServer(config -> config.jwt(Customizer.withDefaults()));
        
        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder(){
        return NimbusJwtDecoder.withPublicKey(key).build();
    }

    @Bean
    JwtEncoder jwtEncoder(){
        var jwk = new RSAKey.Builder(key).privateKey(priv).build();
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(UsuarioDetailsService usuarioDetailsService, PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(usuarioDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        
        return new ProviderManager(authenticationProvider);
    }

}
