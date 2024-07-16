package domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "sportID")
@ToString(of = "sportsName")

public class Sport implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty("sport_id")
	private int sportID;
	@JsonProperty("sport_name")
	private String sportsName;

	@OneToMany(mappedBy = "sport")
	private final Set<Game> games = new HashSet<>();

	@OneToMany(mappedBy = "sport")
	private final Set<Discipline> disciplines = new HashSet<>();

	public Sport(String sportsName) {
		this.sportsName = sportsName;
	}

	public Sport(int id, String sportsName) {
		this(sportsName);
		this.sportID = id;
	}

	public Set<Game> getGames() {
		return Collections.unmodifiableSet(games);
	}

	public Set<Discipline> getDisciplines() {
		return Collections.unmodifiableSet(disciplines);
	}

	public void addGame(Game game) {
		games.add(game);
	}

}
