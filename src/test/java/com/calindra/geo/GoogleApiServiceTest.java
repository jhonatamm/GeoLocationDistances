package com.calindra.geo;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.calindra.geo.services.impl.GoogleApiInterfaceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class GoogleApiServiceTest {


	
	@Test
	public void testGetGeoDistancesByAddress_InvalidApiKey() throws Exception {
		log.info("## inicio test 01");
		GoogleApiInterfaceImpl googleApiService = new GoogleApiInterfaceImpl();
		Exception exception = assertThrows(RuntimeException.class, () -> {
			googleApiService.getDistancesFromLocations("Bom jesus do Itabapoana", "Campos dos goytacazes", "XPTO");
	    });

		String expectedMessage = "Api key invalida";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	public void testGetGeoLocationsByAddress_InvalidApiKey() throws Exception {
		log.info("## inicio test 02");
		GoogleApiInterfaceImpl googleApiService = new GoogleApiInterfaceImpl();
		Exception exception = assertThrows(RuntimeException.class, () -> {
			googleApiService.getLocationFromGoogle("Bom jesus do Itabapoana",  "XPTO");
	    });

		String expectedMessage = "Api key invalida";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	public void testGetGeoLocationsByAddress() throws Exception {
		log.info("## inicio test 03");
		GoogleApiInterfaceImpl googleApiService = new GoogleApiInterfaceImpl();
		Exception exception = assertThrows(Exception.class, () -> {
			googleApiService.getLocationFromGoogle("",  "XPTO");
	    });

		assertTrue(!exception.getMessage().isBlank());
	}
	
	
}
