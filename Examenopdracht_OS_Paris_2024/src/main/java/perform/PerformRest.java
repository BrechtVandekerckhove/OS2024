package perform;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.web.reactive.function.client.WebClient;

import domain.Game;
import domain.Seat;
import reactor.core.publisher.Mono;
import static utils.InitFormatter.*;

public class PerformRest {
	private final String SERVER_URI = "http://localhost:8080/rest";
	private WebClient webClient = WebClient.create();

	public PerformRest() {
		IntStream.rangeClosed(1, 16).forEach(gameID -> {
			try {
				System.out.printf("------- GET ALL FREEPLACES OF GAME WITH ID %d ------- %n", gameID);
				getFreePlaces(gameID);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		});

		IntStream.rangeClosed(1, 5).forEach(sportID -> {
			try {
				System.out.printf("\n------- GET ALL GAMES OF SPORT WITH ID %d -------%n", sportID);
				getGamesOfSport(sportID);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		});

	}

	private void getGamesOfSport(int sportID) {
		webClient.get().uri(SERVER_URI + "/sport/" + sportID).retrieve().bodyToFlux(Game.class).flatMap(game -> {
			printGameData(game);
			return Mono.empty();
		}).blockLast();

	}

	private void printGameData(Game game) {
		System.out.printf(
				"- Game ID=%d, sport=%s, start=%s,%nstadium=%s, discipline(s)=[%s], %nolympic number 1=%d, olympic number 2=%d, free places=%d, price=%s%n",
				game.getGameID(), game.getSport().getSportsName(), FORMATTERDATE.format(game.getStart()),
				game.getStadium(), game.getDisciplines().stream().map(d -> String.format("id=%d description=%s",d.getId(), d.getDescription())).collect(Collectors.joining(", ")), game.getOlympicNumber1(), game.getOlympicNumber2(),
				game.getFreePlaces(), FORMATTERPRICE.format(game.getPrice()));

	}

	private void getFreePlaces(int gameID) {

		webClient.get().uri(SERVER_URI + "/game/" + gameID + "/freePlaces").retrieve().bodyToFlux(Seat.class)
				.flatMap(seat -> {
					printSeatData(seat);
					return Mono.empty();
				}).blockLast();

	}

	private void printSeatData(Seat seat) {
		System.out.printf("- Seat ID=%d, row=%s, seat_nr=%d%n", seat.getId(), seat.getRow_(), seat.getSeatnr()
				);
	}
}
