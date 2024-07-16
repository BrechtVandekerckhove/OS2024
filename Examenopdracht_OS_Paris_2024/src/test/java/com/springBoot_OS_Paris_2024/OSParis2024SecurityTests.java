package com.springBoot_OS_Paris_2024;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import org.springframework.security.test.context.support.WithAnonymousUser;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@Import(SecurityConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
class OSParis2024SecurityTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void loginGet() throws Exception {
		mockMvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));
	}

	@Test
	public void accessDeniedPageGet() throws Exception {
		mockMvc.perform(get("/403")).andExpect(status().isOk()).andExpect(view().name("403"));
	}

	@WithMockUser(username = "USER", roles = { "NO_ROLE" })
	@Test
	public void testNoAccessWithWrongUserRole() throws Exception {
		mockMvc.perform(get("/sports")).andExpect(status().isForbidden());
	}

	@WithAnonymousUser
	@Test
	public void testNoAccessAnonymous() throws Exception {
		mockMvc.perform(get("/sports")).andExpect(redirectedUrlPattern("**/login"));
	}

	@Test
	void testWrongPassword() throws Exception {
		mockMvc.perform(formLogin("/login").user("username", "user").password("password", "wrongpassword"))
				.andExpect(status().isFound()).andExpect(redirectedUrl("/login?error"));
	}

	@Test
	void testCorrectPassword() throws Exception {
		mockMvc.perform(formLogin("/login").user("username", "user").password("password", "user"))
				.andExpect(status().isFound()).andExpect(redirectedUrl("/sports"));
	}

	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testAccessSportsPageWithUserRole() throws Exception {
		mockMvc.perform(get("/sports")).andExpect(status().isOk()).andExpect(view().name("sports"))
				.andExpect(model().attributeExists("username")).andExpect(model().attribute("username", "user"));
	}

	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testAccessSportsPageWithAdminRole() throws Exception {
		mockMvc.perform(get("/sports")).andExpect(status().isOk()).andExpect(view().name("sports"))
				.andExpect(model().attributeExists("username")).andExpect(model().attribute("username", "admin"));
	}

	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testAccessGamesSportPageWithUserRole() throws Exception {
		mockMvc.perform(get("/sports/judo")).andExpect(status().isOk()).andExpect(view().name("gamesSport"));
	}
	
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testAccessGamesSportPageWithAdminRole() throws Exception {
		mockMvc.perform(get("/sports/golf")).andExpect(status().isOk()).andExpect(view().name("gamesSport"));
	}

	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testNoAccessNewGamePageWithUserRole() throws Exception {
		mockMvc.perform(get("/sports/judo/newGame")).andExpect(status().isForbidden());
	}

	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testAccessNewGamePageWithAdminRole() throws Exception {
		mockMvc.perform(get("/sports/judo/newGame")).andExpect(status().isOk()).andExpect(view().name("newGame"));
	}
}
