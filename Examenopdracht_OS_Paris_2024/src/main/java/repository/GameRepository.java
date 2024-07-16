package repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import domain.Game;
import domain.Sport;

public interface GameRepository extends CrudRepository<Game, Integer> {

	List<Game> findAllBySport(Sport sport);

	@Query("SELECT DISTINCT g.stadium FROM Game g")
	List<String> getAllStadiums();

	@Query("SELECT g.olympicNumber1 FROM Game g")
	List<Integer> getAllOlympicNumber1s();

	}
