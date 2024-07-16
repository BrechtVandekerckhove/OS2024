package com.springBoot_OS_Paris_2024;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class LoginController {
	@Autowired
	private MessageSource messageSource;

	@GetMapping
	public String login(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout, Model model, Locale locale) {

		if (error != null) {
			model.addAttribute("error", messageSource.getMessage("error.login", new Object[] {}, locale));
		}
		if (logout != null) {
			model.addAttribute("msg", messageSource.getMessage("logout", new Object[] {}, locale));
		}
		return "login";
	}

}