package service;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import domain.MyUser;
import domain.Role;
import lombok.NoArgsConstructor;
import repository.UserRepository;

@Service
@NoArgsConstructor
//interface van spring gebruiken om in te loggen
public class MyUserDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;

	  @Override
	  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    MyUser user = userRepository.findByUsername(username);
	    if (user == null) {
	      throw new UsernameNotFoundException(username);
	    }
	    return new User(user.getUsername(), user.getPassword(), convertAuthorities(user.getRole()));
	  }
	  
	  private Collection<? extends GrantedAuthority> convertAuthorities(Role role) {
		    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toString())); //ROLE_ moet ervoor
	  }
	  
	  //if MyUser has multiple roles
	  /*private Collection<? extends GrantedAuthority> convertAuthorities(Set<Role> roles) {
		    return roles.stream()
		            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toString()))
		            .collect(Collectors.toList());
	  }*/
}
