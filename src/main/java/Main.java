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

        List<PDRoute> routes = new ArrayList<>();

        // Hier werden die Routenpunkte(Pickups+Deliveries angelegt), sollte aus dem Input-JSON ausgelesen und damit erzeugt werden.

        Place start1 =  new Place(48.6480042, 9.4496037, "KH"); // Kirchheim
        Place destination1 = new Place(48.4791793,9.186754, "RT"); // Hochschule Reutlingen
        routes.add(new PDRoute(start1, destination1));

        Place start2 = new Place(48.6234705, 9.3428399, "NT"); //Nuertingen
        Place destination2 = new Place(48.4936279, 9.3991581, "BU"); // Bad-Urach
        routes.add(new PDRoute(start2, destination2));

        Place start3 = new Place(48.6921222, 9.1977333, "FH"); // Flughafen
        Place destination3 = new Place(48.5372796, 9.2846843, "MT"); // Metzingen
        routes.add(new PDRoute(start3, destination3));

        Place start4 = new Place(48.4107975, 9.4980255, "MS"); // Münsingen
        Place destination4 = new Place(48.5219655, 9.0646742, "TU"); // Tübingen
        routes.add(new PDRoute(start4, destination4));

        Place start5 = new Place(48.7352218, 9.3169017, "ES"); // Esslingen
        Place destination5 = new Place(48.7039347, 9.6508024, "GP"); // Göppingen
        routes.add(new PDRoute(start5, destination5));

        Place start6 = new Place(48.6724286, 9.5185991, "SB"); // Schlierbach
        Place destination6 = new Place(48.8221525, 9.3343294, "WB"); // Waiblingen
        routes.add(new PDRoute(start6, destination6));

        BaseRouting br = new BaseRouting("", "C:\\Users\\mike-\\OneDrive\\Dokumente\\GraphhoppeOutput");
        //Test: System.out.println(br.calcCostForRoute(start1,start6).getDistance());

        PDOptmizer opti = new PDOptmizer();
        ProblemBuilder builder = new ProblemBuilder(br, routes);
        VehicleRoutingProblem problem = builder.buildProblem();
        VehicleRoutingProblemSolution solution = opti.buildSolution(problem);
        SolutionPrinter.print(problem,solution,SolutionPrinter.Print.VERBOSE);
        FastVehicleRoutingTransportCostsMatrix costsMatrix = (FastVehicleRoutingTransportCostsMatrix) problem.getTransportCosts();
        Map<String, Job> jobs = problem.getJobs();

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
            JSONBuilder jsonBuilder = new JSONBuilder();
            String jsonString =jsonBuilder.buildSolutionJSON(solution,routes,distance);
            System.out.println(jsonString);
        }

    }

}
