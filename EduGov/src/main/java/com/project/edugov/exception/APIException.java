package com.project.edugov.exception;

import org.springframework.http.HttpStatus;

public class APIException extends RuntimeException {
	private HttpStatus status;
	private String message;

	public APIException(String message) {
		super(message);
		this.message = message;
	}

	public APIException(HttpStatus status, String message) {
		super(message);
		this.status = status;
		this.message = message;
	}

	public HttpStatus getStatus() {
		return status;
	}

	@Override
	public String getMessage() {
		return message;
	}
}