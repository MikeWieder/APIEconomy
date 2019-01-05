import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JSONReader {

//    private JSONParser parser = new JSONParser(); //JSON parser object to parse read file
    private JSONTokener tokener;

    private Place startLocation, pickup, delivery;
    private VehicleDefinition vehicleDefinition;
    private List<VehicleDefinition> vehicleDefinitionList = new ArrayList<>();
    private PDRoute pdRoute;
    private List<PDRoute> pdRouteList = new ArrayList<>();

    public void readJSON (){
        {

            System.out.println(System.getProperty("user.dir"));
            InputStream in = getClass().getResourceAsStream("raw_data/input_v2.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            //BufferedReader reader = new BufferedReader(new FileReader("input_v2.json"));
            // read + parse file and convert to JSONObject
//                org.json.JSONObject inputObject = (org.json.JSONObject) parser.parse(reader);
            tokener = new JSONTokener(in);
            org.json.JSONObject inputObject = new org.json.JSONObject(tokener);

            //JSONObject inputObject = (JSONObject) parser.parse(
            //new FileReader(getClass().getResource("input_v2.json").getPath()));

            //method to generate a List of PDRoute objects
            generatePDRouteList(inputObject);

            int placeID = getPdRouteList().size()*2;

            //method to generate a List of VehicleDefiniton objects
            generateVehicleDefinitionList(placeID, inputObject);

        }
    }

    public void readJSONFromByte(byte[] json) {

        org.json.JSONObject inputObject = null;
        try {
            inputObject = new org.json.JSONObject(new String(json, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        generatePDRouteList(inputObject);
        generateVehicleDefinitionList(getPdRouteList().size()*2, inputObject);

    }

    // Getter for PDRoute list and Vehicle list
    public List<PDRoute> getPdRouteList() {
        return pdRouteList;
    }
    public List<VehicleDefinition> getVehicleDefinitionList() {
        return vehicleDefinitionList;
    }

    //method to generate a List of PDRoute objects
    private void generatePDRouteList(org.json.JSONObject inputObject) {

        JSONArray services = (JSONArray) inputObject.get("services");

        int placeID = 0;
        for (Object o : services){

            JSONObject serviceObjects = (JSONObject) o;
//            System.out.println(o);
            String jobID = (String) serviceObjects.get("jobid");
            int capacity = (int)serviceObjects.get("capacity");

            //read coordinates and adress data of pickup location; initialize Place objects
            org.json.JSONObject pickupLocation = (org.json.JSONObject) serviceObjects.get("pickup");
            pickup = generatePlace(pickupLocation, placeID);

            //read coordinates and adress data of delivery location; initialize Place object with placeID + 1
            placeID++;
            org.json.JSONObject deliveryLocation = (org.json.JSONObject) serviceObjects.get("delivery");
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
    private void generateVehicleDefinitionList(int placeID, org.json.JSONObject inputObject) {
        JSONArray vehicles = (JSONArray) inputObject.get("vehicles");
        for (Object o : vehicles){

            JSONObject vehicleObjects = (JSONObject) o;

            String uniqueName = (String) vehicleObjects.get("id");
            int capacity = (int)vehicleObjects.get("capacity");

            org.json.JSONObject vehicleLocation = (org.json.JSONObject) vehicleObjects.get("startlocation");
            startLocation = generatePlace(vehicleLocation, placeID);

            vehicleDefinition = new VehicleDefinition(uniqueName, capacity, startLocation);
            vehicleDefinitionList.add(vehicleDefinition);
            placeID++;

            System.out.println("TEST PRINT " +
                    "\nGenerated VehicleDefiniton object with ID "+uniqueName+" ; Place location ID: "+placeID
                    + "\nElements within VehicleDefinitionList: "+vehicleDefinitionList.size());
        }
    }

    private Place generatePlace(org.json.JSONObject location, int placeID) {
        double lat = (Double) location.get("lat");
        double lon = (Double) location.get("lon");
        String city = (String) location.get("city");
        String street = (String) location.get("street");
        int houseNo = (int)location.get("houseNo");
        int zip = (int)location.get("zip");
        TimeWindow timeWindow = new TimeWindow((double)location.get("start"), (double)location.get("end"));

        return new Place(lat, lon, city, street,  houseNo, zip, placeID, timeWindow);
    }
}
