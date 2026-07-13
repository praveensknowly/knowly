package com.knowly.exceptionhandling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

import com.knowly.exceptions.FileStorageException;
import com.knowly.exceptions.InvalidImageException;
import com.knowly.exceptions.UserAlreadyExistException;
import com.knowly.exceptions.UserNotFoundException;

@ControllerAdvice
public class ExceptionHandling {
	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandling.class);
	 @ExceptionHandler(UserAlreadyExistException.class)
	    @ResponseStatus(HttpStatus.CONFLICT)
	    public String handleUserAlreadyExists(UserAlreadyExistException e,Model model) {
	        model.addAttribute("error", e.getMessage());
	        return "SignUp";
	    }

	    @ExceptionHandler(UserNotFoundException.class)
	    @ResponseStatus(HttpStatus.NOT_FOUND)
		public String handleUserNotFound(UserNotFoundException e,Model model) {
			model.addAttribute("error", e.getMessage());
			return "error";
		}

	    @ExceptionHandler(InvalidImageException.class)
	    @ResponseStatus(HttpStatus.BAD_REQUEST)
	    public String handleInvalidImage(InvalidImageException e, Model model) {
	        logger.error("Invalid image exception", e);
	        model.addAttribute("message", e.getMessage());
	        return "error";
	    }

	    @ExceptionHandler(FileStorageException.class)
	    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	    public String handleFileStorage(FileStorageException e,Model model) {
	        logger.error("File storage exception", e);
	        model.addAttribute("message", "Unable to upload your profile picture. Please try again.");
	        return "error";
	    }

	    @ExceptionHandler(IllegalArgumentException.class)
	    @ResponseStatus(HttpStatus.BAD_REQUEST)
	    public String handleIllegalArgument(IllegalArgumentException e,Model model) {
	        model.addAttribute("message", e.getMessage());
			return "error";
	    }

	    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDataIntegrityViolation(DataIntegrityViolationException e, Model model) {
        logger.error("Data integrity violation", e);
        model.addAttribute("error", "This email or phone number is already registered. Please try signing in or use a different one.");
        return "SignUp";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUnexpected(Exception e, Model model) {
        logger.error("Unexpected error occurred", e);
        model.addAttribute("message", "Something went wrong. Please try again.");
        return "error";
    }
	
}
