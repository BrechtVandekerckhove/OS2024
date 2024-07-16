package domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString (exclude="game")
@JsonPropertyOrder({"seat_id","row","seat_nr"})
public class Seat implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty("seat_id")
	private int id;
	@ManyToOne 
	@JsonIgnore
	private Game game;
	@JsonProperty("row")
	private char row_;
	@JsonProperty("seat_nr")
	private int seatnr;
	@OneToOne (mappedBy="seat")
	@JsonIgnore
	private Ticket ticket;

	public Seat(Game game, char row_, int seatnr) {
		this.game=game;
		this.row_ = row_;
		this.seatnr = seatnr;
	}
}
