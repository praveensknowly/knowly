package com.knowly.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.knowly.entity.User;
import com.knowly.model.UserPrinciple;
import com.knowly.repository.UserRepository;
@Service 
public class MyUserDetailsService implements UserDetailsService{
	
	private final UserRepository repo;

	public MyUserDetailsService(UserRepository repo) {
		this.repo = repo;
	}
	@Override
	public UserDetails loadUserByUsername(String nameOrNumber) throws UsernameNotFoundException {
		Optional<User> u=repo.findByEmail(nameOrNumber);
		if(u.isEmpty()) {
			u=repo.findByNumber(nameOrNumber);
		}
		if(u.isPresent()) {
			User user = u.get();
			if (user.getPassword() == null) {
				throw new UsernameNotFoundException("This account uses Google/GitHub sign-in. Please log in that way, or set a password in Settings first.");
			}
			return new UserPrinciple(user);
		}
		throw new UsernameNotFoundException("Email or Number is not regitered yet");
	}

}
