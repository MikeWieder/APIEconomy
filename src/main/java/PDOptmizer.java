import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.util.Solutions;

import java.util.Collection;

/**
 *
 * Klasse, welche die eigentliche Optimierung des Pickup and Delivery Problems durchführt
 *
 */
public class PDOptmizer {

    public PDOptmizer() {

    }

    /**
     *
     * Verwendet aktuell einen einfachen (Standart) Algorithmus.
     *
     * @param problem Das zu optimierende Problem
     * @return Die Lösung(optimale Route) des übergebenen Problems
     */
    public VehicleRoutingProblemSolution buildSolution(VehicleRoutingProblem problem) {
        VehicleRoutingAlgorithm spassMitAlgo = Jsprit.createAlgorithm(problem);
        Collection<VehicleRoutingProblemSolution> solutions = spassMitAlgo.searchSolutions();
        return Solutions.bestOf(solutions);
    }

}
