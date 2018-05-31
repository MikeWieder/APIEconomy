import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.FastVehicleRoutingTransportCostsMatrix;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.Translation;
import com.graphhopper.util.TranslationMap;


import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TestClass {

    public static void main(String[] args) {


        FastVehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = FastVehicleRoutingTransportCostsMatrix.Builder.newInstance(4,true);

        costMatrixBuilder.addTransportTimeAndDistance(0,1,10,10);
        costMatrixBuilder.addTransportTimeAndDistance(0,2,10,25);
        costMatrixBuilder.addTransportTimeAndDistance(0,3,45,30);
        costMatrixBuilder.addTransportTimeAndDistance(1,2,22,41);
        costMatrixBuilder.addTransportTimeAndDistance(1,3,5,15);
        costMatrixBuilder.addTransportTimeAndDistance(2,3,20,25);

        FastVehicleRoutingTransportCostsMatrix costMatrix = costMatrixBuilder.build();

        Shipment shipment1 = Shipment.Builder.newInstance(Integer.toString(0)).addSizeDimension(0,1)
                .setPickupLocation(Location.Builder.newInstance().setIndex(0).setId("Pickup1").build())
                .setDeliveryLocation(Location.Builder.newInstance().setIndex(1).setId("Delivery1").build()).build();

        Shipment shipment2 = Shipment.Builder.newInstance(Integer.toString(1)).addSizeDimension(0,1)
                .setPickupLocation(Location.Builder.newInstance().setIndex(2).setId("Pickup2").build())
                .setDeliveryLocation(Location.Builder.newInstance().setIndex(3).setId("Delivery2").build()).build();

        VehicleType type = VehicleTypeImpl.Builder.newInstance("type").addCapacityDimension(0, 5).setCostPerDistance(1).setCostPerTime(2).build();
        VehicleImpl vehicle = VehicleImpl.Builder.newInstance("vehicle")
                .setStartLocation(shipment1.getPickupLocation()).setType(type).build();


        VehicleRoutingProblem.Builder builder = VehicleRoutingProblem.Builder.newInstance();
        builder.setFleetSize(VehicleRoutingProblem.FleetSize.FINITE).setRoutingCost(costMatrix).addVehicle(vehicle);
        builder.addJob(shipment1).addJob(shipment2);

        VehicleRoutingProblem vrp = builder.build();

        VehicleRoutingAlgorithm spassMitAlgo = Jsprit.createAlgorithm(vrp);
        Collection<VehicleRoutingProblemSolution> solutions = spassMitAlgo.searchSolutions();
        SolutionPrinter.print(vrp,Solutions.bestOf(solutions),SolutionPrinter.Print.VERBOSE);

        BaseRouting br = new BaseRouting("","C:\\Users\\mike-\\OneDrive\\Dokumente\\GraphhoppeOutput");

        Place start3 = new Place(48.6921222, 9.1977333, "Flughafen"); // Flughafen
        Place destination3 = new Place(48.5372796, 9.2846843, "Metzingen"); // Metzingen

        TransportationCost tc = br.calcCostForRoute(start3,destination3);
        System.out.println(tc.getDistance() + tc.getTime());

        TranslationMap tm = new TranslationMap().doImport();
        Translation tl = tm.getWithFallBack(Locale.GERMANY);
        for(Instruction in : tc.getInstructions()) {
            System.out.println(in.getTurnDescription(tl));
            System.out.println(in.getDistance());
        }

        List<Map<String, Object>> iList = tc.getInstructions().createJson();
        System.out.println(iList.toString());



    }

}
