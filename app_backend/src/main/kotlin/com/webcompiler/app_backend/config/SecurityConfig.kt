package com.webcompiler.app_backend.config

import com.webcompiler.app_backend.service.CustomUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    @Autowired private val customUserDetailsService: CustomUserDetailsService,
    @Autowired private val passwordEncoder: PasswordEncoder,
    @Autowired private val authenticationSuccessHandler: CustomAuthenticationSuccessHandler,
    @Autowired private val customLogoutSuccessHandler: CustomLogoutSuccessHandler,
) {

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf ->
                csrf.disable()
            }
            .cors { cors ->
                cors.configurationSource(corsConfigurationSource())
            }
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/api/register", "/api/session-context/current-user").permitAll()
                    .requestMatchers("/api/user/**").hasRole("USER")
                    .requestMatchers("/api/moderator/**").hasRole("MODERATOR")
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
            .formLogin { form ->
                form
                    .successHandler(authenticationSuccessHandler)
            }
            .logout { logout ->
                logout.logoutSuccessHandler(customLogoutSuccessHandler)
            }

        return http.build()
    }

    @Bean
    @Throws(java.lang.Exception::class)
    fun authManager(http: HttpSecurity): AuthenticationManager {

        return http
            .getSharedObject(AuthenticationManagerBuilder::class.java)
            .userDetailsService(customUserDetailsService)
            .passwordEncoder(passwordEncoder)
            .and()
            .build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost", "http://localhost:3000")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
