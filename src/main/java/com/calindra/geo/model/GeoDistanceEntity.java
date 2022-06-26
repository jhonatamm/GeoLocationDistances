package com.calindra.geo.model;

import lombok.Data;

@Data
public class GeoDistanceEntity {

	private String originAddress;
	private GeoLocationEntity originLocation;
	private String destinationAddress;
	private GeoLocationEntity destinationLocation;
	private Double distanceValue;
	private Double distanceTimeSeconds;
	private String distanceValueDescription;
	private String distanceTimeDescription;

}
