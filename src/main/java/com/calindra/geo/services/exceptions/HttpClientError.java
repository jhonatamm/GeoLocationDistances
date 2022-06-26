package com.calindra.geo.services.exceptions;

public class HttpClientError extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public HttpClientError(String msg) {
		super(msg);
	}

}
