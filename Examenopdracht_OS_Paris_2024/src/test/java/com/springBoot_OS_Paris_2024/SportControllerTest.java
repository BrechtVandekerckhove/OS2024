package com.springBoot_OS_Paris_2024;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class SportControllerTest {
	@Autowired
	private MockMvc mockMvc;


	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testGetRequestSports() throws Exception {
		mockMvc.perform(get("/sports")).andExpect(view().name("sports")).andExpect(status().isOk())
				.andExpect(model().attributeExists("username")).andExpect(model().attribute("username", "user"))
				.andExpect(model().attributeExists("role")).andExpect(model().attributeExists("sportList"))
				.andExpect(model().attributeExists("ticketMap"));
	}

	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testGetRequestSportsAsAdminTicketButtonDoesNotExist() throws Exception {

		mockMvc.perform(get("/sports")).andExpect(status().isOk())
				.andExpect(content().string(not(containsString("id=\"button-ticket\""))));
	}

	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testGetRequestSportsAsUserTicketButtonExist() throws Exception {

		mockMvc.perform(get("/sports")).andExpect(status().isOk())
				.andExpect(content().string(containsString("id=\"button-ticket\"")));
	}

	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testGetRequestGamesSport() throws Exception {
		mockMvc.perform(get("/sports/judo")).andExpect(view().name("gamesSport")).andExpect(status().isOk())
				.andExpect(model().attributeExists("username")).andExpect(model().attributeExists("role"))
				.andExpect(model().attributeExists("gamesList"))
				.andExpect(model().attributeExists("numberOfTicketsPerGameMap"));
	}

	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testGetRequestGamesSportAsAdminAddGameButtonExist() throws Exception {
		mockMvc.perform(get("/sports/judo")).andExpect(view().name("gamesSport")).andExpect(status().isOk())
				.andExpect(content().string(containsString("id=\"button-addGame\"")));
	}

	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testGetRequestGamesSportAsUserAddGameButtonDoesNotExist() throws Exception {
		mockMvc.perform(get("/sports/judo")).andExpect(view().name("gamesSport")).andExpect(status().isOk())
				.andExpect(content().string(not(containsString("id=\"button-addGame\""))));
	}

	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testGetRequestGamesSportAsUserBuyTicketsButtonExist() throws Exception {
		mockMvc.perform(get("/sports/judo")).andExpect(view().name("gamesSport")).andExpect(status().isOk())
				.andExpect(content().string(containsString("id=\"button-buyTickets\"")));
	}

	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testGetRequestGamesSportAsAdminBuyTicketsButtonDoesNotExist() throws Exception {
		mockMvc.perform(get("/sports/judo")).andExpect(view().name("gamesSport")).andExpect(status().isOk())
				.andExpect(content().string(not(containsString("id=\"button-buyTickets\""))));
	}
}
