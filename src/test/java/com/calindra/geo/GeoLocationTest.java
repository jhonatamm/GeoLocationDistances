package com.calindra.geo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.calindra.geo.modeldto.GeoLocationDto;
import com.calindra.geo.resources.GeoLocationResources;
import com.calindra.geo.services.IGeoLocations;
import com.calindra.geo.services.IGoogleApiInterface;
import com.calindra.geo.services.impl.GeoLocationServiceImpl;
import com.calindra.geo.services.impl.GoogleApiInterfaceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
@ContextConfiguration(classes = {GeoLocationServiceImpl.class, GeoLocationResources.class, GoogleApiInterfaceImpl.class})
@WebMvcTest
public class GeoLocationTest {
		private  static final String JSON_LOCATION ="data/locationJson.json";
		private  static final String JSON_GEO_POINT ="data/geoPointJson.json";
	   
		@Autowired
	    private MockMvc mockMvc;
	   

	    @MockBean
		private IGeoLocations service;
		
		@MockBean
		private IGoogleApiInterface googleApiService;

		
		@Test
		public void testResourceAddress() throws Exception {
			log.info("## inicio teste 01");
	    	GeoLocationDto dt1 = new GeoLocationDto();
	    	dt1.setAddress("rua aparecida");
	    	List<GeoLocationDto> list = new ArrayList<>();
	    	list.add(dt1);
	    	
			when(service.getLocationByAdress("AV. Rio Branco, 1 centro, Rio de Janeiro,20090003", "XPTO")).thenReturn(list);
			
			this.mockMvc.perform(get("/api/geolocation/address").param("address","AV. Rio Branco, 1 centro, Rio de Janeiro,20090003" ).param("apikey", "XPTO")).andDo(print()).andExpect(status().isOk());
			log.info("## final teste 01");
		}

		@Test
		public void testResourceAddressIntegrated() throws Exception {
			log.info("## inicio teste 02");
	    	Gson g = new Gson();
	    	JsonElement element = g.fromJson(readFile(JSON_LOCATION), JsonElement.class);
	    	
	    	GeoLocationServiceImpl serviceLocal = new GeoLocationServiceImpl(googleApiService);
	    	
	    	doReturn(element).when(googleApiService).getLocationFromGoogle("AV. Rio Branco, 1 centro, Rio de Janeiro,20090003", "XPTO");
	    	
	    	List<GeoLocationDto> list =  serviceLocal.getLocationByAdress("AV. Rio Branco, 1 centro, Rio de Janeiro,20090003", "XPTO");

	    	when(service.getLocationByAdress("AV. Rio Branco, 1 centro, Rio de Janeiro,20090003", "XPTO")).thenReturn(list);
			
	    	MockHttpServletResponse response = this.mockMvc.perform(get("/api/geolocation/address").param("address","AV. Rio Branco, 1 centro, Rio de Janeiro,20090003" ).param("apikey", "XPTO")).andDo(print()).andExpect(status().isOk()).andReturn().getResponse();
	    	
	    	assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
	    	JsonElement returnElement = g.fromJson(response.getContentAsString(), JsonElement.class);
	    	assertThat(returnElement.getAsJsonArray().size() > 0);
	    	log.info("## final test 02");
		}
		
		
		@Test
		public void testResourceGeoPoint() throws Exception {
			log.info("## inicio teste 03");
	    	GeoLocationDto dt1 = new GeoLocationDto();
	    	dt1.setAddress("R. Silva Jardim, 32 - Centro, Rio de Janeiro - RJ, 20050-060, Brasil");
	    	dt1.setLat(-22.9070828);
	    	dt1.setLng(-43.18191480000001);
	    	List<GeoLocationDto> list = new ArrayList<>();
	    	list.add(dt1);
	    	
	    	GeoLocationDto dto = new GeoLocationDto();
	    	dto.setLat(-22.9070828);
	    	dto.setLng(-43.18191480000001);
			when(service.getAdressByGeoLocation(dto, "XPTO")).thenReturn(list);
			
			this.mockMvc.perform(get("/api/geolocation/geopoint").param("latitude","-22.9070828" ).param("longitude", "-43.18191480000001").param("apikey", "XPTO")).andDo(print()).andExpect(status().isOk());
			log.info("## final teste 03");
		}
		
		@Test
		public void testResourceGeoPointIntegrated() throws Exception {
			log.info("## inicio teste 04");
	    	Gson g = new Gson();
	    	JsonElement element = g.fromJson(readFile(JSON_GEO_POINT), JsonElement.class);
	    	
	    	GeoLocationServiceImpl serviceLocal = new GeoLocationServiceImpl(googleApiService);
	    	
	    	doReturn(element).when(googleApiService).getLocationFromGoogle("-22.9070828,-43.18191480000001", "XPTO");
	    	
	    	List<GeoLocationDto> list =  serviceLocal.getLocationByAdress("-22.9070828,-43.18191480000001", "XPTO");

	    	GeoLocationDto dto = new GeoLocationDto();
	    	dto.setLat(-22.9070828);
	    	dto.setLng(-43.18191480000001);
	    	
	    	when(service.getAdressByGeoLocation(dto, "XPTO")).thenReturn(list);
			
	    	MockHttpServletResponse response = this.mockMvc.perform(get("/api/geolocation/geopoint").param("latitude","-22.9070828" ).param("longitude", "-43.18191480000001").param("apikey", "XPTO")).andDo(print()).andExpect(status().isOk()).andReturn().getResponse();
	    	
	    	assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
	    	JsonElement returnElement = g.fromJson(response.getContentAsString(), JsonElement.class);
	    	assertThat(returnElement.getAsJsonArray().size() > 3);
	    	log.info("## final teste 04");
		}
		
		
		private static String readFile(String path) {
			String content = "";
			try {
				Path filePath = Path.of(	new ClassPathResource(
						path, 
						GeoLocationTest.class.getClassLoader()).getFile().getAbsolutePath());
				content = Files.readString(filePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return content;
		}
	   

}
