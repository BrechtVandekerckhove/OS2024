package service;

import org.springframework.transaction.annotation.Transactional;

import domain.Game;

public interface GameService {
	@Transactional
	public void addDisciplines(Game game, int discipline1id, int discipline2id);

}
