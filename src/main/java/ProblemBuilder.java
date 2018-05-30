import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.FastVehicleRoutingTransportCostsMatrix;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class ProblemBuilder {

    private BaseRouting br;
    private List<PDRoute> routes;

    public ProblemBuilder(BaseRouting br, List<PDRoute> routes) {
        this.br = br;
        this.routes = routes;
    }


    public VehicleRoutingProblem buildProblem() {

        VehicleRoutingProblem.Builder builder = VehicleRoutingProblem.Builder.newInstance();
        builder.setFleetSize(VehicleRoutingProblem.FleetSize.FINITE).setRoutingCost(buildCostMatrix());

        Collection<Shipment> shipments = createShipments();
        builder.addAllJobs(shipments);

        Location startLocation = null;

        for(Shipment shipment : shipments) {
            startLocation = shipment.getPickupLocation();
            break;
        }

        builder.addVehicle(buildVehicle(startLocation));

        Random random = new Random();

        /*for(int i = 0; i<getRoutes().size()*2;i++) {
            builder.addLocation(Integer.toString(i), Coordinate.newInstance(random.nextDouble()*10,random.nextDouble()*10));
        }*/

        VehicleRoutingProblem vrp = builder.build();

        return vrp;
    }

    public Collection<Shipment> createShipments() {

        List<Shipment> shipments = new ArrayList<>();
        List<PDRoute> routes = getRoutes();

        int i = 0;
        int j = 1;

        for(PDRoute route : routes) {

            Shipment shipment = Shipment.Builder.newInstance(Integer.toString(j-1)).addSizeDimension(0,1).setPickupLocation(Location.Builder.newInstance()
                    .setIndex(i).setId(Integer.toString(i)).build())
                    .setDeliveryLocation(Location.Builder.newInstance().setIndex(i+1).setId(Integer.toString(i+1)).build()).build();

            shipments.add(shipment);

            j++;
            i += 2;
        }

        return shipments;
    }

    public VehicleImpl buildVehicle(Location startLocation) {
        VehicleType type = VehicleTypeImpl.Builder.newInstance("type").addCapacityDimension(0, 5).setCostPerDistance(1).setCostPerTime(2).build();
        return VehicleImpl.Builder.newInstance("vehicle")
                .setStartLocation(startLocation).setType(type).build();
    }


    public FastVehicleRoutingTransportCostsMatrix buildCostMatrix() {

        List<PDRoute> routes = getRoutes();
        BaseRouting br = getBr();

        FastVehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = FastVehicleRoutingTransportCostsMatrix.Builder.newInstance(6,false);

        for(PDRoute route : routes) {

            int i = 0;

            TransportationCost tc = br.calcCostForRoute(route.getPickup(),route.getDelivery());
            costMatrixBuilder.addTransportTimeAndDistance(i,i+1, tc.getTime(), tc.getDistance());
            //costMatrixBuilder.addTransportTime(i,i+1, tc.getTime());

            tc = br.calcCostForRoute(route.getDelivery(),route.getPickup());
            costMatrixBuilder.addTransportTimeAndDistance(i+1,i, tc.getTime(), tc.getDistance());
            //costMatrixBuilder.addTransportTime(i+1,i, tc.getTime());


            for(int j = 0; j<routes.size();j++) {
                if(j==0) {
                    if (i == 0) j = 1;
                    if (i == 2) j = 2;
                    else j = i / 2 + 1;
                }

                PDRoute altRoute = routes.get(j);

                tc = br.calcCostForRoute(route.getDelivery(),altRoute.getPickup());
                costMatrixBuilder.addTransportTimeAndDistance(i+1,2*j, tc.getTime(), tc.getDistance());
                //costMatrixBuilder.addTransportTime(i+1,2*j, tc.getTime());

                tc = br.calcCostForRoute(altRoute.getDelivery(),route.getPickup());
                costMatrixBuilder.addTransportTimeAndDistance(2*j,i+1, tc.getTime(), tc.getDistance());
                //costMatrixBuilder.addTransportTime(2*j,i+1, tc.getTime());


                tc = br.calcCostForRoute(route.getDelivery(),altRoute.getDelivery());
                costMatrixBuilder.addTransportTimeAndDistance(i+1,2*j+1, tc.getTime(), tc.getDistance());
                //costMatrixBuilder.addTransportTime(i+1,2*j+1, tc.getTime());

                tc = br.calcCostForRoute(altRoute.getDelivery(),route.getDelivery());
                costMatrixBuilder.addTransportTimeAndDistance(2*j+1,i+1, tc.getTime(), tc.getDistance());
                //costMatrixBuilder.addTransportTime(2*j+1,i+1, tc.getTime());


                tc = br.calcCostForRoute(route.getPickup(),altRoute.getPickup());
                costMatrixBuilder.addTransportTimeAndDistance(i,2*j, tc.getTime(), tc.getDistance());
                //costMatrixBuilder.addTransportTime(i,2*j, tc.getTime());

                tc = br.calcCostForRoute(altRoute.getPickup(),route.getPickup());
                costMatrixBuilder.addTransportTimeAndDistance(2*j,i, tc.getTime(), tc.getDistance());
                //costMatrixBuilder.addTransportTime(2*j,i, tc.getTime());


                tc = br.calcCostForRoute(route.getPickup(),altRoute.getDelivery());
                costMatrixBuilder.addTransportTimeAndDistance(i,2*j+1, tc.getTime(), tc.getDistance());
                //costMatrixBuilder.addTransportTime(i,2*j+1, tc.getTime());

                tc = br.calcCostForRoute(altRoute.getPickup(),route.getDelivery());
                costMatrixBuilder.addTransportDistance(2*j+1,i, tc.getDistance());
                //costMatrixBuilder.addTransportTime(2*j+1,i, tc.getTime());
            }
            i +=2;


        }

        FastVehicleRoutingTransportCostsMatrix costMatrix = costMatrixBuilder.build();
        return costMatrix;

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
}
