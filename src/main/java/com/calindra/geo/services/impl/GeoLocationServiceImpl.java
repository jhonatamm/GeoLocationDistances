package com.calindra.geo.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.calindra.geo.model.GeoLocationEntity;
import com.calindra.geo.modeldto.GeoLocationDto;
import com.calindra.geo.services.IGeoLocations;
import com.calindra.geo.services.IGoogleApiInterface;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class GeoLocationServiceImpl implements IGeoLocations {

	private final IGoogleApiInterface googleApiService;
	
	
	@Override
	public List<GeoLocationDto> getLocationByAdress(String address, String googleApikey) {
		JsonElement element = googleApiService.getLocationFromGoogle(address, googleApikey);		
		return jsonToGeoEntityList(element).stream().map(GeoLocationDto::new).collect(Collectors.toList());
	}

	@Override
	public List<GeoLocationDto> getAdressByGeoLocation(GeoLocationDto entity, String googleApikey) {
		String geoPointLocation = ""+entity.getLat()+","+entity.getLng();
		JsonElement element = googleApiService.getLocationFromGoogle(geoPointLocation, googleApikey);
		return jsonToGeoEntityList(element).stream().map(GeoLocationDto::new).collect(Collectors.toList());
	}
	
	private List<GeoLocationEntity> jsonToGeoEntityList(JsonElement element) {
		List<GeoLocationEntity> list = new ArrayList<>();
		 
		JsonArray jsonRespArr = element.getAsJsonObject().get("results").getAsJsonArray();
		for(JsonElement el :  jsonRespArr) {
			GeoLocationEntity geoLocation = new GeoLocationEntity();
			geoLocation.setAddress(el.getAsJsonObject().get("formatted_address").getAsString());
			geoLocation.setFullAdressGoogleObject(el);
			JsonObject location = el.getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject();
			geoLocation.setLat(location.get("lat").getAsDouble());
			geoLocation.setLng(location.get("lng").getAsDouble());
			list.add(geoLocation);
		}
		return list;
	}

}
