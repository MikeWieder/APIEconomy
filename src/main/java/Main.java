import com.graphhopper.*;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
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

        Place start1 =  new Place(48.6480042, 9.4496037); // Kirchheim
        Place destination1 = new Place(48.4791793,9.186754); // Hochschule Reutlingen
        routes.add(new PDRoute(start1, destination1));

        Place start2 = new Place(48.6234705, 9.3428399); //Nuertingen
        Place destination2 = new Place(48.4936279, 9.3991581); // Bad-Urach
        routes.add(new PDRoute(start2, destination2));

        Place start3 = new Place(48.6921222, 9.1977333); // Flughafen
        Place destination3 = new Place(48.5372796, 9.2846843); // Metzingen
        routes.add(new PDRoute(start3, destination3));

        BaseRouting br = new BaseRouting("", "C:\\Users\\mike-\\OneDrive\\Dokumente\\GraphhoppeOutput");
        PDOptmizer opti = new PDOptmizer();
        ProblemBuilder builder = new ProblemBuilder(br, routes);
        VehicleRoutingProblem problem = builder.buildProblem();
        VehicleRoutingProblemSolution solution = opti.buildSolution(problem);
        SolutionPrinter.print(problem,solution,SolutionPrinter.Print.VERBOSE);

    }

}
