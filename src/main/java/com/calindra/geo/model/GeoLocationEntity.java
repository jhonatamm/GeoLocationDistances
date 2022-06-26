package com.calindra.geo.model;

import com.google.gson.JsonElement;

import lombok.Data;

@Data
public class GeoLocationEntity {

	private String address;
	private Double lat;
	private Double lng;
	private JsonElement fullAdressGoogleObject;
}
