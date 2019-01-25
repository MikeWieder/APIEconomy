import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.List;
import java.util.Map;


/**
 * Klasse zum Erzeugen von JSON-Strings
 */
public class JSONBuilder {

    public JSONBuilder() {

    }

    /**
     *
     * Erzeugt ein JSON mit allen relevanten Informationen zu der Lösung
     * <br> noch NICHT fertig, fehlerhaft bei mehreren Fahrzeugen
     *
     * @param solution Lösung zum Pickup and Delivery Problem
     * @param routes Sämtliche "Grundrouten", also Liste an Pickups und dazugehörigen Deliveries.
     * @param distances Gesamte Distanz der einzelnen Routen
     * @return String, welcher das erzeugte JSON beinhaltet
     */
    public String buildSolutionJSON(VehicleRoutingProblemSolution solution, List<PDRoute> routes, int[] distances, Map<PDRoute,InstructionList> routeInstructions) {


        Double totalTime = 0.;

        JSONArray array = new JSONArray();
        int routeCount = 0;

        for(VehicleRoute vr : solution.getRoutes()){
            totalTime = 0.;
            JSONObject routeObject = new JSONObject();
            JSONArray steps = new JSONArray();
            JSONObject step = new JSONObject();
            JSONObject stepLocation = new JSONObject();

            int stepCount = 0;
            Place locationMem = null;
            for(TourActivity activity :vr.getActivities()) {
                totalTime = activity.getArrTime();
                int activityIndex = activity.getIndex();

                PDRoute route = routes.get((activityIndex-1)/2);
                Place location;

                if(activityIndex % 2 == 0) {
                    location = route.getPickup();
                }else {
                    location = route.getDelivery();
                }

                if(locationMem == null) {
                    locationMem = location;
                } else {
                    PDRoute instrRoute = new PDRoute("abc", locationMem, location, 1);
                    //System.out.println(locationMem.getLat() + " --- " + locationMem.getLon());
                    //System.out.println(location.getLat() + " --- " + location.getLon());

                    InstructionList instructions = routeInstructions.get(instrRoute);
                    //System.out.println(instructions);

                    for(Instruction instruction : instructions) {
                        //System.out.println(instruction.getPoints().getLat(0));
                        //System.out.println(instruction.getPoints().getLon(0));
                    }
                    locationMem = location;
                }

                stepLocation.put("x", location.getLat()).put("y", location.getLon());
                step.put("Location", stepLocation);
                steps.put(stepCount, step);

                stepCount++;
            }

            routeObject.put("totalTime",totalTime).put("totalDistance",distances[routeCount]);
            routeObject.put("steps",steps);
            array.put(routeCount, routeObject);
            //System.out.println(stepCount + "-------------------------------------------------------");
            routeCount++;

        }
        System.out.println(routeCount);
        return array.toString();
    }

    public String buildGeoJSON(VehicleRoutingProblemSolution solution, List<PDRoute> routes, List<VehicleDefinition> vehicles) {
        JSONObject outerObject = new JSONObject();
        outerObject.put("type", "FeatureCollection");
        JSONArray featureArray = new JSONArray();


        for(VehicleRoute vr : solution.getRoutes()) {
            JSONObject lineString = new JSONObject();
            lineString.put("type", "LineString");
            JSONObject feature = new JSONObject();
            feature.put("type", "Feature");
            JSONArray routeArray = new JSONArray();
            for(TourActivity activity : vr.getActivities()) {
                int index = activity.getLocation().getIndex();
                JSONArray locEntry = new JSONArray();
                for(PDRoute route : routes) {
                    if(route.getPickup().getLocID() == index) {
                        locEntry.put(route.getPickup().getLat());
                        locEntry.put(route.getPickup().getLon());
                    }
                    if(route.getDelivery().getLocID() == index) {
                        locEntry.put(route.getDelivery().getLat());
                        locEntry.put(route.getDelivery().getLon());
                    }
                }
                routeArray.put(locEntry);
            }
            lineString.put("coordinates", routeArray);
            feature.put("geometry", lineString);
            JSONObject properties = new JSONObject();
            properties.put("vehicle", vr.getVehicle().getId());
            feature.put("properties", properties);
            featureArray.put(feature);
        }
        outerObject.put("features", featureArray);

        return outerObject.toString(1);
    }

    public String buildShowCaseJSON(VehicleRoutingProblemSolution solution, List<PDRoute> routes, List<VehicleDefinition> vehicles) {

        JSONObject obj = new JSONObject();

        for(VehicleRoute vr : solution.getRoutes()) {
            JSONArray vehicleRoute = new JSONArray();
            String vehicleid = vr.getVehicle().getStartLocation().getId();
            for(VehicleDefinition vehicle : vehicles) {
                System.out.println(vehicle.getStartLocation().getLocID());
                System.out.println(vehicleid);
                if(Integer.toString(vehicle.getStartLocation().getLocID()).equals(vehicleid)) {
                    System.out.println("here");
                    vehicleRoute.put(""+vehicle.getStartLocation().getLat()+", "+vehicle.getStartLocation().getLon()+"");
                }
            }

            for(TourActivity activity : vr.getActivities()) {
                int index = activity.getLocation().getIndex();
                for(PDRoute route : routes) {
                    if(route.getPickup().getLocID() == index) {
                        vehicleRoute.put(""+route.getPickup().getLat()+", "+route.getPickup().getLon()+"");
                    }
                    if(route.getDelivery().getLocID() == index) {
                        vehicleRoute.put(""+route.getDelivery().getLat()+", "+route.getDelivery().getLon()+"");
                    }
                }
            }
            obj.put(vr.getVehicle().getId(),vehicleRoute);

        }




        return obj.toString();
    }

}
