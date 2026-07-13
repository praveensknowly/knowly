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
			return new UserPrinciple(u.get());
		}
		throw new UsernameNotFoundException("Email or Number is not regitered yet");
	}

}
