package com.springBoot_OS_Paris_2024;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import domain.Discipline;
import domain.Game;
import domain.MyUser;
import domain.Role;
import domain.Sport;
import domain.TicketRequest;
import repository.GameRepository;
import repository.SportRepository;
import repository.TicketRepository;
import repository.UserRepository;
import validator.TicketValidation;

@SpringBootTest
@AutoConfigureMockMvc
class TicketControllerMockTest {
	@Autowired
	private TicketController controller;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private TicketValidation ticketValidation;

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
		ReflectionTestUtils.setField(ticketValidation,"gameRepository",mockGameRepository);

		user = new MyUser(1, "user", "123", "user", Role.USER);
		sport1 = new Sport("sport1");
		sport2 = new Sport("sport2");

		game1 = new Game(1, sport1, LocalDateTime.now(), "Stadium1", 12345, 12346, 10, 50.00,
				List.of(new Discipline(sport1, "Description1"), new Discipline(sport1, "Description2")));
		game2 = new Game(2, sport1, LocalDateTime.now(), "Stadium2", 12375, 12348, 25, 55.00,
				List.of(new Discipline(sport1, "Description3")));
		game3 = new Game(3, sport2, LocalDateTime.now(), "Stadium3", 12372, 12349, 20, 56.00,
				List.of(new Discipline(sport2, "Description1")));
		user.addNumberOfTicketsForGame(game1, 3);
		user.addNumberOfTicketsForGame(game2, 2);
		user.addNumberOfTicketsForGame(game3, 18);

		mockPrincipal = Mockito.mock(Principal.class);

		Mockito.when(mockPrincipal.getName()).thenReturn("user");
		Mockito.when(mockUserRepository.findByUsername(mockPrincipal.getName())).thenReturn(user);
	}

	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testGetRequest() throws Exception {
		Sport expectedSport = sport1;
		Optional<Game> expectedGameOptional = Optional.ofNullable(game1);
		Integer expectedNumberOfTickets = 10;

		Mockito.when(mockSportRepository.findBySportsName(expectedSport.getSportsName())).thenReturn(expectedSport);
		Mockito.when(mockGameRepository.findById(expectedGameOptional.get().getGameID()))
				.thenReturn(expectedGameOptional);
		Mockito.when(mockTicketRepository.countTicketsOfGameIDForUserID(expectedGameOptional.get().getGameID(),
				user.getId())).thenReturn(expectedNumberOfTickets);

		mockMvc.perform(get(
				"/sports/" + expectedSport.getSportsName() + "/tickets-game=" + expectedGameOptional.get().getGameID())
				.principal(mockPrincipal)).andExpect(view().name("ticketsGame"))
				.andExpect(model().attributeExists("sport")).andExpect(model().attribute("sport", expectedSport))
				.andExpect(model().attributeExists("game"))
				.andExpect(model().attribute("game", expectedGameOptional.get()))
				.andExpect(model().attributeExists("numberOfTickets"))
				.andExpect(model().attribute("numberOfTickets", expectedNumberOfTickets))
				.andExpect(model().attributeExists("ticketRequest"))
				.andExpect(model().attribute("ticketRequest", instanceOf(TicketRequest.class)));
	}

	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testGetRequestWithNonExistingSportsName_redirect() throws Exception {
		String nonExistingSportsName = "nonExistingSport";
		Mockito.when(mockSportRepository.findBySportsName(nonExistingSportsName)).thenReturn(null);

		Optional<Game> expectedGameOptional = Optional.ofNullable(game1);
		Mockito.when(mockGameRepository.findById(expectedGameOptional.get().getGameID()))
				.thenReturn(expectedGameOptional);

		mockMvc.perform(
				get("/sports/" + nonExistingSportsName + "/tickets-game=" + expectedGameOptional.get().getGameID())
						.principal(mockPrincipal))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/sports"));
	}

	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testGetRequestWithNonExistingGameID_redirect() throws Exception {
		Sport expectedSport = sport1;
		int gameID = 999;
		Optional<Game> expectedGameOptional = Optional.empty();

		Mockito.when(mockSportRepository.findBySportsName(expectedSport.getSportsName())).thenReturn(expectedSport);
		Mockito.when(mockGameRepository.findById(gameID)).thenReturn(expectedGameOptional);

		mockMvc.perform(
				get("/sports/" + expectedSport.getSportsName() + "/tickets-game=" + gameID).principal(mockPrincipal))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/sports"));
	}

	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testPostRequest() throws Exception {
		Game expectedGame = game1;
		TicketRequest ticketRequest = new TicketRequest();
		int expectedNumberOfTickets = 5;
		ticketRequest.setNumberOfTickets(expectedNumberOfTickets);

		Mockito.when(mockGameRepository.findById(expectedGame.getGameID())).thenReturn(Optional.of(expectedGame));
		mockMvc.perform(
				post("/sports/" + expectedGame.getSport().getSportsName() + "/tickets-game=" + expectedGame.getGameID())
						.with(csrf()).principal(mockPrincipal).flashAttr("ticketRequest", ticketRequest))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/sports/" + game1.getSport().getSportsName()))
				.andExpect(flash().attribute("numberOfTickets", expectedNumberOfTickets));

	}

	@WithMockUser(username = "user", roles = { "USER" })
	@ParameterizedTest
	@ValueSource(ints = { 21, 50,77, 99, 954455 })
	public void testPostRequestWithTooManyTicketsPerGameAndFreePlaces_error(int tooManyTickets) throws Exception {
		Game expectedGame = game1;
		TicketRequest ticketRequest = new TicketRequest();
		ticketRequest.setNumberOfTickets(tooManyTickets);

		Mockito.when(mockGameRepository.findById(expectedGame.getGameID())).thenReturn(Optional.of(expectedGame));
		Mockito.when(mockSportRepository.findBySportsName(expectedGame.getSport().getSportsName()))
				.thenReturn(expectedGame.getSport());

		mockMvc.perform(
				post("/sports/" + expectedGame.getSport().getSportsName() + "/tickets-game=" + expectedGame.getGameID())
						.with(csrf()).principal(mockPrincipal).flashAttr("ticketRequest", ticketRequest))
				.andExpect(status().isOk()).andExpect(view().name("ticketsGame"))
				.andExpect(model().attributeHasFieldErrors("ticketRequest", "numberOfTickets")).andExpect(result -> {
					BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel()
							.get("org.springframework.validation.BindingResult.ticketRequest");
					List<ObjectError> allErrors = bindingResult.getAllErrors();
					boolean foundMaxTicketsFreePlaces = allErrors.stream()
							.anyMatch(error -> "validator.maxTicketsFreePlaces".equals(error.getCode()));
					boolean foundMaxTicketsGame = allErrors.stream()
							.anyMatch(error -> "validator.maxTicketsGame".equals(error.getCode()));
					if (!foundMaxTicketsFreePlaces || !foundMaxTicketsGame) {
						throw new AssertionError(
								"Expected both validator.maxTicketsFreePlaces and validator.maxTicketsGame errors");
					}
				});

		;
	}

	@WithMockUser(username = "user", roles = { "USER" })
	@ParameterizedTest
	@ValueSource(ints = { 11, 15, 19 })
	public void testPostRequestWithTooManyTicketsFreePlaces_error(int tooManyTickets) throws Exception {
		Game expectedGame = game1;
		TicketRequest ticketRequest = new TicketRequest();
		ticketRequest.setNumberOfTickets(tooManyTickets);

		Mockito.when(mockGameRepository.findById(expectedGame.getGameID())).thenReturn(Optional.of(expectedGame));
		Mockito.when(mockSportRepository.findBySportsName(expectedGame.getSport().getSportsName()))
				.thenReturn(expectedGame.getSport());

		mockMvc.perform(
				post("/sports/" + expectedGame.getSport().getSportsName() + "/tickets-game=" + expectedGame.getGameID())
						.with(csrf()).principal(mockPrincipal).flashAttr("ticketRequest", ticketRequest))
				.andExpect(status().isOk()).andExpect(view().name("ticketsGame"))
				.andExpect(model().attributeHasFieldErrors("ticketRequest", "numberOfTickets"))
				.andExpect(model().attributeHasFieldErrorCode("ticketRequest", "numberOfTickets",
						"validator.maxTicketsFreePlaces"));
	}

	@WithMockUser(username = "user", roles = { "USER" })
	@ParameterizedTest
	@ValueSource(ints = {78, 80, 100, 999998 })
	public void testPostRequestWithTooManyTicketsPerGameAndFreePlacesAndUser_error(int tooManyTickets) throws Exception {
		Game expectedGame = game1;
		TicketRequest ticketRequest = new TicketRequest();
		ticketRequest.setNumberOfTickets(tooManyTickets);

		Mockito.when(mockGameRepository.findById(expectedGame.getGameID())).thenReturn(Optional.of(expectedGame));
		Mockito.when(mockSportRepository.findBySportsName(expectedGame.getSport().getSportsName()))
				.thenReturn(expectedGame.getSport());

		mockMvc.perform(
				post("/sports/" + expectedGame.getSport().getSportsName() + "/tickets-game=" + expectedGame.getGameID())
						.with(csrf()).principal(mockPrincipal).flashAttr("ticketRequest", ticketRequest))
				.andExpect(status().isOk()).andExpect(view().name("ticketsGame"))
				.andExpect(model().attributeHasFieldErrors("ticketRequest", "numberOfTickets")).andExpect(result -> {
					BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel()
							.get("org.springframework.validation.BindingResult.ticketRequest");
					List<ObjectError> allErrors = bindingResult.getAllErrors();
					boolean foundMaxTicketsFreePlaces = allErrors.stream()
							.anyMatch(error -> "validator.maxTicketsFreePlaces".equals(error.getCode()));
					boolean foundMaxTicketsGame = allErrors.stream()
							.anyMatch(error -> "validator.maxTicketsGame".equals(error.getCode()));
					boolean foundMaxTicketsUser = allErrors.stream()
							.anyMatch(error -> "validator.maxTickets".equals(error.getCode()));
					if (!foundMaxTicketsFreePlaces || !foundMaxTicketsGame || !foundMaxTicketsUser) {
						throw new AssertionError(
								"Expected both validator.maxTicketsFreePlaces, validator.maxTicketsGame and validator.maxTickets errors");
					}
				});

		;
	}
}
