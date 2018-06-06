import com.graphhopper.util.shapes.GHPlace;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSONReader {

    private JSONParser parser = new JSONParser(); //JSON parser object to parse read file

    private Place startLocation, pickup, delivery;
    private VehicleDefinition vehicleDefinition;
    private List<VehicleDefinition> vehicleDefinitionList = new ArrayList<>();
    private PDRoute pdRoute;
    private List<PDRoute> pdRouteList = new ArrayList<>();

    public void readJSON (){
        {
            try {
                // read + parse file and convert to JSONObject
                JSONObject inputObject = (JSONObject) parser.parse(
                        new FileReader("D:\\IdeaProjects\\ApiEconomy\\src\\main\\resources\\Input.json"));

                //method to generate a List of PDRoute objects
                //returns integer placeID to pass its value to the Vehicle List
                //--> Place objects are numbered continuously
                int placeID = generatePDRouteList(inputObject);

                //method to generate a List of VehicleDefiniton objects
                generateVehicleDefinitionList(placeID, inputObject);

            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Getter for PDRoute list and Vehicle list
    public List<PDRoute> getPdRouteList() {
        return pdRouteList;
    }
    public List<VehicleDefinition> getVehicleDefinitionList() {
        return vehicleDefinitionList;
    }

    //method to generate a List of PDRoute objects
    private int generatePDRouteList(JSONObject inputObject) {

        JSONArray services = (JSONArray) inputObject.get("services");

        int placeID = 0;
        for (Object o : services){

            JSONObject serviceObjects = (JSONObject) o;

            //read coordinates of pickup location; initialize Place objects
            JSONObject pickupLocation = (JSONObject) serviceObjects.get("pickup");
            double lat = Double.parseDouble( (String) pickupLocation.get("x"));
            double lon = Double.parseDouble( (String) pickupLocation.get("y"));
            String locName = (String) serviceObjects.get("pickup-name_city");

            pickup = new Place(lat, lon, locName, placeID);

            //read coordinates of pickup location; initialize Place object with placeID + 1
            placeID++;
            JSONObject deliveryLocation = (JSONObject) serviceObjects.get("delivery");
            lat = Double.parseDouble( (String) pickupLocation.get("x"));
            lon = Double.parseDouble( (String) pickupLocation.get("y"));
            locName = (String) serviceObjects.get("delivery-name_city");

            delivery = new Place(lat, lon, locName, placeID);

            String jobID = (String) serviceObjects.get("jobID");
            int capacity = Integer.parseInt( (String) serviceObjects.get("capacity"));

            // generate PDRoute object with Places "pickup" & "delivery"
            pdRoute = new PDRoute(jobID, pickup, delivery, capacity);
            System.out.println("Generated PDRoute object.");

            pdRouteList.add(pdRoute);
            System.out.println("Elements within PDRouteList: "+ pdRouteList.size());
            placeID++;
        }
        return placeID;
    }

    //method to generate a List of VehicleDefiniton objects
    private void generateVehicleDefinitionList(int placeID, JSONObject inputObject) {
        JSONArray vehicles = (JSONArray) inputObject.get("vehicles");

        int vehicleID = 0;
        for (Object o : vehicles){

            JSONObject vehicleObjects = (JSONObject) o;

            int capacity = Integer.parseInt( (String) vehicleObjects.get("capacity"));
            String vehicleName = (String) vehicleObjects.get("id");

            JSONObject vehicleLocation = (JSONObject) vehicleObjects.get("location-coord");
            double lat = Double.parseDouble( (String) vehicleLocation.get("x"));
            double lon = Double.parseDouble( (String) vehicleLocation.get("y"));
            String locName = (String) vehicleObjects.get("loc-name_city");
            startLocation = new Place(lat, lon, locName, placeID);

            System.out.println("TEST PRINT");
            System.out.println(vehicleID +" "+ capacity +" "+ startLocation.getLocName()
                    +" "+ startLocation.getLat() +" "+ startLocation.getLon());

            vehicleDefinition = new VehicleDefinition(vehicleName, capacity, startLocation);
            System.out.println("Generated vehicledefiniton object with ID "+vehicleID);

            vehicleDefinitionList.add(vehicleDefinition);
            System.out.println("Elements within VehicleDefinitionList: "+vehicleDefinitionList.size());
            vehicleID++;
            placeID++;
        }
    }
}
