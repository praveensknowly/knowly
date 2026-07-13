package com.knowly.mapper;

import com.knowly.dto.SignupDto;
import com.knowly.dto.UserDto;
import com.knowly.entity.User;

public class UserMapper { 
	public static UserDto toDto(User user) {
	UserDto us=new UserDto();
	us.setName(user.getName());
	us.setEmail(user.getEmail());
	return us;
	}
	public static User toUser(SignupDto dto) {
		User us=new User();
		us.setName(dto.getName());
		us.setEmail(dto.getEmail());
		us.setNumber(dto.getNumber());
		us.setPassword(dto.getPassword());
		return us;
		}
}
