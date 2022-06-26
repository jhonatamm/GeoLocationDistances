package com.calindra.geo.services;

import java.util.List;

import com.calindra.geo.modeldto.GeoDistanceDto;

public interface IGeoDistances{
	
	List<GeoDistanceDto> getDistanceFromOriginDestinAddress(List<String> origins, List<String> destinations, String googleApikey);

	List<GeoDistanceDto> getMaxDistancesAndMin(List<String> origins, List<String> destinations, String googleApikey);

}
