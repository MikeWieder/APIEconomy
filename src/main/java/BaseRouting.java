import com.graphhopper.*;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.*;
import com.graphhopper.util.shapes.GHPoint;

import java.util.Locale;

/**
 *
 * Klasse zum Durchführen von Routenberechnungen auf Basis von OSM-Kartenmaterial
 *
 */
public class BaseRouting {

    GraphHopperOSM hopper;
    String mapType;

    /**
     *
     * @param mapType String zum Speicherort der gewünschten Karte, wird aktuell nicht verwendet und wird auch nur dann tatsächlich benötigt wenn Daten benötigt werden,
     *                <br> welche nicht bereits verarbeitet wurden(nicht in OutputLocation vorhanden)
     * @param outputLocation Speicherpfad, wo die verarbeiteten Kartendaten gespeichert werden.
     */
    public BaseRouting(String mapType, String outputLocation) {
        hopper = (GraphHopperOSM) new GraphHopperOSM().forDesktop();
        setMap(mapType);
        setOutput(outputLocation);
        initPreProcessing();
    }
    // TODO: 29.05.2018 actually consider the supplied String for loading the correct map
    public void setMap(String mapType) {
        //hopper.setDataReaderFile(getClass().getResource("baden-wuerttemberg-latest.osm.pbf").getPath());
        //hopper.setDataReaderFile("../results/baden-wuerttemberg-latest.osm.pbf");
        //hopper.setDataReaderFile(mapType);
}

    public void setOutput(String outputLocation) {
        //String path = "results";
        try {
            //path = getClass().getResource("/results/").getPath();
        } catch(Exception e) {
            e.printStackTrace();
        }
        String path = "../results";
        hopper.setGraphHopperLocation(path);
        //hopper.setGraphHopperLocation(outputLocation);
    }


    /**
     * Verarbeiten des neuen Kartenmaterials oder Laden der bereits verarbeiteten Daten
     */
    public void initPreProcessing() {
        hopper.setEncodingManager(new EncodingManager("car"));
        hopper.importOrLoad();
    }

    public TransportationCost calcCostForRoute(Place start, Place destination) {

        // TODO: 29.05.2018 handle errors
        PathWrapper path = hopper.route(buildRequest(start,destination)).getBest();
        /*PointList list = path.getWaypoints();
        for(int i = 0; i<list.getSize();i++) {
            System.out.println("lon: " + list.getLon(i) + " lat: " + list.getLat(i));
        }
        System.out.println("----------------------------------------------------");*/

        /*for(Instruction instruction : path.getInstructions()) {
            System.out.println(instruction.getPoints().getLat(0));
        }
        System.out.println("-------------------------------");*/

        return processResults(path);

    }

    /**
     * Routenanfrage wird auf Basis des Start- und Zielpunkts erzeugt.
     * @param start Place-Objekt des Startpunkts
     * @param destination Place-Objekt des Zielpunkts
     * @return Erzeugter Graphhopper-Request
     */
    private GHRequest buildRequest(Place start, Place destination) {
        GHRequest req = new GHRequest(start.getLat(), start.getLon(), destination.getLat(), destination.getLon()).
        setWeighting("fastest").
                setVehicle("car").
                setLocale(Locale.GERMANY);
        return req;
    }


    /**
     *
     * Umrechnung der Distanz von Metern in Kilometer und der Zeit von ms in Minuten.
     *
     * @param path Erzeugte Route zwischen zwei Punkten.
     * @return TransportationCost-Object mit entsprechenden Daten.
     */
    private TransportationCost processResults(PathWrapper path) {
        double cost = 0;
        cost += path.getTime()/60000;
        cost += path.getDistance()/1000;
        //System.out.println(cost);
        return new TransportationCost((int)path.getTime()/60000, (int)(path.getDistance()/1000), path.getInstructions());
    }

}
