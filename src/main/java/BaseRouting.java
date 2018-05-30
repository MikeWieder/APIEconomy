import com.graphhopper.*;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.*;
import java.util.Locale;


public class BaseRouting {

    GraphHopperOSM hopper;
    String mapType;

    public BaseRouting(String mapType, String outputLocation) {
        hopper = (GraphHopperOSM) new GraphHopperOSM().forDesktop();
        setMap(mapType);
        setOutput(outputLocation);
        initPreProcessing();
    }
    // TODO: 29.05.2018 actually consider the supplied String for loading the correct map
    public void setMap(String mapType) {
        hopper.setDataReaderFile("C:\\Users\\mike-\\Downloads\\baden-wuerttemberg-latest.osm.pbf");
}

    public void setOutput(String outputLocation) {
        hopper.setGraphHopperLocation(outputLocation);
    }

    public void initPreProcessing() {
        hopper.setEncodingManager(new EncodingManager("car"));
        hopper.importOrLoad();
    }

    public TransportationCost calcCostForRoute(Place start, Place destination) {

        // TODO: 29.05.2018 handle errors
        PathWrapper path = hopper.route(buildRequest(start,destination)).getBest();
        return processResults(path);

    }

    private GHRequest buildRequest(Place start, Place destination) {
        GHRequest req = new GHRequest(start.getLat(), start.getLon(), destination.getLat(), destination.getLon()).
        setWeighting("fastest").
                setVehicle("car").
                setLocale(Locale.GERMANY);
        return req;
    }

    private TransportationCost processResults(PathWrapper path) {
        double cost = 0;
        cost += path.getTime()/60000;
        cost += path.getDistance()/1000;
        return new TransportationCost((int)path.getTime()/60000, (int)(path.getDistance()/1000), path.getInstructions());
    }

}
