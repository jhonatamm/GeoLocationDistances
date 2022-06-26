package com.calindra.geo.services;

import com.google.gson.JsonElement;

public interface IGoogleApiInterface {
	
	JsonElement getLocationFromGoogle(String location,String googleApikey);

	JsonElement getDistancesFromLocations(String origin, String destiny,String googleApikey );
}
