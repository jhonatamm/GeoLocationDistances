package com.calindra.geo.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.calindra.geo.model.GeoDistanceEntity;
import com.calindra.geo.modeldto.GeoDistanceDto;
import com.calindra.geo.modeldto.GeoLocationDto;
import com.calindra.geo.services.IGeoDistances;
import com.calindra.geo.services.IGeoLocations;
import com.calindra.geo.services.IGoogleApiInterface;
import com.calindra.geo.services.exceptions.ResourceNotFoundException;
import com.google.gson.JsonElement;

import lombok.AllArgsConstructor;
@AllArgsConstructor
@Service
public class GeoDistancesServiceImpl implements IGeoDistances{

	private final IGoogleApiInterface googleApiService;
	private final IGeoLocations geolocationService;
	
	@Override
	public List<GeoDistanceDto> getDistanceFromOriginDestinAddress(List<String> origins, List<String> destinations,
			String googleApikey) {
		String originsString = String.join("| ", origins);
		String destinationsString = String.join("| ", destinations);
		JsonElement element = googleApiService.getDistancesFromLocations(originsString, destinationsString, googleApikey);
		List<GeoDistanceEntity> listEntity =  new ArrayList<GeoDistanceEntity>();
		int originIndex = 0;
		for(JsonElement originsObject : element.getAsJsonObject().get("origin_addresses").getAsJsonArray() ) {
			int destinationIndex = 0;
			for(JsonElement destinationsObject : element.getAsJsonObject().get("destination_addresses").getAsJsonArray()) {
				GeoDistanceEntity entity = new  GeoDistanceEntity();
				entity.setOriginAddress(originsObject.getAsString());
				entity.setDestinationAddress(destinationsObject.getAsString());
				entity.setDistanceValue(element.getAsJsonObject()
						.get("rows").getAsJsonArray().get(originIndex).getAsJsonObject()
						.get("elements").getAsJsonArray().get(destinationIndex).getAsJsonObject()
						.get("distance").getAsJsonObject()
						.get("value").getAsDouble());
				entity.setDistanceValueDescription(element.getAsJsonObject()
						.get("rows").getAsJsonArray().get(originIndex).getAsJsonObject()
						.get("elements").getAsJsonArray().get(destinationIndex).getAsJsonObject()
						.get("distance").getAsJsonObject()
						.get("text").getAsString());
				entity.setDistanceTimeSeconds(element.getAsJsonObject()
						.get("rows").getAsJsonArray().get(originIndex).getAsJsonObject()
						.get("elements").getAsJsonArray().get(destinationIndex).getAsJsonObject()
						.get("duration").getAsJsonObject()
						.get("value").getAsDouble());
				entity.setDistanceTimeDescription(element.getAsJsonObject()
						.get("rows").getAsJsonArray().get(originIndex).getAsJsonObject()
						.get("elements").getAsJsonArray().get(destinationIndex).getAsJsonObject()
						.get("duration").getAsJsonObject()
						.get("text").getAsString());
				destinationIndex++;
				listEntity.add(entity);
			}
			originIndex++;
		}

		
		
		return dtoListWithGeoPoint(listEntity,googleApikey);
	}
	
	@Override
	public List<GeoDistanceDto> getMaxDistancesAndMin(List<String> origins, List<String> destinations,
			String googleApikey){
		List<GeoDistanceDto> listAddress = getDistanceFromOriginDestinAddress(origins, destinations, googleApikey);
		GeoDistanceDto dtoLongest = listAddress.stream().filter(address -> address.getDistanceValue() > 0).sorted(Comparator.comparing(GeoDistanceDto::getDistanceValue).reversed()).findFirst().orElseThrow(() -> new ResourceNotFoundException("Falha ao realizar filtro da maior distancia"));
		GeoDistanceDto dtoClosest = listAddress.stream().filter(address -> address.getDistanceValue() > 0).sorted(Comparator.comparing(GeoDistanceDto::getDistanceValue)).findFirst().orElseThrow(() -> new ResourceNotFoundException("Falha ao realizar filtro da menor distancia"));
		List<GeoDistanceDto> listMaxMinDistance = Arrays.asList(dtoClosest, dtoLongest);
		return listMaxMinDistance;
	}
	
	private List<GeoDistanceDto> dtoListWithGeoPoint(List<GeoDistanceEntity> listEntity, String googleApikey ){
		 List<String> addressToGetGeoPoint = new ArrayList<String>();
		 for(GeoDistanceEntity entity : listEntity) {
			 addressToGetGeoPoint.addAll(Arrays.asList(entity.getDestinationAddress(), entity.getOriginAddress()));
		 }

		addressToGetGeoPoint = addressToGetGeoPoint.stream().distinct().collect(Collectors.toList());

		String address = String.join("; ", addressToGetGeoPoint);
		List<GeoLocationDto> geoLocationPointList = geolocationService.getLocationByAdress(address, googleApikey);
		List<GeoDistanceDto> listDistanceDto = listEntity.stream().map(e -> new GeoDistanceDto(e)).collect(Collectors.toList());
		for(GeoLocationDto gp :geoLocationPointList ) {
			listDistanceDto = listDistanceDto.stream().map( entity -> {
				if(entity.getDestinationAddress().replace(" -", ",").replaceAll("[A-Z]{2}, ", "").equalsIgnoreCase(gp.getAddress().replace(" -", ",").replaceAll("[A-Z]{2}, ", ""))) {
					entity.setDestinationLocation(gp);
				}
				if(entity.getOriginAddress().replace(" -", ",").replaceAll("[A-Z]{2}, ", "").equalsIgnoreCase(gp.getAddress().replace(" -", ",").replaceAll("[A-Z]{2}, ", ""))) {
					entity.setOriginLocation(gp);
				}
				return entity;
			}).collect(Collectors.toList());
		}
		return listDistanceDto;
	}
}
