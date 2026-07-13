package com.knowly.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import com.knowly.service.MyUserDetailsService;

@Configuration
public class SecurityConfig {
	
	private final PasswordEncoder passEncoder;
	private final MyUserDetailsService userDetailsService;
	private final CustomOAuth2UserService customOAuth2UserService;

	@Value("${app.remember-me-key}")
	private String rememberMeKey;

	public SecurityConfig(PasswordEncoder passEncoder, MyUserDetailsService userDetailsService,
	                       CustomOAuth2UserService customOAuth2UserService) {
		this.passEncoder = passEncoder;
		this.userDetailsService = userDetailsService;
		this.customOAuth2UserService = customOAuth2UserService;
	}
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

	    http
	        .authenticationProvider(authprovider())   // <-- Add this

	        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .ignoringRequestMatchers("/signup", "/send-otp", "/verify-otp")
        )

	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(
	                "/",
	                "/signup",
	                "/send-otp",
	                "/verify-otp",
	                "/login",
	                "/oauth2/**",
	                "/login/oauth2/**",
	                "/css/**",
	                "/js/**",
	                "/images/**",
	                "/actuator/health"
	            ).permitAll()
	            .anyRequest().authenticated()
	        )

	        .formLogin(form -> form
    			    .loginPage("/login")
    			    .loginProcessingUrl("/login")
	        	    .usernameParameter("emailOrPhone")
	        	    .passwordParameter("password")
	        	    .defaultSuccessUrl("/home", true)
	        	    .failureUrl("/login?error")
	        	    .permitAll()
	        	)
	        .oauth2Login(oauth2 -> oauth2
	                .loginPage("/login")
	                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
	                .defaultSuccessUrl("/home", true)
	                .failureUrl("/login?error")
	        )
			.rememberMe(r -> r
					.rememberMeParameter("remember")
					.tokenValiditySeconds(60 * 60 * 24 * 14) // 14 days
					.userDetailsService(userDetailsService)
					.key(rememberMeKey)
			)
	        .logout(logout -> logout
	            .logoutUrl("/logout")
	            .logoutSuccessUrl("/login?logout")
	            .permitAll()
	        );

	    return http.build();
	}
    @Bean
    public AuthenticationProvider authprovider() {
    		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
    		provider.setPasswordEncoder(passEncoder);
    		return provider;
    }
}