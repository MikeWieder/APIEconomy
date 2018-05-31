import com.graphhopper.*;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
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

        BaseRouting br = new BaseRouting("", "C:\\Users\\Mike\\Documents\\GraphhopperResults");
        PDOptmizer opti = new PDOptmizer();
        ProblemBuilder builder = new ProblemBuilder(br, routes);
        VehicleRoutingProblem problem = builder.buildProblem();
        VehicleRoutingProblemSolution solution = opti.buildSolution(problem);
        SolutionPrinter.print(problem,solution,SolutionPrinter.Print.VERBOSE);

    }

}
