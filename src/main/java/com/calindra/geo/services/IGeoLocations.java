package com.calindra.geo.services;

import java.util.List;

import com.calindra.geo.modeldto.GeoLocationDto;

public interface IGeoLocations{
	
	List<GeoLocationDto> getLocationByAdress(String address, String googleApikey);
	
	List<GeoLocationDto> getAdressByGeoLocation(GeoLocationDto entity,String googleApikey);
}
