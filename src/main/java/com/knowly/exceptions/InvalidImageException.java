package com.knowly.exceptions;

public class InvalidImageException extends RuntimeException{
	public InvalidImageException(String mes) {
		super(mes);
	}
}
