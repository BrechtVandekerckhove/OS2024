package domain;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TicketRequest {
	private MyUser myUser;
	@Min(value=1, message ="{validation.TicketRequest.numberOfTickets.Min.message}")
	private int numberOfTickets;
	private int gameID;
}
