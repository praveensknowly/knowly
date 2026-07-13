package com.knowly.exceptions;

public class UserNotFoundException extends RuntimeException{
	public UserNotFoundException(String mes) {
		super(mes);
	}
}
