package com.springBoot_OS_Paris_2024;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import domain.TicketRequest;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class TicketControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testGetRequest() throws Exception {
		mockMvc.perform(get("/sports/judo/tickets-game=1")).andExpect(view().name("ticketsGame"))
				.andExpect(status().isOk()).andExpect(model().attributeExists("username"))
				.andExpect(model().attributeExists("role")).andExpect(model().attributeExists("numberOfTickets"))
				.andExpect(model().attributeExists("game")).andExpect(model().attributeExists("ticketRequest"))
				.andExpect(model().attribute("ticketRequest", instanceOf(TicketRequest.class)));
	}

}
