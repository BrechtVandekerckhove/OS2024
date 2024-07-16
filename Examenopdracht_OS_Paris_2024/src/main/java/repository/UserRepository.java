package repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import domain.MyUser;

@Component
public interface UserRepository extends CrudRepository<MyUser, Integer> {

	MyUser findByUsername(String name);
	
}

