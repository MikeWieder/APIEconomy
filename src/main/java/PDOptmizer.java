import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.util.Solutions;

import java.util.Collection;

public class PDOptmizer {

    public PDOptmizer() {

    }

    public VehicleRoutingProblemSolution buildSolution(VehicleRoutingProblem problem) {
        VehicleRoutingAlgorithm spassMitAlgo = Jsprit.createAlgorithm(problem);
        Collection<VehicleRoutingProblemSolution> solutions = spassMitAlgo.searchSolutions();
        return Solutions.bestOf(solutions);
    }

}
