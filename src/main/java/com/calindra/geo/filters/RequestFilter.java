package com.calindra.geo.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(1)
public class RequestFilter implements Filter {
	
	@Value("${google-env-api-key}")
	private String googleEnvApiKey;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		try {
			log.info("Logging  Filter Request  {} : {}", req.getMethod(), req.getRequestURI());
			String apikey = req.getParameter("apikey");
			if(apikey != null) {
				log.info("Executing with user apikey");
				chain.doFilter(request, response);
			} else if(googleEnvApiKey != null &&  !googleEnvApiKey.isBlank()) {
				log.info("Executing with env apikey");
				RequestWrapper wrapper = new RequestWrapper((HttpServletRequest) request , googleEnvApiKey );
				chain.doFilter((HttpServletRequest) wrapper, response);
			} else {
				log.warn("Executing without apikey");
				chain.doFilter(request, response);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
