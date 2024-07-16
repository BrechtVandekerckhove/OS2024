package com.springBoot_OS_Paris_2024;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static utils.InitFormatter.FORMATTERDATE;
import static utils.InitFormatter.FORMATTERPRICE;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import domain.Discipline;
import domain.Game;
import domain.Seat;
import domain.Sport;
import exceptions.GamesNotFoundException;
import exceptions.NoFreeSeatsException;
import exceptions.SportNotFoundException;
import repository.GameRepository;
import repository.SeatRepository;
import repository.SportRepository;

@SpringBootTest
class OSParis2024RestMockTest {

	@Mock
	private SportRepository mockSport;
	@Mock
	private GameRepository mockGame;
	@Mock
	private SeatRepository mockSeat;

	private OSParis2024RestController controller;
	private MockMvc mockMvc;

	private final int SPORTID = 2;
	private final int GAMEID1 = 10;
	private final int GAMEID2 = 20;
	private final String SPORTNAME = "testSport";
	private final String DISCIPLINE_DESCRIPTION1 = "testDiscipline";
	private final LocalDateTime START1 = LocalDateTime.of(2024, 8, 1, 10, 0);
	private final String STADIUM1 = "testStadium";
	private final int OLYMPIC_NUMBER1_1 = 12345;
	private final int OLYMPIC_NUMBER2_1 = 12346;
	private final int FREE_PLACES1 = 10;
	private final double PRICE1 = 50;
	private final String DISCIPLINE_DESCRIPTION2 = "test3Discipline";
	private final String DISCIPLINE_DESCRIPTION3 = "test3Discipline";
	private final LocalDateTime START2 = LocalDateTime.of(2024, 7, 31, 12, 0);
	private final String STADIUM2 = "test2Stadium";
	private final int OLYMPIC_NUMBER1_2 = 22345;
	private final int OLYMPIC_NUMBER2_2 = 22346;
	private final int FREE_PLACES2 = 40;
	private Optional<Sport> sport;
	private Game game;

	@BeforeEach
	public void before() {
		MockitoAnnotations.openMocks(this);
		controller = new OSParis2024RestController();
		mockMvc = standaloneSetup(controller).build();
		ReflectionTestUtils.setField(controller, "sportRepository", mockSport);
		ReflectionTestUtils.setField(controller, "gameRepository", mockGame);
		ReflectionTestUtils.setField(controller, "seatRepository", mockSeat);
		sport = aSport(SPORTID, SPORTNAME);
		game = aGame(GAMEID1, sport.get(), START1, STADIUM1, OLYMPIC_NUMBER1_1, OLYMPIC_NUMBER2_1, FREE_PLACES1, PRICE1,
				List.of(aDiscipline(sport.get(), DISCIPLINE_DESCRIPTION1)));
	}

	private Game aGame(int gameID, Sport sport, LocalDateTime start, String stadium, int olympicNumber1,
			int olympicNumber2, int freePlaces, double price, List<Discipline> disciplines) {
		return new Game(gameID, sport, start, stadium, olympicNumber1, olympicNumber2, freePlaces, price,
				disciplines);
	}

	private Optional<Sport> aSport(int sportID, String sportName) {
		Sport sport = new Sport(sportID, sportName);
		return Optional.ofNullable(sport);
	}

	private Discipline aDiscipline(Sport sport, String description) {
		return new Discipline(sport, description);
	}

	private Seat aSeat(Game game, char row, int number) {
		return new Seat(game, row, number);
	}

	@Test
	public void testGetGamesOfSport_noSportFound() throws Exception {
		Optional<Sport> sport = Optional.empty();
		Mockito.when(mockSport.findById(SPORTID)).thenReturn(sport);
		Exception exception = assertThrows(Exception.class, () -> {
			mockMvc.perform(get("/rest/sport/" + SPORTID)).andReturn();
		});
		assertTrue(exception.getCause() instanceof SportNotFoundException);

		Mockito.verify(mockSport).findById(SPORTID);
	}

	@Test
	public void testGetGamesOfSport_noGamesFound() throws Exception {
		Mockito.when(mockSport.findById(SPORTID)).thenReturn(sport);
		Mockito.when(mockGame.findAllBySport(sport.get())).thenReturn(new ArrayList<>());

		Exception exception = assertThrows(Exception.class, () -> {
			mockMvc.perform(get("/rest/sport/" + SPORTID)).andReturn();
		});
		assertTrue(exception.getCause() instanceof GamesNotFoundException);

		Mockito.verify(mockSport).findById(SPORTID);
		Mockito.verify(mockGame).findAllBySport(sport.get());
	}

	@ParameterizedTest
	@CsvSource(value = { "10.5\t€10,50", "7\t€7,00", "100.99\t€100,99" }, delimiter = '\t')
	public void testGetGamesOfSport_noEmptyList(double price, String expectedFormattedPrice) throws Exception {
		Game game2 = aGame(GAMEID2, sport.get(), START2, STADIUM2, OLYMPIC_NUMBER1_2, OLYMPIC_NUMBER2_2, FREE_PLACES2,
				price, List.of(aDiscipline(sport.get(), DISCIPLINE_DESCRIPTION2),
						aDiscipline(sport.get(), DISCIPLINE_DESCRIPTION3)));

		String formattedPrice = FORMATTERPRICE.format(game2.getPrice());
		assertEquals(expectedFormattedPrice, formattedPrice);

		Mockito.when(mockSport.findById(SPORTID)).thenReturn(sport);
		Mockito.when(mockGame.findAllBySport(sport.get())).thenReturn(List.of(game, game2));

		mockMvc.perform(get("/rest/sport/" + SPORTID)).andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$[0].game_id").value(GAMEID1))
				.andExpect(jsonPath("$[0].sport.sport_id").value(SPORTID))
				.andExpect(jsonPath("$[0].sport.sport_name").value(SPORTNAME))
				.andExpect(jsonPath("$[0].start").value(START1.format(FORMATTERDATE)))
				.andExpect(jsonPath("$[0].stadium").value(STADIUM1))
				.andExpect(jsonPath("$[0].disciplines[0].discipline_id").isNumber())
				.andExpect(jsonPath("$[0].disciplines[0].description").value(DISCIPLINE_DESCRIPTION1))
				.andExpect(jsonPath("$[0].olympic_number_1").value(OLYMPIC_NUMBER1_1))
				.andExpect(jsonPath("$[0].olympic_number_2").value(OLYMPIC_NUMBER2_1))
				.andExpect(jsonPath("$[0].free_places").value(FREE_PLACES1))
				.andExpect(jsonPath("$[0].price").value(FORMATTERPRICE.format(PRICE1)))
				.andExpect(jsonPath("$[1].game_id").value(GAMEID2))
				.andExpect(jsonPath("$[1].sport.sport_id").value(SPORTID))
				.andExpect(jsonPath("$[1].sport.sport_name").value(SPORTNAME))
				.andExpect(jsonPath("$[1].start").value(START2.format(FORMATTERDATE)))
				.andExpect(jsonPath("$[1].stadium").value(STADIUM2))
				.andExpect(jsonPath("$[1].disciplines[0].discipline_id").isNumber())
				.andExpect(jsonPath("$[1].disciplines[0].description").value(DISCIPLINE_DESCRIPTION2))
				.andExpect(jsonPath("$[1].disciplines[1].discipline_id").isNumber())
				.andExpect(jsonPath("$[1].disciplines[1].description").value(DISCIPLINE_DESCRIPTION3))
				.andExpect(jsonPath("$[1].olympic_number_1").value(OLYMPIC_NUMBER1_2))
				.andExpect(jsonPath("$[1].olympic_number_2").value(OLYMPIC_NUMBER2_2))
				.andExpect(jsonPath("$[1].free_places").value(FREE_PLACES2))
				.andExpect(jsonPath("$[1].price").value(FORMATTERPRICE.format(price)));

		Mockito.verify(mockSport).findById(SPORTID);
		Mockito.verify(mockGame).findAllBySport(sport.get());
	}

	@Test
	public void testGetOverviewFreePlacesOfGame_noFreeSeatsFound() throws Exception {
		Mockito.when(mockSeat.getFreeSeatsOfGameID(GAMEID1)).thenReturn(new ArrayList<>());
		Exception exception = assertThrows(Exception.class, () -> {
			mockMvc.perform(get("/rest/game/" + GAMEID1 + "/freePlaces")).andReturn();
		});
		assertTrue(exception.getCause() instanceof NoFreeSeatsException);

		Mockito.verify(mockSeat).getFreeSeatsOfGameID(GAMEID1);
	}

	@Test
	public void testGetOverviewFreePlacesOfGame_noEmptyList() throws Exception {
		Mockito.when(mockSeat.getFreeSeatsOfGameID(GAMEID1))
				.thenReturn(List.of(aSeat(game, 'A', 1), aSeat(game, 'A', 2), aSeat(game, 'B', 15)));
		mockMvc.perform(get("/rest/game/" + GAMEID1 + "/freePlaces")).andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$[0].seat_id").isNumber()).andExpect(jsonPath("$[0].row").value("A"))
				.andExpect(jsonPath("$[0].seat_nr").value("1")).andExpect(jsonPath("$[1].seat_id").isNumber())
				.andExpect(jsonPath("$[1].row").value("A")).andExpect(jsonPath("$[1].seat_nr").value("2"))
				.andExpect(jsonPath("$[2].seat_id").isNumber()).andExpect(jsonPath("$[2].row").value("B"))
				.andExpect(jsonPath("$[2].seat_nr").value("15"));

		Mockito.verify(mockSeat).getFreeSeatsOfGameID(GAMEID1);
	}

}
