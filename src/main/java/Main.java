import com.graphhopper.*;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.FastVehicleRoutingTransportCostsMatrix;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.*;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeoutException;


public class Main {

    private static final String RPC_QUEUE_NAME = "rpc_queue";

    public static void main(String args[]) {

        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("10.244.2.27");
//        factory.setPort(5672);

        factory.setHost("134.103.195.110");
        factory.setPort(31834);

        Connection connection = null;
        try {
            connection      = factory.newConnection();
            final Channel channel = connection.createChannel();

            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
            //channel.queuePurge(RPC_QUEUE_NAME);

            channel.basicQos(1);

            System.out.println("Awaiting RPC requests");

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                            .Builder()
                            .correlationId(properties.getCorrelationId())
                            .build();

                    String response = "";

                    try {
                        byte[] output = performOpt(body);
                        if(output != null) {
                            response += new String(output, "UTF-8");
                        }
                        else {
                            response = "'error':'app'";
                        }
                    }
                    catch (RuntimeException e){
                        System.out.println(" [.] " + e.toString());
                    }
                    finally {
                        channel.basicPublish( "", properties.getReplyTo(), replyProps, response.getBytes("UTF-8"));
                        channel.basicAck(envelope.getDeliveryTag(), false);
                        // RabbitMq consumer worker thread notifies the RPC server owner thread
                        synchronized(this) {
                            this.notify();
                        }
                    }
                }
            };

            channel.basicConsume(RPC_QUEUE_NAME, false, consumer);
            // Wait and be prepared to consume the message from RPC client.
            while (true) {
                synchronized(consumer) {
                    try {
                        consumer.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                try {
                    connection.close();
                } catch (IOException _ignore) {}
        }



    }

    public static byte[] performOpt(byte[] request) {

        JSONReader reader = new JSONReader();
        reader.readJSONFromByte(request);

        // TODO: 09.06.2018  use args to set the paths
        BaseRouting br = new BaseRouting("", "");

        PDOptmizer opti = new PDOptmizer();
        ProblemBuilder builder = new ProblemBuilder(br, reader.getPdRouteList(), reader.getVehicleDefinitionList());
        VehicleRoutingProblem problem = builder.buildProblem();
        VehicleRoutingProblemSolution solution = opti.buildSolution(problem);
        SolutionPrinter.print(problem,solution,SolutionPrinter.Print.VERBOSE);
        FastVehicleRoutingTransportCostsMatrix costsMatrix = (FastVehicleRoutingTransportCostsMatrix) problem.getTransportCosts();
        Map<String, Job> jobs = problem.getJobs();

        Map<PDRoute,InstructionList> routeInstructions = builder.getRouteInstructionsMap();
        List<PDRoute> routes = reader.getPdRouteList();
        List<VehicleDefinition> vehilces = reader.getVehicleDefinitionList();

        int totalDistance = 0;
        int[] distances = new int[solution.getRoutes().size()];
        int count = 0;
        for(VehicleRoute vr : solution.getRoutes()) {
            int distance = 0;
            int time = 0;
            int startIndex = 0;
            /*Location startLocation = null;
            int routeIndex = 0;
            PDRoute route = null;*/
            for(TourActivity ta : vr.getActivities()) {
                if(startIndex == 0) {
                    startIndex = ta.getIndex();
                    /*routeIndex = (ta.getIndex()-1)/2;
                    route = routes.get(routeIndex);
                    String locationID = route.getPickup().toString();
                    startLocation = ((Shipment)jobs.get(locationID)).getPickupLocation();*/
                    continue;
                }
                distance += costsMatrix.getDistance(startIndex-1,ta.getIndex()-1);


                //System.out.println("distance: " + costsMatrix.getDistance(startIndex-1,ta.getIndex()-1) + " start: " + (startIndex-1) + " dest: " + (ta.getIndex()-1));


                /*if(routeIndex % 2 == 0) {
                    String locationID = route.getPickup().toString();
                    startLocation = ((Shipment)jobs.get(locationID)).getPickupLocation();
                } else {
                    String locationID = route.getPickup().toString();
                    startLocation = ((Shipment)jobs.get(locationID)).getDeliveryLocation();
                }*/



                startIndex = ta.getIndex();
            }
            // TODO: 01.06.2018  duplicated locations in json
            System.out.println("Total distance of the route in km: " + distance);
            distances[count] = distance;
            totalDistance += distance;

            count++;

        }
        JSONBuilder jsonBuilder = new JSONBuilder();
        String jsonString =jsonBuilder.buildGeoJSON(solution,routes,vehilces);
        jsonString=jsonBuilder.buildShowCaseJSON(solution,routes,vehilces);
//        try {
//            File file = new File(".." + File.separator + "output" + File.separator + "output.json");
//            file.createNewFile();
//            FileWriter fileWriter = new FileWriter(file);
//            BufferedWriter writer = new BufferedWriter(fileWriter);
//            writer.write(jsonString);
//            writer.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        System.out.println(jsonString);

        return jsonString.getBytes();
    }

}
