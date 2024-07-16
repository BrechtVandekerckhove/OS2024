package com.springBoot_OS_Paris_2024;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.csrfTokenRepository(new HttpSessionCsrfTokenRepository()))

				.authorizeHttpRequests(requests -> requests.requestMatchers("/login**").permitAll()
						.requestMatchers("/css/**").permitAll().requestMatchers("/img/**").permitAll()
						.requestMatchers("/scripts/**").permitAll().requestMatchers("/403**").permitAll()
						.requestMatchers("/rest/**").permitAll().requestMatchers("/sports/*/newGame").hasRole("ADMIN")
						.requestMatchers("/sports/**").hasAnyRole("USER", "ADMIN"))
				.formLogin(form -> form.defaultSuccessUrl("/sports", true).loginPage("/login")).exceptionHandling()
				.accessDeniedPage("/403");

		return http.build();

	}
}