package com.springBoot_OS_Paris_2024;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import domain.Game;
import domain.MyUser;
import domain.Role;
import domain.Sport;
import jakarta.validation.Valid;
import repository.DisciplineRepository;
import repository.GameRepository;
import repository.SportRepository;
import repository.UserRepository;
import service.GameService;
import validator.GameValidation;

@Controller
@RequestMapping("/sports/{sportsName}/newGame")
public class NewGameController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SportRepository sportRepository;
	@Autowired
	private GameRepository gameRepository;
	@Autowired
	private DisciplineRepository disciplineRepository;
	@Autowired
	private GameValidation gameValidation;
	@Autowired
	private GameService gameService;

	@ModelAttribute("username")
	public String populateUsername(Principal principal) {
		return getUser(principal).getName();
	}

	@ModelAttribute("role")
	public Role populateRole(Principal principal) {
		return getUser(principal).getRole();
	}


	@GetMapping
	public String showNewGameScreen(@PathVariable String sportsName, Model model, Principal principal) {
		Sport sport = sportRepository.findBySportsName(sportsName);
		if (sport == null) {
			return "redirect:/sports";
		}
		model.addAttribute("sport", sport);
		model.addAttribute("listStadiums", gameRepository.getAllStadiums());
		model.addAttribute("listDisciplines", disciplineRepository.getAllDisciplinesOfSport(sport.getSportID()));
		model.addAttribute("game", new Game());
		return "newGame";
	}

	@PostMapping
	public String onSubmitNewGame(@Valid Game game, BindingResult result, Model model, @PathVariable String sportsName,
			@RequestParam int discipline1id, @RequestParam int discipline2id) {

		gameService.addDisciplines(game, discipline1id, discipline2id);
		gameValidation.validate(game, result);

		Sport sport = sportRepository.findBySportsName(sportsName);
		if (sport == null) {
			return "redirect:/sports";
		}
		

		if (result.hasErrors()) {
			model.addAttribute("sport", sport);
			model.addAttribute("listStadiums", gameRepository.getAllStadiums());
			model.addAttribute("listDisciplines", disciplineRepository.getAllDisciplinesOfSport(sport.getSportID()));
			return "newGame";
		}
		game.setSport(sport);
		game.addSeats();
		gameRepository.save(game);

		return "redirect:/sports/" + sportsName;
	}

	private MyUser getUser(Principal principal) {
		return userRepository.findByUsername(principal.getName());
	}
}
