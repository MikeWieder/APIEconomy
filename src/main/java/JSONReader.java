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
                        new FileReader("src/main/resources/input_v2.json"));

                //method to generate a List of PDRoute objects
                //returns integer placeID to pass its value to the Vehicle List
                //--> Place objects are numbered continuously
                generatePDRouteList(inputObject);

                int placeID = getPdRouteList().size();

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
    private void generatePDRouteList(JSONObject inputObject) {

        JSONArray services = (JSONArray) inputObject.get("services");

        int placeID = 0;
        for (Object o : services){

            JSONObject serviceObjects = (JSONObject) o;

            String jobID = (String) serviceObjects.get("jobID");
            int capacity = (int) (long) serviceObjects.get("capacity");

            //read coordinates and adress data of pickup location; initialize Place objects
            JSONObject pickupLocation = (JSONObject) serviceObjects.get("pickup");
            double lat = (Double) pickupLocation.get("lat");
            double lon = (Double) pickupLocation.get("lon");
            String city = (String) pickupLocation.get("city");
            //TODO (060618, MFO) add street. houseNo, zip
            //+ street
            //+ houseNo
            //+ zip
            pickup = new Place(lat, lon, city, placeID);

            //read coordinates and adress data of delivery location; initialize Place object with placeID + 1
            placeID++;
            JSONObject deliveryLocation = (JSONObject) serviceObjects.get("delivery");
            lat = (Double) deliveryLocation.get("lat");
            lon = (Double) deliveryLocation.get("lon");
            city = (String) deliveryLocation.get("city");
            //TODO (060618, MFO) add street. houseNo, zip
            //+ street
            //+ houseNo
            //+ zip
            delivery = new Place(lat, lon, city, placeID);

            // generate PDRoute object with Places "pickup" & "delivery"
            pdRoute = new PDRoute(jobID, pickup, delivery, capacity);
            System.out.println("Generated PDRoute object.");

            pdRouteList.add(pdRoute);
            System.out.println("Elements within PDRouteList: "+ pdRouteList.size());
            placeID++;
        }
    }

    //method to generate a List of VehicleDefiniton objects
    private void generateVehicleDefinitionList(int placeID, JSONObject inputObject) {
        JSONArray vehicles = (JSONArray) inputObject.get("vehicles");
        placeID *=2;
        int vehicleID = 0;
        for (Object o : vehicles){

            JSONObject vehicleObjects = (JSONObject) o;

            String uniqueName = (String) vehicleObjects.get("id");
            int capacity = (int) (long) vehicleObjects.get("capacity");

            JSONObject vehicleLocation = (JSONObject) vehicleObjects.get("startlocation");
            double lat = (Double) vehicleLocation.get("lat");
            double lon = (Double) vehicleLocation.get("lon");
            String city = (String) vehicleLocation.get("city");
            //TODO (060618, MFO) add street. houseNo, zip
            //+ street
            //+ houseNo
            //+ zip
            startLocation = new Place(lat, lon, city, placeID);

            System.out.println("TEST PRINT");
            System.out.println(vehicleID +" "+ capacity +" "+ startLocation.getCity()
                    +" "+ startLocation.getLat() +" "+ startLocation.getLon());

            vehicleDefinition = new VehicleDefinition(uniqueName, capacity, startLocation);
            System.out.println("Generated VehicleDefiniton object with ID "+uniqueName+" ; Place location ID: "+placeID);

            vehicleDefinitionList.add(vehicleDefinition);
            System.out.println("Elements within VehicleDefinitionList: "+vehicleDefinitionList.size());
            vehicleID++;
            placeID++;
        }
    }
}
