package domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.format.annotation.NumberFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import utils.LocalDateTimeDeserializer;
import utils.LocalDateTimeSerializer;
import utils.PriceDeserializer;
import utils.PriceSerializer;
import utils.SportDeserializer;
import utils.SportSerializer;
import validator.ValidStart;

@Entity
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@EqualsAndHashCode(of = "gameID")
@ToString(exclude = { "gameID", "tickets", "seats" })
@Getter
@Setter
@JsonPropertyOrder({ "game_id", "sport", "start", "stadium", "disciplines", "olympic_number_1", "olympic_number_2",
		"free_places", "price" })
public class Game implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty("game_id")
	private int gameID;
	@ManyToOne
	@JsonSerialize(using = SportSerializer.class)
	@JsonDeserialize(using = SportDeserializer.class)
	private Sport sport;
	@ValidStart
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime start;
	@NotBlank
	private String stadium;
	@ManyToMany
	private List<Discipline> disciplines = new ArrayList<>();
	@NotNull(message = "{validation.Game.FreePlaces.NotNull.message}")
	@Min(value = 0, message = "{validation.Game.freePlaces.Min.message}")
	@Max(value = 50, message = "{validation.Game.freePlaces.Max.message}")
	@JsonProperty("free_places")
	private int freePlaces;
	@JsonProperty("olympic_number_1")
	private int olympicNumber1;
	@JsonProperty("olympic_number_2")
	private int olympicNumber2;
	@NotNull
	@DecimalMin(value = "0.01", message = "{validation.Game.Price.Min.message}")
	@DecimalMax(value = "149.99", message = "{validation.Game.Price.Max.message}")
	@NumberFormat(pattern = "€ #,##0.00")
	@JsonSerialize(using = PriceSerializer.class)
	@JsonDeserialize(using = PriceDeserializer.class)
	private double price;
	@OneToMany(mappedBy = "game")
	@JsonIgnore
	private final Set<Ticket> tickets = new HashSet<>();
	@OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
	@JsonIgnore
	private final List<Seat> seats = new ArrayList<>();

	public Game(Sport sport, LocalDateTime start, String stadium, int olympicNumber1, int olympicNumber2,
			int freePlaces, double price) {
		super();
		this.sport = sport;
		this.start = start;
		this.stadium = stadium;
		this.olympicNumber1 = olympicNumber1;
		this.olympicNumber2 = olympicNumber2;
		this.freePlaces = freePlaces;
		this.price = price;
		addSeats();
	}
	

	public Game( int gameID, Sport sport, LocalDateTime start, String stadium, int olympicNumber1, int olympicNumber2,
			int freePlaces, double price, List<Discipline> disciplines) {
		this(sport, start, stadium, olympicNumber1, olympicNumber2, freePlaces, price);
		this.gameID=gameID;
		this.disciplines = disciplines;
	}

	public void addSeats() {
		int number = freePlaces;
		char[] rows = new char[] { 'A', 'B', 'C', 'D', 'E' };
		for (char r : rows) {
			for (int seatnr = 1; seatnr <= 10; seatnr++) {
				if (number == 0)
					break;
				seats.add(new Seat(this, r, seatnr));
				number--;
			}
		}
	}

	public List<Discipline> getDisciplines() {
		return Collections.unmodifiableList(disciplines);
	}

	// public zorgt ervoor dat freeSeat in JSON komt van Sport...
	protected Seat getFreeSeat() {
		Seat s = seats.remove(0);
		while (s.getTicket() != null) {
			s = seats.remove(0);
		}
		return s;
	}

	public void addDiscipline(Discipline d) {
		disciplines.add(d);
	}
}
