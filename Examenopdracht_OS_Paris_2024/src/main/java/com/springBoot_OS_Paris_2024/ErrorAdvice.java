package com.springBoot_OS_Paris_2024;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import exceptions.GamesNotFoundException;
import exceptions.NoFreeSeatsException;
import exceptions.SportNotFoundException;

@RestControllerAdvice
class ErrorAdvice {

	@ResponseBody
	@ExceptionHandler(SportNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String sportNotFoundHandler(SportNotFoundException ex) {
		return ex.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(GamesNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String gamesNotFoundHandler(GamesNotFoundException ex) {
		return ex.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(NoFreeSeatsException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String noFreeSeatsHandler(NoFreeSeatsException ex) {
		return ex.getMessage();
	}
}