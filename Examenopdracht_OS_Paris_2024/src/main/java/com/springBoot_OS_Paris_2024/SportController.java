package com.springBoot_OS_Paris_2024;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import domain.MyUser;
import domain.Role;
import domain.Sport;
import repository.GameRepository;
import repository.SportRepository;
import repository.TicketRepository;
import repository.UserRepository;

@Controller
@RequestMapping("/sports")
public class SportController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SportRepository sportRepository;
	@Autowired
	private GameRepository gameRepository;
	@Autowired
	private TicketRepository ticketRepository;

	@ModelAttribute("username")
	public String populateUsername(Principal principal) {
		return principal.getName();
	}

	@ModelAttribute("role")
	public Role populateRole(Principal principal) {
		return getUser(principal).getRole();
	}

	@GetMapping
	public String showSports(Model model, Principal principal) {
		model.addAttribute("sportList", sportRepository.findAll());
		model.addAttribute("ticketMap",
				ticketRepository.countTicketsByGameForUserOrderedBySportsNameAndStartDateAsMap(getUser(principal).getId()));
		return "sports";
	}

	@GetMapping(value = "/{sportsName}")
	public String showGamesOfSport(@PathVariable String sportsName, Model model, Principal principal) {
		Sport sport = sportRepository.findBySportsName(sportsName);
		if (sport == null) {
	
			return "redirect:/sports";
		}
		model.addAttribute("sport", sport);
		model.addAttribute("gamesList", gameRepository.findAllBySport(sport));
		model.addAttribute("numberOfTicketsPerGameMap",
				ticketRepository.countTicketsPerGameOfSportIDForUserIDAsMap(sport.getSportID(), getUser(principal).getId()));
		return "gamesSport";
	}

	private MyUser getUser(Principal principal) {
		return userRepository.findByUsername(principal.getName());
	}

}
