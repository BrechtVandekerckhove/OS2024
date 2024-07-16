package service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import domain.Game;
import domain.MyUser;
import repository.GameRepository;

public class MyUserServiceImpl implements MyUserService {

	@Autowired
	private GameRepository gameRepository;

	@Override
	public void addTickets(MyUser myUser, int gameId, int number) {
		Optional<Game> game = gameRepository.findById(gameId);
		if (game.isPresent()) {
			myUser.addNumberOfTicketsForGame(game.get(), number);
		}
	}

}
