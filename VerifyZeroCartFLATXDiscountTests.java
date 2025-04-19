package com.springboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.controller.OfferRequest;
import com.springboot.controller.SegmentResponse;
import com.springboot.controller.ApplyOfferRequest;
import com.springboot.controller.ApplyOfferResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VerifyZeroCartFLATXDiscountTests {

// to verify the flag of empty cart value for user	
	@Test
		public void verifyZeroCartValue() throws Exception {
		    resetOffers();
		    Boolean isCartEmpty = false;
		    List<String> segments = new ArrayList<>();
		    segments.add("p1");
		    OfferRequest offerRequest = new OfferRequest(1, "FLATX", 100, segments);
		    addOffer(offerRequest);

		    // Step 2: Apply offer
		    ApplyOfferRequest applyOfferRequest = new ApplyOfferRequest(1, 1, 0); // user_id, restaurant_id, cart_value
		    ApplyOfferResponse response = applyOffer(applyOfferRequest);
		    if(applyOfferRequest.getCart_value()==0){
		    	isCartEmpty = true;
		    }
		    Assert.assertEquals(isCartEmpty, true);
		}


	public void resetOffers() throws Exception {
		URL url = new URL("http://localhost:9001/api/v1/reset");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.getResponseCode();
	}

	public boolean addOffer(OfferRequest offerRequest) throws Exception {
		String urlString = "http://localhost:9001/api/v1/offer";
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setRequestProperty("Content-Type", "application/json");

		ObjectMapper mapper = new ObjectMapper();

		String POST_PARAMS = mapper.writeValueAsString(offerRequest);
		OutputStream os = con.getOutputStream();
		os.write(POST_PARAMS.getBytes());
		os.flush();
		os.close();
		int responseCode = con.getResponseCode();
		System.out.println("POST Response Code :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) { 
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			// print result
			System.out.println(response.toString());
		} else {
			System.out.println("POST request did not work.");
		}
		return true;
	}

	public ApplyOfferResponse applyOffer(ApplyOfferRequest applyOfferRequest) throws Exception {
	    String urlString = "http://localhost:9001/api/v1/cart/apply_offer";
	    URL url = new URL(urlString);
	    HttpURLConnection con = (HttpURLConnection) url.openConnection();
	    con.setDoOutput(true);
	    con.setRequestMethod("POST");
	    con.setRequestProperty("Content-Type", "application/json");

	    ObjectMapper mapper = new ObjectMapper();

	    String POST_PARAMS = mapper.writeValueAsString(applyOfferRequest);
	    OutputStream os = con.getOutputStream();
	    os.write(POST_PARAMS.getBytes());
	    os.flush();
	    os.close();

	    int responseCode = con.getResponseCode();
	    if (responseCode == HttpURLConnection.HTTP_OK) {
	        InputStream inputStream = con.getInputStream();
	        return mapper.readValue(inputStream, ApplyOfferResponse.class);
	    } else {
	        throw new RuntimeException("Failed : HTTP error code : " + responseCode);
	    }
	}

}
