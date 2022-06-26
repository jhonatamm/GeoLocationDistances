package com.calindra.geo.filters;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RequestWrapper extends HttpServletRequestWrapper {
	 
    
    private String apikey;
    
    private HttpServletRequest request;

    
    public RequestWrapper(HttpServletRequest request, String apikey) throws IOException {
        super(request);
        this.request = request;
        this.apikey = apikey;
    }
 
    
    @Override
    public String[] getParameterValues(String name) {
    	if(name != null && name.equalsIgnoreCase("apikey")) {
    		String [] apikey = {this.apikey};
    		return apikey;
    	} else {
    		return this.request.getParameterValues(name);
    	}
    }


}
