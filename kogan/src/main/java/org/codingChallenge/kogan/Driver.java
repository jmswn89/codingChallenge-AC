package org.codingChallenge.kogan;

import java.io.IOException;

import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.codingChallenge.kogan.fetcher.Fetcher;

/**
 * Main class for coding challenge.
 * 
 * @author James Jayaputera
 *
 */
public class Driver
{
	private final String MAIN_PATH = "/api/products/1";
	private long totalAC;
	private double totalCubicWeight;
	
	public Driver() {
		totalAC = 0;
		totalCubicWeight = 0;
	}
	
	/**
	 * Calculate a cubic weight from a given field 'size'.
	 * 
	 * @param size the 'size' JsonObject.
	 * @return the cubic weight value.
	 * 
	 * @throws ClassCastException
	 */
	private double calculateCubicWeight(JsonObject size) throws ClassCastException {
		double cubicWeight = 0;
		try {
			JsonNumber tmp = size.getJsonNumber(Constants.lengthField);
			double length = (tmp == null) ? 0 : tmp.doubleValue() / 100;
			tmp = size.getJsonNumber(Constants.widthField);
			double width = (tmp == null) ? 0 : tmp.doubleValue() / 100;
			tmp = size.getJsonNumber(Constants.heightField);
			double height = (tmp == null) ? 0 : tmp.doubleValue() / 100;
			double volume = length * width * height;
			cubicWeight = volume / Constants.conversionFactor;
			return cubicWeight;
		}
		catch (ClassCastException ex) {
			throw ex;
		}

	}
	
	/**
	 * Extract the JsonArray from field 'objects' and iterate through each element in JsonArray.
	 * If the field 'category' has value 'Air Conditioners', extract the length, 
	 * width and height field value from the field 'size'.
	 *  
	 * @param obj The 'objects' JsonObject.
	 * @throws ClassCastException
	 */
	private void extractAndDoCalculation(JsonObject obj) throws ClassCastException{
		// Retrieve the "Objects" field from JSON Object.
		JsonArray objArr = obj.getJsonArray(Constants.objectsField);
		for (JsonValue value : objArr) {
			JsonObject tmp = value.asJsonObject();
			String category = tmp.getString(Constants.categoryField);
			if (category.equalsIgnoreCase("Air Conditioners")) {
				JsonObject sizeObj = tmp.getJsonObject(Constants.sizeField);
				double cubicWeight = calculateCubicWeight(sizeObj);
				totalCubicWeight += cubicWeight;
				totalAC++;
			}
		}
	}
	
	/**
	 * The entry point to fetch data and calculate the average cubic weight.
	 *  
	 * @throws JsonException indicates that some exception happened duringJSON processing.
	 * @throws IOException
	 */
	public void execute() throws JsonException, IOException {
		JsonObject obj = Fetcher.getJSONFromURL(MAIN_PATH);
		extractAndDoCalculation(obj);
		JsonString nextPathJson;

		while (!obj.isNull(Constants.nextField)) {
			nextPathJson = obj.getJsonString(Constants.nextField);
			obj = Fetcher.getJSONFromURL(nextPathJson.getString());
			extractAndDoCalculation(obj);
		}
		
		System.out.println("Total Cubic Weight : " + totalCubicWeight);
		System.out.println("Total Air Conditioners: " + totalAC);
		double avgCubicWeight = totalCubicWeight / totalAC;
		System.out.println("Average Cubic Weight: " + avgCubicWeight);
	}
	
	/**
	 * Main Function.
	 * @param args
	 */
    public static void main( String[] args )
    {
        System.out.println( "Welcome to Coding Challenge." );
        System.out.println( "by James Jayaputera" );
        System.out.println();
        Driver driver = new Driver();
        try {
			driver.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
