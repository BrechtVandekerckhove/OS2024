package validator;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import domain.Game;
import domain.MyUser;
import domain.Ticket;
import domain.TicketRequest;
import repository.GameRepository;

public class TicketValidation implements Validator {

	@Autowired
	private GameRepository gameRepository;

	@Override
	public boolean supports(Class<?> klass) {
		return TicketRequest.class.isAssignableFrom(klass);
	}

	@Override
	public void validate(Object target, Errors errors) {

		TicketRequest ticketRequest = (TicketRequest) target;

		List<Ticket> listTickets = ticketRequest.getMyUser().getTickets();

		Optional<Game> game = gameRepository.findById(ticketRequest.getGameID());
		if (game.isPresent()) {
			int freePlaces = game.get().getFreePlaces();
			if (ticketRequest.getNumberOfTickets() > freePlaces) {
				if (freePlaces > 1) {
					errors.rejectValue("numberOfTickets", "validator.maxTicketsFreePlaces", new Object[] { freePlaces },
							"");
				} else {
					errors.rejectValue("numberOfTickets", "validator.maxTicketsOneFreePlace", new Object[] { freePlaces },
							"");
				}
			}
		}
		if (listTickets.size() + ticketRequest.getNumberOfTickets() > MyUser.MAX_TICKETS) {
			errors.rejectValue("numberOfTickets", "validator.maxTickets", new Object[] { MyUser.MAX_TICKETS }, "");
		}

		int ticketsGame = (int) listTickets.stream().filter(t -> t.getGame().getGameID() == ticketRequest.getGameID())
				.count();

		if (ticketsGame + ticketRequest.getNumberOfTickets() > MyUser.MAX_TICKETS_PER_GAME) {

			errors.rejectValue("numberOfTickets", "validator.maxTicketsGame",
					new Object[] { MyUser.MAX_TICKETS_PER_GAME }, "");
		}

	}
}
