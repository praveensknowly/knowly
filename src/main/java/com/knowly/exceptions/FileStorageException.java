package com.knowly.exceptions;

public class FileStorageException extends RuntimeException{
	public FileStorageException(String mes) {
		super(mes);
	}
	public FileStorageException(String mes,Throwable c) {
		super(mes,c);
	}
	
}
