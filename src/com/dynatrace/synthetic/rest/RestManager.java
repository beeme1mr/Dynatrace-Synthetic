package com.dynatrace.synthetic.rest;

import java.io.IOException;
import java.util.logging.Logger;

import org.json.JSONException;

import com.dynatrace.synthetic.monitor;
import com.dynatrace.synthetic.common.Vars;
import com.dynatrace.synthetic.data.monitor.Monitors;
import com.dynatrace.synthetic.data.results.TestData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class RestManager {
	
	private static final Logger log = Logger.getLogger(monitor.class.getName());
	
	static{
        // Only one time
		Unirest.setObjectMapper(new ObjectMapper() {
	        private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
	                = new com.fasterxml.jackson.databind.ObjectMapper();
		
	        public <T> T readValue(String value, Class<T> valueType) {
	            try {
	                return jacksonObjectMapper.readValue(value, valueType);
	            } catch (IOException e) {
	                throw new RuntimeException(e);
	            }
	        }
		
	        public String writeValue(Object value) {
	            try {
	                return jacksonObjectMapper.writeValueAsString(value);
	            } catch (JsonProcessingException e) {
	                throw new RuntimeException(e);
	            }
	        }
	    });
		
		Unirest.setTimeouts(Vars.DEFAULT_CONNECTION_TIMEOUT_MILLISECONDS, Vars.DEFAULT_SOCKET_TIMEOUT_MILLISECONDS);
		Unirest.setConcurrency(Vars.DEFAULT_MAX_CONNECTION_LIMIT, Vars.DEFAULT_MAX_CONNECTIONS_PER_ROUTE);
	}

	public static Monitors getMonitors(String token, String testName, String testType){
	    Monitors monitors = null;
	    try {
		    HttpResponse<Monitors> response = Unirest.get(Vars.REST_URL)
		            .routeParam("method", "tests")
		            .header("Accept", "application/json")
		            .header("Authentication", "bearer " + token)
		            .queryString("testName", testName)
		            .queryString("testType", testType)
		            .asObject(Monitors.class);
		
		    if (response.getStatus() == 200) {
		        log.fine("Successfully received the test list");
		        monitors = response.getBody();
		    } else {
		        log.severe("An unexpected error has occurred!");
		        log.severe(response.getBody().toString());
		    }
	    } catch (UnirestException e){
	    	log.severe("A Unirest exception has been thrown while attempting to get test data");
	    	log.severe(e.getMessage());
		} catch (JSONException e){
			log.severe("A JSON exception has been thrown while attempting to get test data");
			log.severe(e.getMessage());
		} catch (Exception e){
			log.severe("An exception has been thrown while attempting to get the list of monitors.");
			log.severe(e.getMessage());
		}
	    return monitors;
	}

	public static String Authenticate(String user, String password){
		log.fine("Attempting to authenticate " + user);
	    String accessToken = null;
		try{
		    HttpResponse<JsonNode> response = Unirest.post(Vars.REST_URL)
		            .routeParam("method", "login")
		            .header("Accept", "application/json")
		            .body("{\"user\":\"" + user + "\", \"password\": \"" + password + "\"}")
		            .asJson();
		    if(response.getStatus() == 200) {
		        log.fine("Successfully authenticated");
		        accessToken = response.getBody().getObject().getString("accessToken");
		    } else if (response.getStatus() == 401) {
		        log.severe("Unauthorized!");
		        log.severe(response.getBody().toString());
		    } else {
		        log.severe("An unexpected error has occurred!");
		        log.severe(response.getBody().toString());
		    }
		} catch (UnirestException e){
	    	log.severe("A Unirest exception has been thrown while attempting to get test data");
	    	log.severe(e.getMessage());
		} catch (JSONException e){
			log.severe("A JSON exception has been thrown while attempting to get test data.");
			log.severe(e.getMessage());
		} catch (Exception e){
			log.severe("An exception has been thrown while attempting to authenticate.");
			log.severe(e.getMessage());
		}
		
	    return accessToken;
	}

	public static TestData getTestData(String token, String monitorId, long startTime){
		TestData testData = null;
		try {
		    HttpResponse<TestData> response = Unirest.get(Vars.REST_URL + "/" + monitorId)
		            .routeParam("method", "testresults")
		            .header("Accept", "application/json")
		            .header("Authentication", "bearer " + token)
		            .queryString("start", startTime)
		            .asObject(TestData.class);	
			
		    if (response.getStatus() == 200) {
		    	log.fine("Successfully received test data");
		    	testData = response.getBody();
		    } else {
		        log.severe("An unexpected error has occurred!");
		        log.severe(response.getBody().toString());
		    }
		} catch (UnirestException e){
	    	log.severe("A Unirest exception has been thrown while attempting to get test data.");
	    	log.severe(e.getMessage());
		} catch (JSONException e){
			log.severe("A JSON exception has been thrown while attempting to get test data.");
			log.severe(e.getMessage());
		} catch (Exception e){
			log.severe("An exception has been thrown while attempting to get test data.");
			log.severe(e.getMessage());
		}
		return testData;		
	}
	
	public static void enableProxy(String host, int port){
		System.setProperty("https.proxyHost", host);
		System.setProperty("https.proxyPort", String.valueOf(port));
	}
	
	public static void enableSecureProxy(String host, int port, String user, String password){
		enableProxy(host, port);
		System.setProperty("https.proxyUser", user);
		System.setProperty("https.proxyPassword", password);
	
	}
	public static void shutdownRest(){
		try {
			Unirest.shutdown();
		} catch (IOException e) {
			log.severe("Unable to shutdown Unirest");
			e.printStackTrace();
		}
	}
}
