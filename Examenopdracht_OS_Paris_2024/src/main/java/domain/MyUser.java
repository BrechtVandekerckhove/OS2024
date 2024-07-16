package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "username")
@Table(name = "user")
@ToString(exclude = "tickets")
public class MyUser implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final int MAX_TICKETS = 100;
	public static final int MAX_TICKETS_PER_GAME = 20;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private Role role;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private final List<Ticket> tickets = new ArrayList<>();

	public void addNumberOfTicketsForGame(Game game, Integer number) {
		for (int i = 0; i < number; i++) {
			tickets.add(new Ticket(game, this));
		}
		game.setFreePlaces(game.getFreePlaces()-number);
	}

}
