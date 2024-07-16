package com.springBoot_OS_Paris_2024;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import perform.PerformRest;
import service.GameService;
import service.GameServiceImpl;
import service.MyUserDetailsService;
import service.MyUserService;
import service.MyUserServiceImpl;
import validator.GameValidation;
import validator.TicketValidation;

@SpringBootApplication
@EnableJpaRepositories("repository")
@EntityScan("domain")
public class OSParis2024Application implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(OSParis2024Application.class, args);
		try {
			new PerformRest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/", "/sports");
		registry.addViewController("/403").setViewName("403");
	}
	@Bean
	GameValidation gameValidation() {
		return new GameValidation();
	}
	@Bean
	TicketValidation ticketValidation() {
		return new TicketValidation();
	}
	@Bean
	UserDetailsService myUserDetailsService() {
		return new MyUserDetailsService();
	}
	@Bean
	GameService gameService() {
		return new GameServiceImpl();
	}
	@Bean
	MyUserService myUserService() {
		return new MyUserServiceImpl();
	}

}
