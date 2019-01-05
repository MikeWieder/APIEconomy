import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.FastVehicleRoutingTransportCostsMatrix;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;
import com.graphhopper.util.InstructionList;

import java.util.*;


/**
 * Klasse zum zusammenbauen des VehicleRoutingProblem
 * <br> bestehend aus den einzelnen Jobs(bzw. Shipments, also den Einzelrouten),
 * <br> den zur Verfügung stehenden Fahrzeugen und der CostMatrix.
 */
public class ProblemBuilder {

    private BaseRouting br;
    private List<PDRoute> routes;
    private List<VehicleDefinition> vehicles;
    private Map<PDRoute,InstructionList> routeInstructionsMap;
    private double pickupServiceTime = 2;
    private double deliveryServiceTime = 1;

    /**
     *
     * @param br BaseRouting-Objekt zum Durchführen der einzelnen Routenberechnungen
     * @param routes Liste mit allen Routen (Pickups+Deliveries)
     * @param vehicles Liste mit den zur Verfuegung stehenden Fahrzeugen
     */
    public ProblemBuilder(BaseRouting br, List<PDRoute> routes, List<VehicleDefinition> vehicles) {
        this.br = br;
        this.routes = routes;
        this.vehicles = vehicles;
    }


    /**
     * Zentrale Methode der Klasse zum erzeugen des VehicleRoutingProblems.
     * <br> Das Problem basiert auf der im Konstrukter übergebenen Liste an Routen
     * <br> (Sollte evtl. noch geändert werden, außerdem sollten die Fahrzeuge zusätzlich übergeben
     * <br> und nicht wie aktuell statisch erzeugt werden.
     *
     * @return Das erzeugte VehicleRoutingProblem
     */
    public VehicleRoutingProblem buildProblem() {

        VehicleRoutingProblem.Builder builder = VehicleRoutingProblem.Builder.newInstance();
        builder.setFleetSize(VehicleRoutingProblem.FleetSize.FINITE).setRoutingCost(buildCostMatrix());



        Collection<Shipment> shipments = createShipments();
        builder.addAllJobs(shipments);

        Location startLocation = null;

        List<VehicleDefinition> vehicles = getVehicles();

        System.out.println("-------------------------VEHICLES----------------------");
        System.out.println(vehicles.toString());

        for(VehicleDefinition vehicle : vehicles) {
            //startLocation = Location.Builder.newInstance().setId(vehicle.getName()).setIndex(vehicle.getId()).build();
            //for(Shipment shipment : shipments) {
//                System.out.println(shipment.getPickupLocation().getId());
//                System.out.println(vehicle.getStartLocation().getLocID());
                //if(Integer.parseInt(shipment.getPickupLocation().getId()) == vehicle.getStartLocation().getLocID() || shipment.getDeliveryLocation().getIndex() == vehicle.getStartLocation().getLocID() ) {
                    //startLocation = shipment.getPickupLocation();
                //}
            //}
//            System.out.println(vehicle.getName());
            builder.addVehicle(buildVehicle(vehicle,false));
        }
        System.out.println(builder.getAddedVehicles().toString());
        //builder.addVehicle(buildVehicle(startLocation, "vehicle1",4, false));
        //builder.addVehicle(buildVehicle(startLocation, "vehicle2", 4, false));

        //Random random = new Random();

        /*for(int i = 0; i<getRoutes().size()*2;i++) {
            builder.addLocation(Integer.toString(i), Coordinate.newInstance(random.nextDouble()*10,random.nextDouble()*10));
        }*/

        VehicleRoutingProblem vrp = builder.build();

        return vrp;
    }

    /**
     *
     * Erstellt basierend auf der im Konstruktor übergebenen Liste an Routen die dazugehörigen
     * <br> Shipments. Jedes Shipment besitzt 2 Locations (Pickup + Delivery) welche mit einem Index und einer ID
     * <br> markiert werden. Diese werden entsprechend der Routen hochgezählt.
     * <br> Pickup-Route0 hat Index/ID 0; Delivery-Route0 hat Index/ID 1; Pickup-Route1 hat Index/ID 2 ...
     *
     * @return Liste, welche alle erzeugten Shipments beinhaltet.
     */
    private Collection<Shipment> createShipments() {

        List<Shipment> shipments = new ArrayList<>();
        List<PDRoute> routes = getRoutes();

        for(PDRoute route : routes) {

            //TimeWindow tw = new TimeWindow(0,20);
            Shipment shipment = Shipment.Builder.newInstance(route.toString()).addSizeDimension(0,route.getCapacity()).setPickupLocation(Location.Builder.newInstance()
                    .setIndex(route.getPickup().getLocID()).setId(Integer.toString(route.getPickup().getLocID())).build())
                    .setDeliveryLocation(Location.Builder.newInstance().setIndex(route.getDelivery().getLocID()).setId(Integer.toString(route.getDelivery().getLocID())).build())
                    .setDeliveryTimeWindow(route.getDelivery().getTimeWindow()).setPickupTimeWindow(route.getPickup().getTimeWindow()).setName(route.getJobName())
                    .setPickupServiceTime(pickupServiceTime).setDeliveryServiceTime(deliveryServiceTime).build();

            shipments.add(shipment);
        }

        return shipments;
    }

    /**
     *
     * @param vehicle Beschreibung des zu erzeugenden Fahrzeugs
     * @param returnToStart Bei true fährt das Fahrzeug nach dem abschließen des letzten Shipments zu seiner Startposition zurück.
     * @return Liefert das erzeugte Fahrzeug zurück, welches dem Problem hinzugefügt werden kann.
     */
    private VehicleImpl buildVehicle(VehicleDefinition vehicle, boolean returnToStart) {
        VehicleType type = VehicleTypeImpl.Builder.newInstance("type").addCapacityDimension(0, vehicle.getCapacity()).setCostPerDistance(1).setCostPerTransportTime(1).build();
        return VehicleImpl.Builder.newInstance(vehicle.getName()).setReturnToDepot(returnToStart)
                .setStartLocation(Location.Builder.newInstance().setIndex(vehicle.getStartLocation().getLocID()).setId(Integer.toString(vehicle.getStartLocation().getLocID())).setName(vehicle.getName()).build()).setType(type).build();
    }


    /**
     *
     * Baut die CostMatrix basierend auf den Routeliste auf.
     * <br> Zum berechnen der Distanzen und Zeiten wird die Klasse BaseRouting verwendet.
     * <br> Die erzeugte Matrix ist nicht symmetrisch, also A zu B entspricht nicht B zu A
     *
     * @return Die Matrix, welche sämtliche Distanzen und Zeiten zwischen allen Punkten beinhaltet.
     */
    private FastVehicleRoutingTransportCostsMatrix buildCostMatrix() {

        // TODO: 03.06.2018  Change the indices in the cost Matrix to the IDs of the Place objects
        List<PDRoute> routes = getRoutes();
        List<VehicleDefinition> vehicles = getVehicles();
        Map<PDRoute,InstructionList> routeInstructions = new TreeMap<>();
        BaseRouting br = getBr();

        FastVehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = FastVehicleRoutingTransportCostsMatrix.Builder.newInstance(routes.size()*2+getVehicles().size(),false);

        List<Place> places = buildListWithAllPlaces(routes,vehicles);

        Place start;
        Place destination;
        TransportationCost tc;
        for(int i = 0; i < places.size(); i++) {
            start = places.get(i);
            for(int j = i+1; j < places.size(); j++) {
                destination = places.get(j);
                //"Hinweg"
                tc = br.calcCostForRoute(start,destination);
                costMatrixBuilder.addTransportTimeAndDistance(start.getLocID(),destination.getLocID(),tc.getTime(),tc.getDistance());
                routeInstructions.put(new PDRoute(""+start.getLocID() + " to " + destination.getLocID(),start,destination,1), tc.getInstructions());

                //"Rueckweg"
                tc = br.calcCostForRoute(destination,start);
                costMatrixBuilder.addTransportTimeAndDistance(destination.getLocID(),start.getLocID(),tc.getTime(),tc.getDistance());
                routeInstructions.put(new PDRoute(""+destination.getLocID() + " to " + start.getLocID(),destination,start,1), tc.getInstructions());
            }

        }



       /* int i = 0;
        for(PDRoute route : routes) {



            TransportationCost tc = br.calcCostForRoute(route.getPickup(),route.getDelivery());
            costMatrixBuilder.addTransportTimeAndDistance(i,i+1, tc.getTime(), tc.getDistance());
            //costMatrixBuilder.addTransportTime(i,i+1, tc.getTime());
            routeInstructions.put(route,tc.getInstructions());

            tc = br.calcCostForRoute(route.getDelivery(),route.getPickup());
            costMatrixBuilder.addTransportTimeAndDistance(i+1,i, tc.getTime(), tc.getDistance());
            //costMatrixBuilder.addTransportTime(i+1,i, tc.getTime());
            routeInstructions.put(new PDRoute("wasweissich", route.getDelivery(),route.getPickup(), 1),tc.getInstructions());


            for(int j = 0; j<routes.size();j++) {
                if(j==0) {
                    if (i == 0) j = 1;
                    if (i == 2) j = 2;
                    else j = i / 2 + 1;
                }

                if(j == routes.size()) break;

                PDRoute altRoute = routes.get(j);

                tc = br.calcCostForRoute(route.getDelivery(),altRoute.getPickup());
                costMatrixBuilder.addTransportTimeAndDistance(i+1,2*j, tc.getTime(), tc.getDistance());
                //costMatrixBuilder.addTransportTime(i+1,2*j, tc.getTime());
                routeInstructions.put(new PDRoute("wasweissich", route.getDelivery(),altRoute.getPickup(), 1),tc.getInstructions());

                tc = br.calcCostForRoute(altRoute.getDelivery(),route.getPickup());
                costMatrixBuilder.addTransportTimeAndDistance(2*j+1,i, tc.getTime(), tc.getDistance());
                //costMatrixBuilder.addTransportTime(2*j,i+1, tc.getTime());
                routeInstructions.put(new PDRoute("wasweissich", altRoute.getDelivery(),route.getPickup(), 1),tc.getInstructions());


                tc = br.calcCostForRoute(route.getDelivery(),altRoute.getDelivery());
                costMatrixBuilder.addTransportTimeAndDistance(i+1,2*j+1, tc.getTime(), tc.getDistance());
                //costMatrixBuilder.addTransportTime(i+1,2*j+1, tc.getTime());
                routeInstructions.put(new PDRoute("wasweissich", route.getDelivery(),altRoute.getDelivery(), 1),tc.getInstructions());

                tc = br.calcCostForRoute(altRoute.getDelivery(),route.getDelivery());
                costMatrixBuilder.addTransportTimeAndDistance(2*j+1,i+1, tc.getTime(), tc.getDistance());
                //costMatrixBuilder.addTransportTime(2*j+1,i+1, tc.getTime());
                routeInstructions.put(new PDRoute("wasweissich", altRoute.getDelivery(),route.getDelivery(), 1),tc.getInstructions());


                tc = br.calcCostForRoute(route.getPickup(),altRoute.getPickup());
                costMatrixBuilder.addTransportTimeAndDistance(i,2*j, tc.getTime(), tc.getDistance());
                //costMatrixBuilder.addTransportTime(i,2*j, tc.getTime());
                routeInstructions.put(new PDRoute("wasweissich", route.getPickup(),altRoute.getPickup(), 1),tc.getInstructions());

                tc = br.calcCostForRoute(altRoute.getPickup(),route.getPickup());
                costMatrixBuilder.addTransportTimeAndDistance(2*j,i, tc.getTime(), tc.getDistance());
                //costMatrixBuilder.addTransportTime(2*j,i, tc.getTime());
                routeInstructions.put(new PDRoute("wasweissich", altRoute.getPickup(),route.getPickup(), 1),tc.getInstructions());


                tc = br.calcCostForRoute(route.getPickup(),altRoute.getDelivery());
                costMatrixBuilder.addTransportTimeAndDistance(i,2*j+1, tc.getTime(), tc.getDistance());
                //costMatrixBuilder.addTransportTime(i,2*j+1, tc.getTime());
                routeInstructions.put(new PDRoute("wasweissich", route.getPickup(),altRoute.getPickup(), 1),tc.getInstructions());

                tc = br.calcCostForRoute(altRoute.getPickup(),route.getDelivery());
                costMatrixBuilder.addTransportTimeAndDistance(2*j,i+1, tc.getTime(), tc.getDistance());
                //costMatrixBuilder.addTransportTime(2*j+1,i, tc.getTime());
                routeInstructions.put(new PDRoute("wasweissich", altRoute.getDelivery(),route.getDelivery(), 1),tc.getInstructions());
            }
            i +=2;


        }*/

        FastVehicleRoutingTransportCostsMatrix costMatrix = costMatrixBuilder.build();

        /*for(int i = 0; i<places.size();i++){
            for(int j = 0; j<places.size();j++) {
                if(i==j) continue;
                for(Place place : places) {
                    if(place.getLocID() == i) {
                        //System.out.println("Von: " + place.toString());
                    }
                    if(place.getLocID() == j) {
                        //System.out.println("Nach: " + place.toString());
                    }
                }
                //System.out.println("Von: "+i+ " Nach: "+j+ " " +costMatrix.getDistance(i,j));
            }
        }*/

        routeInstructionsMap = routeInstructions;
        return costMatrix;

    }

    public List<Place> buildListWithAllPlaces(List<PDRoute> routes, List<VehicleDefinition> vehicles) {
        List<Place> places = new ArrayList<>();

        for(PDRoute route : routes) {
            places.add(route.getPickup());
            places.add(route.getDelivery());
        }
        for(VehicleDefinition vehicle : vehicles) {
            places.add(vehicle.getStartLocation());
        }

        return places;
    }

    public TransportationCost[] performCostCalc(PDRoute route) {

        // TODO: 29.05.2018  array solution is messy, possibly causing confusion between the differen routes
        TransportationCost[] costs = new TransportationCost[2];
        costs[0] = br.calcCostForRoute(route.getPickup(), route.getDelivery());
        costs[1] = br.calcCostForRoute(route.getPickup(), route.getDelivery());
        return costs;

    }

    public List<PDRoute> getRoutes() {
        return routes;
    }

    public BaseRouting getBr() {
        return br;
    }

    public List<VehicleDefinition> getVehicles() { return vehicles; }

    public Map<PDRoute,InstructionList> getRouteInstructionsMap() {
        return routeInstructionsMap;
    }
}
