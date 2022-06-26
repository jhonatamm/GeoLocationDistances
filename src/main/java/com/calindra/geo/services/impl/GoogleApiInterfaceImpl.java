package com.calindra.geo.services.impl;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.calindra.geo.constant.GeoLocationConstants;
import com.calindra.geo.services.IGoogleApiInterface;
import com.calindra.geo.services.exceptions.HttpClientError;
import com.calindra.geo.services.exceptions.ResourceNotFoundException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class GoogleApiInterfaceImpl implements IGoogleApiInterface{

	private static RestTemplate restTemplate = getInstance();
	
	private String lang = "pt-BR";
	
	private String units = "metric";
	
	private static Gson gson = new Gson();

	@Override
	public JsonElement getLocationFromGoogle(String location, String googleApikey) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(GeoLocationConstants.GEOLOCATION_CODE_ENDPOINT)
		        .queryParam(checkIfIsAddress(location), location)
		        .queryParam("language", lang)
				.queryParam("key", googleApikey);
		
		
		log.info("URI to send : {}", builder.toUriString());
		
		JsonElement element = callGoogleForInformation(builder);
		return element;
	}
	
	@Override
	public JsonElement getDistancesFromLocations(String origins, String destinations, String googleApikey) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(GeoLocationConstants.GEOMATRIX_DISTANCE_ENDPOINT)
		        .queryParam("origins", origins)
		        .queryParam("destinations", destinations)
		        .queryParam("language", lang)
		        .queryParam("units", units)
				.queryParam("key", googleApikey);
		
		
		log.info("URI to send : {}", builder.buildAndExpand());
		
		JsonElement element = callGoogleForInformation(builder);
		return element;
	}
	
	
	private JsonElement callGoogleForInformation(UriComponentsBuilder builder) {
		JsonElement element = null;
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> request = new HttpEntity<>(headers);
		try {
			ResponseEntity<String> response = restTemplate.exchange(builder.buildAndExpand().toString(), HttpMethod.GET,request , String.class);
			if(response.getStatusCode().is2xxSuccessful()) {
				element = gson.fromJson(response.getBody(), JsonElement.class);
				log.info("Body Return: {}", element);
				if(element.getAsJsonObject().get("status").getAsString().equals("REQUEST_DENIED")) {
					throw new IllegalArgumentException("Api key invalida");
				} else if(!isValidElement(element)) {
					throw new IllegalArgumentException("Não foram encontrados resultados para esta consulta");
				}
			} else {
				throw new ResourceNotFoundException("Verifique os argumentos enviados");
			}
		} catch (HttpClientErrorException e) {
			log.error( "erro on request {}", e.getMessage() );
			throw new HttpClientError("Erro na requisição");
		}
		return element;
	}
	
	private String checkIfIsAddress(String Location) {
	    Pattern pattern = Pattern.compile("[a-z]", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(Location);
	    boolean matchFound = matcher.find();
	    if(matchFound) {
	    	return "address";
	    } else {
	    	return "latlng";
	    }
	}
	
	private Boolean isValidElement(JsonElement element) {
		log.debug("is Valid element init");
		try {
			JsonObject objectReturn = element.getAsJsonObject();
			if(objectReturn.has("results") && objectReturn.get("results").getAsJsonArray().isEmpty()) {
				return false;
			}
			else if(objectReturn.has("rows") && objectReturn.get("rows").getAsJsonArray().isEmpty() ){				
				return false;
			} else if( objectReturn.has("rows") && objectReturn.get("rows").getAsJsonArray().size() > 0) {
				for(JsonElement el : objectReturn.get("rows").getAsJsonArray()) {
					if(el.getAsJsonObject().get("elements").getAsJsonArray().get(0).getAsJsonObject().get("status").getAsString().equals("ZERO_RESULTS") 
							|| el.getAsJsonObject().get("elements").getAsJsonArray().get(0).getAsJsonObject().get("status").getAsString().equals("NOT_FOUND") ) {
						return false;
					}
				}
			}
		} catch (Exception e) {
			log.error("Erro on evaluate:", e);
			return false;
		}
		log.debug("is Valid element end");
		return true;
	}
	
    private static RestTemplate getInstance() {
        if (restTemplate == null) {
        	restTemplate = new RestTemplate();
        	restTemplate.getMessageConverters()
            .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        }
        return restTemplate;
    }

}
