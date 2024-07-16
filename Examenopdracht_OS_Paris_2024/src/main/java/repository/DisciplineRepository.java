package repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import domain.Discipline;

public interface DisciplineRepository extends CrudRepository<Discipline, Integer> {

	@Query("SELECT d FROM Discipline d WHERE d.sport.sportID=:sportID")
	List<Discipline> getAllDisciplinesOfSport(int sportID);

}
