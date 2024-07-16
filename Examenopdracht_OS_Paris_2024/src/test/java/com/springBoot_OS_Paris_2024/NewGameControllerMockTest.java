package com.springBoot_OS_Paris_2024;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
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

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import domain.Discipline;
import domain.Game;
import domain.MyUser;
import domain.Role;
import domain.Sport;
import repository.DisciplineRepository;
import repository.GameRepository;
import repository.SportRepository;
import repository.UserRepository;
import service.GameService;
import validator.GameValidation;

@SpringBootTest
@AutoConfigureMockMvc
class NewGameControllerMockTest {
	@Autowired
	private NewGameController controller;
	@Autowired
	private GameService gameService;
	@Autowired
	private GameValidation gameValidation;

	@Autowired
	private MockMvc mockMvc;
	@Mock
	private UserRepository mockUserRepository;
	@Mock
	private SportRepository mockSportRepository;
	@Mock
	private GameRepository mockGameRepository;
	@Mock
	private DisciplineRepository mockDisciplineRepository;

	private Sport sport;
	private Game game, gameToAdjust;
	private Principal mockPrincipal;
	private MyUser admin;
	private Discipline discipline1, discipline2;

	@BeforeEach
	public void before() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(controller, "userRepository", mockUserRepository);
		ReflectionTestUtils.setField(controller, "sportRepository", mockSportRepository);
		ReflectionTestUtils.setField(controller, "gameRepository", mockGameRepository);
		ReflectionTestUtils.setField(controller, "disciplineRepository", mockDisciplineRepository);
		ReflectionTestUtils.setField(gameService, "disciplineRepository", mockDisciplineRepository);
		ReflectionTestUtils.setField(gameValidation, "gameRepository", mockGameRepository);

		admin = new MyUser(2, "admin", "123@", "admin", Role.ADMIN);
		sport = new Sport("sportName");
		game = new Game(sport, LocalDateTime.of(2024, 8, 1, 10, 0, 0), "Stadium1", 12347, 12348, 20, 50.00);
		gameToAdjust = new Game();
		gameToAdjust.setStart(LocalDateTime.of(2024, 8, 1, 10, 0, 0));
		gameToAdjust.setStadium("stadium");
		gameToAdjust.setOlympicNumber1(12345);
		gameToAdjust.setOlympicNumber2(12346);
		gameToAdjust.setPrice(100.0);
		gameToAdjust.setFreePlaces(10);
		mockPrincipal = Mockito.mock(Principal.class);
		discipline1 = new Discipline(1, sport, "description1");
		discipline2 = new Discipline(2, sport, "description2");

		Mockito.when(mockPrincipal.getName()).thenReturn("admin");
		Mockito.when(mockUserRepository.findByUsername(mockPrincipal.getName())).thenReturn(admin);
		Mockito.when(mockSportRepository.findBySportsName(sport.getSportsName())).thenReturn(sport);
		Mockito.when(mockDisciplineRepository.findById(1)).thenReturn(Optional.of(discipline1));
		Mockito.when(mockDisciplineRepository.findById(2)).thenReturn(Optional.of(discipline2));
		Mockito.when(mockGameRepository.getAllOlympicNumber1s()).thenReturn(List.of(12343, 12344));
	}

	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testGetRequest() throws Exception {

		List<String> expectedStadiums = List.of(game.getStadium(), "stadium2", "stadium3");
		List<Discipline> expectedDisciplines = game.getDisciplines();

		Mockito.when(mockGameRepository.getAllStadiums()).thenReturn(expectedStadiums);
		Mockito.when(mockDisciplineRepository.getAllDisciplinesOfSport(sport.getSportID()))
				.thenReturn(expectedDisciplines);

		mockMvc.perform(get("/sports/" + sport.getSportsName() + "/newGame").principal(mockPrincipal))
				.andExpect(view().name("newGame")).andExpect(model().attributeExists("sport"))
				.andExpect(model().attribute("sport", sport)).andExpect(model().attributeExists("listStadiums"))
				.andExpect(model().attribute("listStadiums", expectedStadiums))
				.andExpect(model().attributeExists("listDisciplines"))
				.andExpect(model().attribute("listDisciplines", expectedDisciplines))
				.andExpect(model().attributeExists("game")).andExpect(model().attribute("game", new Game()));
	}

	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testGetRequestWithNonExistingSportsName_redirect() throws Exception {
		String nonExistingSportsName = "nonExistingSport";
		Mockito.when(mockSportRepository.findBySportsName(nonExistingSportsName)).thenReturn(null);

		mockMvc.perform(get("/sports/" + nonExistingSportsName + "/newGame").principal(mockPrincipal))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/sports"));
	}

	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testPostRequestWith2DifferentDisciplines_ok() throws Exception {
		mockMvc.perform(post("/sports/" + sport.getSportsName() + "/newGame").with(csrf()).principal(mockPrincipal)
				.flashAttr("game", game).param("discipline1id", "1").param("discipline2id", "2"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/sports/" + sport.getSportsName()));
	}

	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testPostRequestWith1Discipline_ok() throws Exception {
		mockMvc.perform(post("/sports/" + sport.getSportsName() + "/newGame").with(csrf()).principal(mockPrincipal)
				.flashAttr("game", game).param("discipline1id", "1").param("discipline2id", "-1"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/sports/" + sport.getSportsName()));
	}

	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testPostRequestWithNoDiscipline_ok() throws Exception {
		mockMvc.perform(post("/sports/" + sport.getSportsName() + "/newGame").with(csrf()).principal(mockPrincipal)
				.flashAttr("game", game).param("discipline1id", "-1").param("discipline2id", "-1"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/sports/" + sport.getSportsName()));
	}

	static Stream<LocalDateTime> dateTimeProvider() {
		return Stream.of(LocalDateTime.of(2024, 7, 1, 10, 0, 0), LocalDateTime.of(2023, 5, 17, 15, 30, 45),
				LocalDateTime.of(2025, 12, 25, 0, 0, 0), LocalDateTime.of(2024, 7, 26, 7, 59, 59),
				LocalDateTime.of(2024, 8, 12, 0, 0, 0), LocalDateTime.of(2024, 8, 1, 7, 59, 59),
				LocalDateTime.of(2024, 7, 31, 0, 59, 0), LocalDateTime.of(24, 8, 11, 0, 0, 0));
	}

	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@ParameterizedTest
	@MethodSource("dateTimeProvider")
	public void testPostRequestWithWrongStartDate_error(LocalDateTime wrongStartDate) throws Exception {
		gameToAdjust.setStart(wrongStartDate);
		mockMvc.perform(post("/sports/" + sport.getSportsName() + "/newGame").with(csrf()).principal(mockPrincipal)
				.flashAttr("game", gameToAdjust).param("discipline1id", "1").param("discipline2id", "2"))
				.andExpect(status().isOk()).andExpect(view().name("newGame"))
				.andExpect(model().attributeHasFieldErrors("game", "start"))
				.andExpect(model().attributeHasFieldErrorCode("game", "start", "ValidStart"));
	}

	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testPostRequestWith2SameDisciplines_error() throws Exception {
		mockMvc.perform(post("/sports/" + sport.getSportsName() + "/newGame").with(csrf()).principal(mockPrincipal)
				.flashAttr("game", gameToAdjust).param("discipline1id", "1").param("discipline2id", "1"))
				.andExpect(status().isOk()).andExpect(view().name("newGame"))
				.andExpect(model().attributeHasFieldErrors("game", "disciplines"))
				.andExpect(model().attributeHasFieldErrorCode("game", "disciplines", "validator.invalidDisciplines"));
	}

	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@ParameterizedTest
	@ValueSource(doubles = { 0, -15.54, 150, 150.01, 1565653.15 })
	public void testPostRequestWithWrongPrice_error(double wrongPrice) throws Exception {
		gameToAdjust.setPrice(wrongPrice);
		mockMvc.perform(post("/sports/" + sport.getSportsName() + "/newGame").with(csrf()).principal(mockPrincipal)
				.flashAttr("game", gameToAdjust).param("discipline1id", "1").param("discipline2id", "-1"))
				.andExpect(status().isOk()).andExpect(view().name("newGame"))
				.andExpect(model().attributeHasFieldErrors("game", "price"))
				.andExpect(wrongPrice <= 0 ? model().attributeHasFieldErrorCode("game", "price", "DecimalMin")
						: model().attributeHasFieldErrorCode("game", "price", "DecimalMax"));
	}

	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@ParameterizedTest
	@ValueSource(ints = { 123456, 1234, 01234, 11111, 98769, 987787, 54235, 00000 })
	public void testPostRequestWithWrongOlympicNumber1_error(int on1) throws Exception {
		gameToAdjust.setOlympicNumber1(on1);
		mockMvc.perform(post("/sports/" + sport.getSportsName() + "/newGame").with(csrf()).principal(mockPrincipal)
				.flashAttr("game", gameToAdjust).param("discipline1id", "1").param("discipline2id", "-1"))
				.andExpect(status().isOk()).andExpect(view().name("newGame"))
				.andExpect(model().attributeHasFieldErrors("game", "olympicNumber1")).andExpect(model()
						.attributeHasFieldErrorCode("game", "olympicNumber1", "validator.invalidOlympicNumber1"));
	}

	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@ParameterizedTest
	@ValueSource(ints = { 12343, 12344 })
	public void testPostRequestWithKnownOlympicNumber1_error(int on1) throws Exception {
		gameToAdjust.setOlympicNumber1(on1);
		mockMvc.perform(post("/sports/" + sport.getSportsName() + "/newGame").with(csrf()).principal(mockPrincipal)
				.flashAttr("game", gameToAdjust).param("discipline1id", "1").param("discipline2id", "-1"))
				.andExpect(status().isOk()).andExpect(view().name("newGame"))
				.andExpect(model().attributeHasFieldErrors("game", "olympicNumber1")).andExpect(model()
						.attributeHasFieldErrorCode("game", "olympicNumber1", "validator.invalidOlympicNumber1Known"));
	}

	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@ParameterizedTest
	@CsvSource({ "12346,13347", "55554,66666", "65498,75498", "12346,0", "12346,5642", "10002,9001" })
	public void testPostRequestWithWrongOlympicNumber2_error(int on1, int on2) throws Exception {
		gameToAdjust.setOlympicNumber1(on1);
		gameToAdjust.setOlympicNumber2(on2);
		mockMvc.perform(post("/sports/" + sport.getSportsName() + "/newGame").with(csrf()).principal(mockPrincipal)
				.flashAttr("game", gameToAdjust).param("discipline1id", "1").param("discipline2id", "-1"))
				.andExpect(status().isOk()).andExpect(view().name("newGame"))
				.andExpect(model().attributeHasFieldErrors("game", "olympicNumber2")).andExpect(model()
						.attributeHasFieldErrorCode("game", "olympicNumber2", "validator.invalidOlympicNumber2"));
	}

	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@ParameterizedTest
	@CsvSource({ "12346,13346", "55554,55555", "10000,9999", "10002,9002", "98747,97852", "99998,100998" })
	public void testPostRequestWithCorrectOlympicNumbers_ok(int on1, int on2) throws Exception {
		gameToAdjust.setOlympicNumber1(on1);
		gameToAdjust.setOlympicNumber2(on2);

		mockMvc.perform(post("/sports/" + sport.getSportsName() + "/newGame").with(csrf()).principal(mockPrincipal)
				.flashAttr("game", game).param("discipline1id", "1").param("discipline2id", "2"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/sports/" + sport.getSportsName()));
	}

	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@ParameterizedTest
	@ValueSource(ints = { -99,-1,51,65565,100})
	public void testPostRequestWithWrongFreePlaces_error(int freePlaces) throws Exception {
		gameToAdjust.setFreePlaces(freePlaces);
		mockMvc.perform(post("/sports/" + sport.getSportsName() + "/newGame").with(csrf()).principal(mockPrincipal)
				.flashAttr("game", gameToAdjust).param("discipline1id", "1").param("discipline2id", "-1"))
				.andExpect(status().isOk()).andExpect(view().name("newGame"))
				.andExpect(model().attributeHasFieldErrors("game", "freePlaces")).andExpect(freePlaces<0?model()
						.attributeHasFieldErrorCode("game", "freePlaces", "Min"):model()
						.attributeHasFieldErrorCode("game", "freePlaces", "Max"));
	}
}
