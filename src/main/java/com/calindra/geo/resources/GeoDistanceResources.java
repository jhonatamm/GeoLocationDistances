package com.calindra.geo.resources;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.calindra.geo.modeldto.GeoDistanceDto;
import com.calindra.geo.services.IGeoDistances;
import com.calindra.geo.util.Utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/api/distances")
public class GeoDistanceResources {
	
	private final IGeoDistances geoDistanceService;
	
	
	
	@GetMapping()
	public ResponseEntity<List<GeoDistanceDto>> getGeoDistancesByAddress(@RequestParam(name = "origins", required = true, defaultValue = "") String origins,
			@RequestParam(name = "destinations", required = true, defaultValue = "") String destinations, 
			@RequestParam(name = "apikey", required = true, defaultValue = "") String apikey){
		log.info("Locations is {}",origins );
		if(Utils.checkIfStringIsNullOrEmpty(origins)) {
			throw new IllegalArgumentException("O parametro origins não pode ser vazio");
		}
		if(Utils.checkIfStringIsNullOrEmpty(destinations)) {
			throw new IllegalArgumentException("O parametro destinations não pode ser vazio");
		}
		List<String> addressOrigins = Arrays.asList(origins.split(";"));
		List<String> addressDestinations = Arrays.asList(destinations.split(";"));
		List<GeoDistanceDto> dtoList = geoDistanceService.getDistanceFromOriginDestinAddress(addressOrigins, addressDestinations, apikey);
		
		return ResponseEntity.ok().body(dtoList);
	}
	
	@GetMapping("/maxmin")
	public ResponseEntity<List<GeoDistanceDto>> getMaxMinByAddress(@RequestParam(name = "origins", required = true, defaultValue = "") String origins,
			@RequestParam(name = "destinations", required = true, defaultValue = "") String destinations, 
			@RequestParam(name = "apikey", required = true, defaultValue = "") String apikey){
		log.info("Locations is {}",origins );
		if(Utils.checkIfStringIsNullOrEmpty(origins)) {
			throw new IllegalArgumentException("O parametro origins não pode ser vazio");
		}
		if(Utils.checkIfStringIsNullOrEmpty(destinations)) {
			throw new IllegalArgumentException("O parametro destinations não pode ser vazio");
		}
		List<String> addressOrigins = Arrays.asList(origins.split(";"));
		List<String> addressDestinations = Arrays.asList(destinations.split(";"));
		List<GeoDistanceDto> dtoList = geoDistanceService.getMaxDistancesAndMin(addressOrigins, addressDestinations, apikey);
		
		return ResponseEntity.ok().body(dtoList);
	}
	
	@GetMapping("/matrix")
	public ResponseEntity<List<GeoDistanceDto>> getMatrixByAddress(@RequestParam(name = "addressList", required = true, defaultValue = "") String origins, 
			@RequestParam(name = "apikey", required = true, defaultValue = "") String apikey){
		log.info("Locations is {}",origins );
		if(Utils.checkIfStringIsNullOrEmpty(origins)) {
			throw new IllegalArgumentException("O parametro origins não pode ser vazio");
		}
		List<String> addressOrigins = Arrays.asList(origins.split(";"));
		if(addressOrigins.size() <= 1) {
			throw new IllegalArgumentException("Devem ser enviados dois endereços ou mais separados por ;");
		}
		List<GeoDistanceDto> dtoList = geoDistanceService.getMaxDistancesAndMin(addressOrigins, addressOrigins, apikey);
		
		return ResponseEntity.ok().body(dtoList);
	}
	

}
