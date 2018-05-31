import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.List;

public class JSONBuilder {

    public JSONBuilder() {

    }

    public String buildSolutionJSON(VehicleRoutingProblemSolution solution, List<PDRoute> routes, int totalDistance) {


        Double totalTime = 0.;

        JSONArray array = new JSONArray();
        int routeCount = 0;

        for(VehicleRoute vr : solution.getRoutes()){
            JSONObject routeObject = new JSONObject();
            JSONArray steps = new JSONArray();
            JSONObject step = new JSONObject();
            JSONObject stepLocation = new JSONObject();

            int stepCount = 0;

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
                stepLocation.put("x", location.getLat()).put("y", location.getLon());
                step.put("Location", stepLocation);
                steps.put(stepCount, step);

                stepCount++;
            }

            routeObject.put("totalTime",totalTime).put("totalDistance",totalDistance);
            routeObject.put("steps",steps);
            array.put(routeCount, routeObject);

            routeCount++;

        }
        return array.toString();
    }

}
