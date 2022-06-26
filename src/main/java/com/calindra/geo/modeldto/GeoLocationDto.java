package com.calindra.geo.modeldto;

import com.calindra.geo.model.GeoLocationEntity;

import lombok.Data;

@Data
public class GeoLocationDto {
	private String address;
	private Double lat;
	private Double lng;
	
	public GeoLocationDto() {}
	
	public GeoLocationDto(GeoLocationEntity geoLocation) {
		this.address = geoLocation.getAddress();
		this.lat = geoLocation.getLat();
		this.lng = geoLocation.getLng();
	}
}
