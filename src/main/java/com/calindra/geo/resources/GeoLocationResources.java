package com.calindra.geo.resources;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.calindra.geo.modeldto.GeoLocationDto;
import com.calindra.geo.services.IGeoLocations;
import com.calindra.geo.util.Utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/api/geolocation")
public class GeoLocationResources {
	
	private final IGeoLocations geolocationService;
	
	@GetMapping("/address")
	public ResponseEntity<List<GeoLocationDto>> getGeoLocationByAddress(@RequestParam(name = "address", required = true, defaultValue = "") String address, @RequestParam(name = "apikey", required = true, defaultValue = "") String apikey){
		log.info("Address is {}",address );
		if(Utils.checkIfStringIsNullOrEmpty(address)) {
			throw new IllegalArgumentException("O parametro address não pode ser vazio");
		}
		List<GeoLocationDto> dtoList = geolocationService.getLocationByAdress(address, apikey);
		
		return ResponseEntity.ok().body(dtoList);
	}
	
	@GetMapping("/geopoint")
	public ResponseEntity<List<GeoLocationDto>> getGeoLocationByGeoPoint(@RequestParam(name = "latitude", required = true, defaultValue = "") String latitude , @RequestParam(name = "longitude", required = true, defaultValue = "") String longitude, @RequestParam(name = "apikey", required = true, defaultValue = "") String apikey){
		log.info("Latitude is {}",longitude );
		if(Utils.checkIfStringIsNullOrEmpty(latitude)) {
			throw new IllegalArgumentException("O parametro latitude não pode ser vazio");
		}
		if(Utils.checkIfStringIsNullOrEmpty(longitude)) {
			throw new IllegalArgumentException("O parametro longitude não pode ser vazio");
		}
		GeoLocationDto unProcessedDto = new GeoLocationDto();
		unProcessedDto.setLat(Double.valueOf(latitude));
		unProcessedDto.setLng(Double.valueOf(longitude));
		List<GeoLocationDto> dtoList = geolocationService.getAdressByGeoLocation(unProcessedDto, apikey);
		
		return ResponseEntity.ok().body(dtoList);
	}
	
}
