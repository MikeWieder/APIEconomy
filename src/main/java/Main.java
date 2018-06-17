import com.graphhopper.*;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.FastVehicleRoutingTransportCostsMatrix;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Main {

    public static void main(String args[]) {

        JSONReader reader = new JSONReader();
        reader.readJSON();

        // TODO: 09.06.2018  use args to set the paths
        BaseRouting br = new BaseRouting("", "");

        PDOptmizer opti = new PDOptmizer();
        ProblemBuilder builder = new ProblemBuilder(br, reader.getPdRouteList(), reader.getVehicleDefinitionList());
        VehicleRoutingProblem problem = builder.buildProblem();
        VehicleRoutingProblemSolution solution = opti.buildSolution(problem);
        SolutionPrinter.print(problem,solution,SolutionPrinter.Print.VERBOSE);
        FastVehicleRoutingTransportCostsMatrix costsMatrix = (FastVehicleRoutingTransportCostsMatrix) problem.getTransportCosts();
        Map<String, Job> jobs = problem.getJobs();

        Map<PDRoute,InstructionList> routeInstructions = builder.getRouteInstructionsMap();
        List<PDRoute> routes = reader.getPdRouteList();
        List<VehicleDefinition> vehilces = reader.getVehicleDefinitionList();

        int totalDistance = 0;
        int[] distances = new int[solution.getRoutes().size()];
        int count = 0;
        for(VehicleRoute vr : solution.getRoutes()) {
            int distance = 0;
            int time = 0;
            int startIndex = 0;
            /*Location startLocation = null;
            int routeIndex = 0;
            PDRoute route = null;*/
            for(TourActivity ta : vr.getActivities()) {
                if(startIndex == 0) {
                    startIndex = ta.getIndex();
                    /*routeIndex = (ta.getIndex()-1)/2;
                    route = routes.get(routeIndex);
                    String locationID = route.getPickup().toString();
                    startLocation = ((Shipment)jobs.get(locationID)).getPickupLocation();*/
                    continue;
                }
                distance += costsMatrix.getDistance(startIndex-1,ta.getIndex()-1);


                //System.out.println("distance: " + costsMatrix.getDistance(startIndex-1,ta.getIndex()-1) + " start: " + (startIndex-1) + " dest: " + (ta.getIndex()-1));


                /*if(routeIndex % 2 == 0) {
                    String locationID = route.getPickup().toString();
                    startLocation = ((Shipment)jobs.get(locationID)).getPickupLocation();
                } else {
                    String locationID = route.getPickup().toString();
                    startLocation = ((Shipment)jobs.get(locationID)).getDeliveryLocation();
                }*/



                startIndex = ta.getIndex();
            }
            // TODO: 01.06.2018  duplicated locations in json
            System.out.println("Total distance of the route in km: " + distance);
            distances[count] = distance;
            totalDistance += distance;

            count++;

        }
        JSONBuilder jsonBuilder = new JSONBuilder();
        String jsonString =jsonBuilder.buildGeoJSON(solution,routes,vehilces);
        System.out.println(jsonString);


    }

}
