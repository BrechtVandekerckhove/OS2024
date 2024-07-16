package repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import domain.Game;
import domain.Ticket;

public interface TicketRepository extends CrudRepository<Ticket, Integer> {

	@Query("SELECT t FROM Ticket t WHERE t.user.id = :user_id ORDER BY t.game.sport.sportsName, t.game.start")
	List<Ticket> findByUserIDOrderedBySportsNameAndStartDate(@Param("user_id") Integer id);

	@Query("SELECT t.game, COUNT(t) FROM Ticket t WHERE t.user.id = :user_id  GROUP BY t.game ORDER BY t.game.sport.sportsName, t.game.start")
	// Object[] = [Game g, Integer number] ==> use default Map<Game,Integer> function
	List<Object[]> countTicketsByGameForUserOrderedBySportsNameAndStartDate(@Param("user_id") Integer id);

	@Query("SELECT g, COUNT(t) FROM Game g LEFT JOIN Ticket t ON g.gameID = t.game.gameID AND t.user.id = :user_id WHERE g.sport.id = :sport_id GROUP BY g")
	// Object[] = [Game g, Integer number] ==> use default Map<Game,Integer> function
	List<Object[]> countTicketsPerGameOfSportIDForUserID(@Param("sport_id") Integer sport_id,
			@Param("user_id") Integer user_id);

	@Query("SELECT COUNT(t) FROM Ticket t WHERE t.game.gameID=:game_id AND t.user.id=:user_id")
	Integer countTicketsOfGameIDForUserID(@Param("game_id") Integer game_id, @Param("user_id") Integer user_id);

	default Map<Game, Integer> countTicketsByGameForUserOrderedBySportsNameAndStartDateAsMap(Integer id) {
		return countTicketsByGameForUserOrderedBySportsNameAndStartDate(id).stream()
				.collect(Collectors.toMap(arr -> (Game) arr[0], arr -> ((Number) arr[1]).intValue()));
	}

	default Map<Game, Integer> countTicketsPerGameOfSportIDForUserIDAsMap(Integer sport_id, Integer user_id) {
		return countTicketsPerGameOfSportIDForUserID(sport_id, user_id).stream()
				.collect(Collectors.toMap(arr -> (Game) arr[0], arr -> ((Number) arr[1]).intValue()));
	}

}
