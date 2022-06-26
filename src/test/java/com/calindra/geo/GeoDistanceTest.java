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
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import com.calindra.geo.modeldto.GeoDistanceDto;
import com.calindra.geo.services.IGeoDistances;
import com.calindra.geo.services.IGeoLocations;
import com.calindra.geo.services.IGoogleApiInterface;
import com.calindra.geo.services.impl.GeoDistancesServiceImpl;
import com.calindra.geo.services.impl.GeoLocationServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
@WebMvcTest
public class GeoDistanceTest {
		private  static final String JSON_DISTANCE ="data/distancesJson.json";
		private  static final String JSON_GEO_POINT_DISTANCE ="data/distancesGeoPointJson.json";
		
		private  static final String JSON_MATRIX_DISTANCE ="data/distancesJsonMatrixTest.json";
		private  static final String JSON_GEO_POINT_MATRIX_DISTANCE ="data/distancesGeoMatrixTest.json";
	   
		@Autowired
	    private MockMvc mockMvc;
	   

	    @MockBean
		private IGeoLocations service;
		
		@MockBean
		private IGoogleApiInterface googleApiService;
		
	    @MockBean
		private IGeoDistances geoDistanceService;

		
		@Test
		public void testGetGeoDistancesByAddress() throws Exception {
			log.info("## inicio teste 01");
			String origins = "Belo horizonte; Salvador;  Rio de janeiro;Campos dos Goytacazes; AV. Rio Branco, 1 Centro, Rio de janeiro RJ, 20090003";
			String destinations ="Belo horizonte; Campinas";
			List<String> addressOrigins = Arrays.asList(origins.split(";"));
			List<String> addressDestinations = Arrays.asList(destinations.split(";"));
			GeoDistanceDto dto = new GeoDistanceDto();
			dto.setOriginAddress("Belo horizonte");
			dto.setDestinationAddress(" Campinas");
			dto.setDistanceValue(155.9);
			List<GeoDistanceDto> list = new ArrayList<>();
			list.add(dto);
			when(geoDistanceService.getDistanceFromOriginDestinAddress(addressOrigins, addressDestinations, "XPTO")).thenReturn(list);
			
			this.mockMvc.perform(get("/api/distances").param("origins","Belo horizonte; Salvador;  Rio de janeiro;Campos dos Goytacazes; AV. Rio Branco, 1 Centro, Rio de janeiro RJ, 20090003" ).param("destinations","Belo horizonte; Campinas").param("apikey", "XPTO")).andDo(print()).andExpect(status().isOk());
			log.info("## final teste 01");
		}

		@Test
		public void testGetGeoDistancesByAddressIntegrated() throws Exception {
			log.info("## inicio teste 02");
			String origins = "Belo horizonte; Salvador;  Rio de janeiro;Campos dos Goytacazes; AV. Rio Branco, 1 Centro, Rio de janeiro RJ, 20090003";
			String destinations ="Belo horizonte; Campinas";
			List<String> addressOrigins = Arrays.asList(origins.split(";"));
			List<String> addressDestinations = Arrays.asList(destinations.split(";"));
	    	Gson g = new Gson();
	    	JsonElement element = g.fromJson(readFile(JSON_DISTANCE), JsonElement.class);
	    	JsonElement elementAddress = g.fromJson(readFile(JSON_GEO_POINT_DISTANCE), JsonElement.class);
	    	
	    	IGeoLocations serviceLocal = new GeoLocationServiceImpl(googleApiService);
	    	IGeoDistances serviceDistanceLocal = new GeoDistancesServiceImpl(googleApiService, serviceLocal);
	    	
	    	doReturn(element).when(googleApiService).getDistancesFromLocations(
	    			"Belo horizonte|  Salvador|   Rio de janeiro| Campos dos Goytacazes|  AV. Rio Branco, 1 Centro, Rio de janeiro RJ, 20090003",
	    			"Belo horizonte|  Campinas",
	    			"XPTO");
	    	
	    	doReturn(elementAddress).when(googleApiService)
	    	.getLocationFromGoogle("Belo Horizonte, MG, Brasil; Campinas, SP, Brasil; Salvador - BA, Brasil; Rio de Janeiro, RJ, Brasil; Campos dos Goytacazes - RJ, Brasil; Av. Rio Branco, 1 - Centro, Rio de Janeiro - RJ, 20090-003, Brasil", "XPTO");
	    	
	    	List<GeoDistanceDto> list = serviceDistanceLocal.getDistanceFromOriginDestinAddress(addressOrigins, addressDestinations,  "XPTO");
	    	
	    	when(geoDistanceService.getDistanceFromOriginDestinAddress(addressOrigins, addressDestinations, "XPTO")).thenReturn(list);

	    	
			
	    	MockHttpServletResponse response = this.mockMvc
	    			.perform(get("/api/distances")
	    					.param("origins","Belo horizonte; Salvador;  Rio de janeiro;Campos dos Goytacazes; AV. Rio Branco, 1 Centro, Rio de janeiro RJ, 20090003" )
	    					.param("destinations","Belo horizonte; Campinas")
	    					.param("apikey", "XPTO"))
	    			.andDo(print()).andExpect(status()
	    					.isOk()).andReturn().getResponse();
	    	
	    	assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
	    	JsonElement returnElement = g.fromJson(response.getContentAsString(), JsonElement.class);
	    	assertThat(returnElement.getAsJsonArray().size() > 2);
	    	log.info("## final test 02");
		}
		
		
		@Test
		public void testGetMaxMinByAddress() throws Exception {
			log.info("## inicio teste 03");
			String origins = "Belo horizonte; Salvador;  Rio de janeiro;Campos dos Goytacazes; AV. Rio Branco, 1 Centro, Rio de janeiro RJ, 20090003";
			String destinations ="Belo horizonte; Campinas";
			List<String> addressOrigins = Arrays.asList(origins.split(";"));
			List<String> addressDestinations = Arrays.asList(destinations.split(";"));
			GeoDistanceDto dto = new GeoDistanceDto();
			dto.setOriginAddress("Belo horizonte");
			dto.setDestinationAddress(" Campinas");
			dto.setDistanceValue(155.9);
			List<GeoDistanceDto> list = new ArrayList<>();
			list.add(dto);
			when(geoDistanceService.getMaxDistancesAndMin(addressOrigins, addressDestinations, "XPTO")).thenReturn(list);
			
			this.mockMvc.perform(get("/api/distances/maxmin").param("origins","Belo horizonte; Salvador;  Rio de janeiro;Campos dos Goytacazes; AV. Rio Branco, 1 Centro, Rio de janeiro RJ, 20090003" ).param("destinations","Belo horizonte; Campinas").param("apikey", "XPTO")).andDo(print()).andExpect(status().isOk());
			log.info("## final teste 03");
		}
		
		@Test
		public void testGetMaxMinByAddressIntegrated() throws Exception {
			log.info("## inicio teste 04");
			String origins = "Belo horizonte; Salvador;  Rio de janeiro;Campos dos Goytacazes; AV. Rio Branco, 1 Centro, Rio de janeiro RJ, 20090003";
			String destinations ="Belo horizonte; Campinas";
			List<String> addressOrigins = Arrays.asList(origins.split(";"));
			List<String> addressDestinations = Arrays.asList(destinations.split(";"));
	    	Gson g = new Gson();
	    	JsonElement element = g.fromJson(readFile(JSON_DISTANCE), JsonElement.class);
	    	JsonElement elementAddress = g.fromJson(readFile(JSON_GEO_POINT_DISTANCE), JsonElement.class);
	    	
	    	IGeoLocations serviceLocal = new GeoLocationServiceImpl(googleApiService);
	    	IGeoDistances serviceDistanceLocal = new GeoDistancesServiceImpl(googleApiService, serviceLocal);
	    	
	    	doReturn(element).when(googleApiService).getDistancesFromLocations(
	    			"Belo horizonte|  Salvador|   Rio de janeiro| Campos dos Goytacazes|  AV. Rio Branco, 1 Centro, Rio de janeiro RJ, 20090003",
	    			"Belo horizonte|  Campinas",
	    			"XPTO");
	    	
	    	doReturn(elementAddress).when(googleApiService)
	    	.getLocationFromGoogle("Belo Horizonte, MG, Brasil; Campinas, SP, Brasil; Salvador - BA, Brasil; Rio de Janeiro, RJ, Brasil; Campos dos Goytacazes - RJ, Brasil; Av. Rio Branco, 1 - Centro, Rio de Janeiro - RJ, 20090-003, Brasil", "XPTO");
	    	
	    	List<GeoDistanceDto> list = serviceDistanceLocal.getMaxDistancesAndMin(addressOrigins, addressDestinations,  "XPTO");
	    	
	    	when(geoDistanceService.getMaxDistancesAndMin(addressOrigins, addressDestinations, "XPTO")).thenReturn(list);

	    	
			
	    	MockHttpServletResponse response = this.mockMvc
	    			.perform(get("/api/distances/maxmin")
	    					.param("origins","Belo horizonte; Salvador;  Rio de janeiro;Campos dos Goytacazes; AV. Rio Branco, 1 Centro, Rio de janeiro RJ, 20090003" )
	    					.param("destinations","Belo horizonte; Campinas")
	    					.param("apikey", "XPTO"))
	    			.andDo(print()).andExpect(status()
	    					.isOk()).andReturn().getResponse();
	    	
	    	assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
	    	JsonElement returnElement = g.fromJson(response.getContentAsString(), JsonElement.class);
	    	assertThat(returnElement.getAsJsonArray().size() > 2);
	    	log.info("## final test 04");
		}
		
		
		@Test
		public void testAPIFinalIntegrated() throws Exception {
			log.info("## inicio teste 05");
			String origins = "Belo horizonte; Salvador;  Rio de janeiro;Campos dos Goytacazes; AV. Rio Branco, 1 Centro, Rio de janeiro RJ, 20090003";
			List<String> addressOrigins = Arrays.asList(origins.split(";"));
			List<String> addressDestinations = addressOrigins;
	    	Gson g = new Gson();
	    	JsonElement element = g.fromJson(readFile(JSON_MATRIX_DISTANCE), JsonElement.class);
	    	JsonElement elementAddress = g.fromJson(readFile(JSON_GEO_POINT_MATRIX_DISTANCE), JsonElement.class);
	    	
	    	IGeoLocations serviceLocal = new GeoLocationServiceImpl(googleApiService);
	    	IGeoDistances serviceDistanceLocal = new GeoDistancesServiceImpl(googleApiService, serviceLocal);
	    	
	    	doReturn(element).when(googleApiService).getDistancesFromLocations(
	    			"Belo horizonte|  Salvador|   Rio de janeiro| Campos dos Goytacazes|  AV. Rio Branco, 1 Centro, Rio de janeiro RJ, 20090003",
	    			"Belo horizonte|  Salvador|   Rio de janeiro| Campos dos Goytacazes|  AV. Rio Branco, 1 Centro, Rio de janeiro RJ, 20090003",
	    			"XPTO");
	    	
	    	doReturn(elementAddress).when(googleApiService)
	    	.getLocationFromGoogle("Belo Horizonte, MG, Brasil; Salvador - BA, Brasil; Rio de Janeiro, RJ, Brasil; Campos dos Goytacazes - RJ, Brasil; Av. Rio Branco, 1 - Centro, Rio de Janeiro - RJ, 20090-003, Brasil", "XPTO");
	    	
	    	List<GeoDistanceDto> list = serviceDistanceLocal.getMaxDistancesAndMin(addressOrigins, addressDestinations,  "XPTO");
	    	
	    	when(geoDistanceService.getMaxDistancesAndMin(addressOrigins, addressDestinations, "XPTO")).thenReturn(list);

	    	
			
	    	MockHttpServletResponse response = this.mockMvc
	    			.perform(get("/api/distances/matrix")
	    					.param("addressList","Belo horizonte; Salvador;  Rio de janeiro;Campos dos Goytacazes; AV. Rio Branco, 1 Centro, Rio de janeiro RJ, 20090003" )
	    					.param("apikey", "XPTO"))
	    			.andDo(print()).andExpect(status()
	    					.isOk()).andReturn().getResponse();
	    	
	    	assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
	    	JsonElement returnElement = g.fromJson(response.getContentAsString(), JsonElement.class);
	    	assertThat(returnElement.getAsJsonArray().size() > 2);
	    	log.info("## final test 05");
		}
		
		
		
		private static String readFile(String path) {
			String content = "";
			try {
				Path filePath = Path.of(	new ClassPathResource(
						path, 
						GeoDistanceTest.class.getClassLoader()).getFile().getAbsolutePath());
				content = Files.readString(filePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return content;
		}
	   

}
