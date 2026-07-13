package com.knowly.exceptions;

public class UserAlreadyExistException extends RuntimeException{
	public UserAlreadyExistException(String mes) {
		super(mes);
	}
}
