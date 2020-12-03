package org.codingChallenge.kogan.fetcher;

import static org.codingChallenge.kogan.Constants.serverUrl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * Fetch a JSON Object of a given URL Path.
 * 
 * @author James Jayaputera.
 *
 */
public class Fetcher {
	/**
	 * Fetches a JSON object from a given URL path.
	 * 
	 * @param path 
	 *                A URL Path.
	 * @return (@link JsonObject} from the specified URL Path.
	 * 
	 * @throws IOException 
	 * 					If there is an error in connecting or fetching data.
	 * @throws JsonException
	 * 					If a JSON object cannot be created due to i/o error.
	 */
	public static JsonObject getJSONFromURL(String path) throws IOException, JsonException {
		HttpURLConnection httpClient =
                (HttpURLConnection) new URL(serverUrl + path).openConnection();

        // optional default is GET
        httpClient.setRequestMethod("GET");
        int responseCode = httpClient.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
        	 throw new IOException("An error occurred when connecting a server. Code: " + responseCode);
        }

        System.out.println("Connected to Server.");
        System.out.print("Fetching data from " + serverUrl + path + " ... ");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(httpClient.getInputStream())) ){
        	//create JsonReader object
    		JsonReader jsonReader = Json.createReader(br);
    		//get JsonObject from JsonReader
    		JsonObject jsonObject = jsonReader.readObject();
    		
    		//we can close IO resource and JsonReader now
    		jsonReader.close();
    		System.out.println("Done!");
    		return jsonObject;
        } catch (JsonException | IOException ex) {
            throw ex;
        }
	}
}
