package com.springBoot_OS_Paris_2024;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testGetRequest() throws Exception {
		mockMvc.perform(get("/login")).andExpect(view().name("login")).andExpect(status().isOk());

	}
}