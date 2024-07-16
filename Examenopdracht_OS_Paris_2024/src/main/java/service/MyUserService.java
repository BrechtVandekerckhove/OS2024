package service;

import org.springframework.transaction.annotation.Transactional;

import domain.MyUser;

public interface MyUserService {
	@Transactional
	public void addTickets(MyUser myUser, int gameId, int number);

}
