package com.springBoot_OS_Paris_2024;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import domain.Discipline;
import domain.Game;
import domain.MyUser;
import domain.Role;
import domain.Sport;
import repository.DisciplineRepository;
import repository.GameRepository;
import repository.SportRepository;
import repository.UserRepository;

@Component
public class InitDataConfig implements CommandLineRunner {

	private PasswordEncoder encoder = new BCryptPasswordEncoder();

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SportRepository sportRepository;
	@Autowired
	private GameRepository gameRepository;
	@Autowired
	private DisciplineRepository disciplineRepository;



	@Override
	public void run(String... args) {

		var brecht = MyUser.builder().username("Brecht").name("Brecht").role(Role.USER)
				.password(encoder.encode("Brecht")).build();
		var user = MyUser.builder().username("user").name("user").role(Role.USER).password(encoder.encode("user"))
				.build();
		var noTickets = MyUser.builder().username("noTickets").name("noTickets").role(Role.USER)
				.password(encoder.encode("noTickets")).build();
		var admin = MyUser.builder().username("admin").name("admin").role(Role.ADMIN).password(encoder.encode("admin"))
				.build();

		List<MyUser> userList = Arrays.asList(admin, brecht, user, noTickets);
		userRepository.saveAll(userList);

		Sport judo = new Sport("judo");
		Sport tableTennis = new Sport("tableTennis");
		Sport cyclingRoad = new Sport("cyclingRoad");
		Sport golf = new Sport("golf");
		Sport artisticGymnastics = new Sport("artisticGymnastics");
		List<Sport> sportList = Arrays.asList(judo, tableTennis, cyclingRoad, golf, artisticGymnastics);
		sportRepository.saveAll(sportList);

		Game judoGame1 = new Game(judo, LocalDateTime.of(2024, 7, 27, 10, 0, 0, 0), "Champ-de-Mars Arena", 12345, 12346,
				10, 20);

		Game judoGame2 = new Game(judo, LocalDateTime.of(2024, 7, 27, 10, 28, 0, 0), "Champ-de-Mars Arena", 12347,
				12348, 10, 20);

		Game tableTennisGame1 = new Game(tableTennis, LocalDateTime.of(2024, 7, 27, 9, 0, 0), "South Paris Arena #4",
				12349, 12350, 20, 15);

		Game tableTennisGame2 = new Game(tableTennis, LocalDateTime.of(2024, 7, 29, 14, 0, 0), "South Paris Arena #4",
				22351, 22352, 20, 25);

		Game cyclingRoadGame1 = new Game(cyclingRoad, LocalDateTime.of(2024, 8, 3, 10, 0, 0), "Champs-Élysées", 12353,
				12354, 20, 110);
		Game cyclingRoadGame2 = new Game(cyclingRoad, LocalDateTime.of(2024, 8, 3, 10, 0, 0), "Banlieu Paris", 12355,
				12356, 20, 100);
		Game cyclingRoadGame3 = new Game(cyclingRoad, LocalDateTime.of(2024, 7, 27, 10, 0, 0), "Champs-Élysées", 12357,
				12358, 20, 120);
		Game cyclingRoadGame4 = new Game(cyclingRoad, LocalDateTime.of(2024, 8, 4, 10, 0, 0), "Banlieu Paris", 12359,
				12360, 20, 140);

		Game golfGame1 = new Game(golf, LocalDateTime.of(2024, 8, 1, 10, 0, 0), "Le Golf National I", 22361, 22362, 30,
				100);
		Game golfGame2 = new Game(golf, LocalDateTime.of(2024, 8, 2, 10, 0, 0), "Le Golf National I", 12363, 12364, 30,
				100);
		Game golfGame3 = new Game(golf, LocalDateTime.of(2024, 8, 3, 10, 0, 0), "Le Golf National II", 12365, 12366, 30,
				100);
		Game golfGame4 = new Game(golf, LocalDateTime.of(2024, 8, 4, 10, 0, 0), "Le Golf National II", 12367, 12368, 1,
				100);

		Game gymnasticsGame1 = new Game(artisticGymnastics, LocalDateTime.of(2024, 7, 27, 10, 0, 0), "Bercy Arena",
				12369, 12370, 50, 100);
		Game gymnasticsGame2 = new Game(artisticGymnastics, LocalDateTime.of(2024, 8, 3, 10, 0, 0), "Bercy Arena",
				22371, 22372, 50, 100);
		Game gymnasticsGame3 = new Game(artisticGymnastics, LocalDateTime.of(2024, 7, 28, 10, 0, 0), "Bercy Arena",
				12373, 12374, 0, 145);
		Game gymnasticsGame4 = new Game(artisticGymnastics, LocalDateTime.of(2024, 8, 4, 10, 0, 0), "Bercy Arena",
				12375, 12376, 1, 145);
		List<Game> gameList = Arrays.asList(judoGame1, judoGame2, tableTennisGame1, tableTennisGame2, cyclingRoadGame1,
				cyclingRoadGame2, cyclingRoadGame3, cyclingRoadGame4, golfGame1, golfGame2, golfGame3, golfGame4,
				gymnasticsGame1, gymnasticsGame2, gymnasticsGame3, gymnasticsGame4);

		userRepository.saveAll(userList);
		sportRepository.saveAll(sportList);
		gameRepository.saveAll(gameList);

		Discipline d1 = new Discipline(judo, "W48R64");
		Discipline d2 = new Discipline(judo, "M60R64");
		Discipline d3 = new Discipline(judo, "W48R32");
		Discipline d4 = new Discipline(judo, "M60R32");
		Discipline d5 = new Discipline(tableTennis, "WSR1");
		Discipline d6 = new Discipline(tableTennis, "MSR1");
		Discipline d7 = new Discipline(tableTennis, "WSR2");
		Discipline d8 = new Discipline(tableTennis, "MSR2");
		Discipline d9 = new Discipline(cyclingRoad, "MRoadRace");
		Discipline d10 = new Discipline(cyclingRoad, "WRoadRace");
		Discipline d11 = new Discipline(cyclingRoad, "MTimeTrail");
		Discipline d12 = new Discipline(cyclingRoad, "WTimeTrail");
		Discipline d13 = new Discipline(golf, "MR1");
		Discipline d14 = new Discipline(golf, "MR2");
		Discipline d15 = new Discipline(golf, "MR3");
		Discipline d16 = new Discipline(golf, "MFR");
		Discipline d17 = new Discipline(artisticGymnastics, "MFQ");
		Discipline d18 = new Discipline(artisticGymnastics, "MFF");
		Discipline d19 = new Discipline(artisticGymnastics, "WFQ");
		Discipline d20 = new Discipline(artisticGymnastics, "WFF");

		List<Discipline> disciplineList = Arrays.asList(d1, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12, d13, d14,
				d15, d16, d17, d18, d19, d20);
		disciplineRepository.saveAll(disciplineList);

		judoGame1.addDiscipline(d1);
		judoGame1.addDiscipline(d2);
		judoGame2.addDiscipline(d3);
		judoGame2.addDiscipline(d4);
		tableTennisGame1.addDiscipline(d5);
		tableTennisGame1.addDiscipline(d6);
		tableTennisGame2.addDiscipline(d7);
		tableTennisGame2.addDiscipline(d8);
		cyclingRoadGame1.addDiscipline(d9);
		cyclingRoadGame2.addDiscipline(d10);
		cyclingRoadGame3.addDiscipline(d11);
		cyclingRoadGame4.addDiscipline(d12);
		golfGame1.addDiscipline(d13);
		golfGame2.addDiscipline(d14);
		golfGame3.addDiscipline(d15);
		golfGame4.addDiscipline(d16);
		gymnasticsGame1.addDiscipline(d17);
		gymnasticsGame2.addDiscipline(d18);
		gymnasticsGame3.addDiscipline(d19);
		gymnasticsGame4.addDiscipline(d20);

		
		disciplineRepository.saveAll(disciplineList);

		brecht.addNumberOfTicketsForGame(judoGame1, 2);
		brecht.addNumberOfTicketsForGame(gymnasticsGame2, 3);
		brecht.addNumberOfTicketsForGame(gymnasticsGame4, 1);
		brecht.addNumberOfTicketsForGame(golfGame1, 4);
		brecht.addNumberOfTicketsForGame(golfGame2, 2);

		user.addNumberOfTicketsForGame(cyclingRoadGame1, 4);
		user.addNumberOfTicketsForGame(cyclingRoadGame3, 5);

		userRepository.saveAll(userList);
		sportRepository.saveAll(sportList);
		gameRepository.saveAll(gameList);
	
	}

}
