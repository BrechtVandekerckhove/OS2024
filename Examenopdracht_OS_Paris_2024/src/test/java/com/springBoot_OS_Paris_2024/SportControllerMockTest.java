package com.springBoot_OS_Paris_2024;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import domain.Discipline;
import domain.Game;
import domain.MyUser;
import domain.Role;
import domain.Sport;
import repository.GameRepository;
import repository.SportRepository;
import repository.TicketRepository;
import repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class SportControllerMockTest {
	@Autowired
	private SportController controller;
	@Autowired
	private MockMvc mockMvc;
	@Mock
	private UserRepository mockUserRepository;
	@Mock
	private SportRepository mockSportRepository;
	@Mock
	private GameRepository mockGameRepository;
	@Mock
	private TicketRepository mockTicketRepository;
	private Sport sport1, sport2;
	private Game game1, game2, game3;
	private Principal mockPrincipal;
	private MyUser user;

	@BeforeEach
	public void before() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(controller, "userRepository", mockUserRepository);
		ReflectionTestUtils.setField(controller, "sportRepository", mockSportRepository);
		ReflectionTestUtils.setField(controller, "gameRepository", mockGameRepository);
		ReflectionTestUtils.setField(controller, "ticketRepository", mockTicketRepository);

		user = new MyUser(1, "user", "123", "user", Role.USER);
		sport1 = new Sport("sport1");
		sport2 = new Sport("sport2");

		game1 = new Game(1, sport1, LocalDateTime.now(), "Stadium1", 12345, 12346, 20, 50.00,
				List.of(new Discipline(sport1, "Description1"), new Discipline(sport1, "Description2")));
		game2 = new Game(2, sport1, LocalDateTime.now(), "Stadium2", 12375, 12348, 25, 55.00,
				List.of(new Discipline(sport1, "Description3")));
		game3 = new Game(3, sport2, LocalDateTime.now(), "Stadium3", 12372, 12349, 20, 56.00,
				List.of(new Discipline(sport2, "Description1")));
		mockPrincipal = Mockito.mock(Principal.class);

		Mockito.when(mockPrincipal.getName()).thenReturn("user");
		Mockito.when(mockUserRepository.findByUsername(mockPrincipal.getName())).thenReturn(user);
	}

	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testGetRequestSports() throws Exception {
		List<Sport> expectedSports = List.of(sport1, sport2);
		Map<Game, Integer> expectedTicketsCount = Map.of(game1, 2, game2, 5, game3, 8);

		Mockito.when(mockSportRepository.findAll()).thenReturn(expectedSports);
		Mockito.when(mockTicketRepository.countTicketsByGameForUserOrderedBySportsNameAndStartDateAsMap(1))
				.thenReturn(expectedTicketsCount);

		mockMvc.perform(get("/sports").principal(mockPrincipal)).andExpect(view().name("sports"))
				.andExpect(model().attributeExists("sportList"))
				.andExpect(model().attribute("sportList", expectedSports))
				.andExpect(model().attributeExists("ticketMap"))
				.andExpect(model().attribute("ticketMap", expectedTicketsCount));
	}

	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testGetRequestGamesSport() throws Exception {
		Sport expectedSport = sport1;
		List<Game> expectedGames = new ArrayList<>(expectedSport.getGames());
		Map<Game, Integer> expectedTicketsCount = Map.of(game1, 2, game2, 5);
		Mockito.when(mockSportRepository.findBySportsName(expectedSport.getSportsName())).thenReturn(expectedSport);
		Mockito.when(mockGameRepository.findAllBySport(expectedSport)).thenReturn(expectedGames);
		Mockito.when(mockTicketRepository.countTicketsPerGameOfSportIDForUserIDAsMap(expectedSport.getSportID(),
				user.getId())).thenReturn(expectedTicketsCount);

		mockMvc.perform(get("/sports/"+sport1.getSportsName()).principal(mockPrincipal)).andExpect(view().name("gamesSport"))
				.andExpect(model().attributeExists("sport")).andExpect(model().attribute("sport", expectedSport))
				.andExpect(model().attributeExists("gamesList"))
				.andExpect(model().attribute("gamesList", expectedGames))
				.andExpect(model().attributeExists("numberOfTicketsPerGameMap"))
				.andExpect(model().attribute("numberOfTicketsPerGameMap", expectedTicketsCount));
	}

	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testGetRequestGamesSportWithNonExistingSportsName_redirect() throws Exception {
		String nonExistingSportsName = "nonExistingSport";
		Mockito.when(mockSportRepository.findBySportsName(nonExistingSportsName)).thenReturn(null);
		mockMvc.perform(get("/sports/" + nonExistingSportsName).principal(mockPrincipal))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/sports"));
	}
}
