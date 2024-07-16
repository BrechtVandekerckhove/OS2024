package domain;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode
@ToString
public class Ticket implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int ticketID;

	@ManyToOne
	private Game game;

	@ManyToOne
	private MyUser user;
	@OneToOne
	private Seat seat;

	public Ticket(Game game, MyUser user) {
		this.game = game;
		this.user = user;
		this.seat = game.getFreeSeat();

	}

}
