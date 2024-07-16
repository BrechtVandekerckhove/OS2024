package com.springBoot_OS_Paris_2024;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import domain.Game;
import domain.MyUser;
import domain.Role;
import domain.Sport;
import domain.TicketRequest;
import jakarta.validation.Valid;
import repository.GameRepository;
import repository.SportRepository;
import repository.TicketRepository;
import repository.UserRepository;
import service.MyUserService;
import validator.TicketValidation;

@Controller
@RequestMapping("/sports/{sportsName}/tickets-game={gameID}")
public class TicketController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SportRepository sportRepository;
	@Autowired
	private GameRepository gameRepository;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private TicketValidation ticketValidation;
	@Autowired
	private MyUserService myUserService;

	@ModelAttribute("username")
	public String populateUsername(Principal principal) {
		return principal.getName();
	}

	@ModelAttribute("role")
	public Role populateRole(Principal principal) {
		return getUser(principal).getRole();
	}

	@GetMapping
	public String showTicketPage(@PathVariable String sportsName, @PathVariable Integer gameID, Model model,
			Principal principal) {
		Sport sport = getSportAndAddToModel(sportsName, model);
		Optional<Game> game = gameRepository.findById(gameID);
		if (sport == null || game.isEmpty()) {
			return "redirect:/sports";
		}
		model.addAttribute("numberOfTickets",
				ticketRepository.countTicketsOfGameIDForUserID(gameID, getUser(principal).getId()));
		model.addAttribute("game", game.get());
		model.addAttribute("ticketRequest", new TicketRequest());
		return "ticketsGame";
	}

	@PostMapping
	public String onSubmitTickets(@Valid TicketRequest ticketRequest, BindingResult result,
			@PathVariable String sportsName, @PathVariable Integer gameID, Model model, RedirectAttributes ra,
			Principal principal) {
		ticketRequest.setMyUser(getUser(principal));
		ticketRequest.setGameID(gameID);
		ticketValidation.validate(ticketRequest, result);

		Optional<Game> game = gameRepository.findById(gameID);
		if (game.isEmpty()) {
			return "redirect:/sports";
		}
		if (result.hasErrors()) {
			Sport sport = getSportAndAddToModel(sportsName, model);
			if (sport == null) {
				return "redirect:/sports";
			}
			model.addAttribute("numberOfTickets",
					ticketRepository.countTicketsOfGameIDForUserID(gameID, getUser(principal).getId()));
			model.addAttribute("game", game.get());
			return "ticketsGame";
		}
		myUserService.addTickets(getUser(principal), gameID, ticketRequest.getNumberOfTickets());
		gameRepository.save(game.get());
		ra.addFlashAttribute("numberOfTickets", ticketRequest.getNumberOfTickets());
		return "redirect:/sports/{sportsName}";
	}

	private MyUser getUser(Principal principal) {
		return userRepository.findByUsername(principal.getName());
	}

	private Sport getSportAndAddToModel(String sportsName, Model model) {
		Sport sport = sportRepository.findBySportsName(sportsName);
		if (sport != null) {
			model.addAttribute("sport", sport);
		}
		return sport;
	}
}
