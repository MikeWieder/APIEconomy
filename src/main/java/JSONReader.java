import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
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

                InputStream in = getClass().getResourceAsStream("input_v2.json");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                // read + parse file and convert to JSONObject
                JSONObject inputObject = (JSONObject) parser.parse(reader);
                //JSONObject inputObject = (JSONObject) parser.parse(
                        //new FileReader(getClass().getResource("input_v2.json").getPath()));

                //method to generate a List of PDRoute objects
                generatePDRouteList(inputObject);

                int placeID = getPdRouteList().size()*2;

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
            pickup = generatePlace(pickupLocation, placeID);

            //read coordinates and adress data of delivery location; initialize Place object with placeID + 1
            placeID++;
            JSONObject deliveryLocation = (JSONObject) serviceObjects.get("delivery");
            delivery = generatePlace(deliveryLocation, placeID);

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
        for (Object o : vehicles){

            JSONObject vehicleObjects = (JSONObject) o;

            String uniqueName = (String) vehicleObjects.get("id");
            int capacity = (int) (long) vehicleObjects.get("capacity");

            JSONObject vehicleLocation = (JSONObject) vehicleObjects.get("startlocation");
            startLocation = generatePlace(vehicleLocation, placeID);

            vehicleDefinition = new VehicleDefinition(uniqueName, capacity, startLocation);
            vehicleDefinitionList.add(vehicleDefinition);
            placeID++;

            System.out.println("TEST PRINT " +
                    "\nGenerated VehicleDefiniton object with ID "+uniqueName+" ; Place location ID: "+placeID
                    + "\nElements within VehicleDefinitionList: "+vehicleDefinitionList.size());
        }
    }

    private Place generatePlace(JSONObject location, int placeID) {
        double lat = (Double) location.get("lat");
        double lon = (Double) location.get("lon");
        String city = (String) location.get("city");
        String street = (String) location.get("street");
        int houseNo = (int) (long) location.get("houseNo");
        int zip = (int) (long) location.get("zip");

        return new Place(lat, lon, city, street,  houseNo, zip, placeID);
    }
}
