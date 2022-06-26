package com.calindra.geo.modeldto;

import com.calindra.geo.model.GeoDistanceEntity;

import lombok.Data;

@Data
public class GeoDistanceDto {
	private String originAddress;
	private GeoLocationDto originLocation;
	private String destinationAddress;
	private GeoLocationDto destinationLocation;
	private Double distanceValue;
	private Double distanceTimeSeconds;
	private String distanceValueDescription;
	private String distanceTimeDescription;
	
	public GeoDistanceDto() {
		
	}
	public GeoDistanceDto(GeoDistanceEntity entity) {
		this.originAddress = entity.getOriginAddress();
		this.destinationAddress = entity.getDestinationAddress();
		this.distanceValue = entity.getDistanceValue();
		this.distanceValueDescription = entity.getDistanceValueDescription();
		this.distanceTimeSeconds = entity.getDistanceTimeSeconds();
		this.distanceTimeDescription = entity.getDistanceTimeDescription();
	}
}
