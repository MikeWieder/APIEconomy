import org.junit.Test;

import javax.xml.bind.Element;
import java.util.List;

public class JSONReaderTest {

    //@Test
    public void testJSONReader() {
        JSONReader reader = new JSONReader();
        reader.readJSON();

        List<PDRoute> pdRouteList = reader.getPdRouteList();
        List<VehicleDefinition> vehicleDefinitionList = reader.getVehicleDefinitionList();


        System.out.println("TEST FUNCTIONALITY of CLASS JSON READER" +
                "\nPDRoute-List size: " +pdRouteList.size() + "\n--> Try to print elements of PDRouteList created...");

        int i = 0;
        for (Object o : pdRouteList){
            System.out.println(pdRouteList.get(i));
            i++;
        }

        System.out.println("Try to print elements of VehicleDefinitionList created...");
        int j = 0;
        for (Object o : vehicleDefinitionList){
            System.out.println(vehicleDefinitionList.get(j));
            j++;
        }

    }
}
