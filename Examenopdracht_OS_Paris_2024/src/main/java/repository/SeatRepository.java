package repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import domain.Seat;

public interface SeatRepository extends CrudRepository<Seat, Integer> {
	@Query("SELECT s FROM Seat s WHERE s.game.gameID=:game_id AND s.id  NOT IN (SELECT t.seat.id FROM Ticket t where t.game.gameID=:game_id)")
	List<Seat> getFreeSeatsOfGameID(@Param("game_id") int id);
}
