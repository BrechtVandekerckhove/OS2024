package service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import domain.Discipline;
import domain.Game;
import repository.DisciplineRepository;


public class GameServiceImpl implements GameService {
	@Autowired
	private DisciplineRepository disciplineRepository;

	@Override
	public void addDisciplines(Game game, int discipline1id, int discipline2id) {
		for (int id : List.of(discipline1id, discipline2id)) {
			Optional<Discipline> d = disciplineRepository.findById(id);
			if (d.isPresent()) {
				game.addDiscipline(d.get());
			}
		}
	}
	
	


}
