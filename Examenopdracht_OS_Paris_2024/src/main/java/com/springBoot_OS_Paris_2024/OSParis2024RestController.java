package com.springBoot_OS_Paris_2024;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import domain.Game;
import domain.Seat;
import domain.Sport;
import exceptions.GamesNotFoundException;
import exceptions.NoFreeSeatsException;
import exceptions.SportNotFoundException;
import repository.GameRepository;
import repository.SeatRepository;
import repository.SportRepository;

@RestController
@RequestMapping("rest")
public class OSParis2024RestController {
	@Autowired
	private SportRepository sportRepository;
	@Autowired
	private GameRepository gameRepository;
	@Autowired
	private SeatRepository seatRepository;

	@GetMapping(value = "/sport/{id}")
	public List<Game> getGamesOfSport(@PathVariable("id") int sportID) {
		Optional<Sport> sport = sportRepository.findById(sportID);
		if (!sport.isPresent()) {
			throw new SportNotFoundException(sportID);
		}
		List<Game> list = gameRepository.findAllBySport(sport.get());
		if (list.isEmpty()) {
			throw new GamesNotFoundException(sportID);
		}
		return Collections.unmodifiableList(list);
	}

	@GetMapping(value = "/game/{id}/freePlaces")
	public List<Seat> getOverviewFreePlacesOfGame(@PathVariable("id") int gameID) {
		List<Seat> list = seatRepository.getFreeSeatsOfGameID(gameID);
		if(list.isEmpty()) {
			throw new NoFreeSeatsException(gameID);
		}
		return Collections.unmodifiableList(list);
	}
}
