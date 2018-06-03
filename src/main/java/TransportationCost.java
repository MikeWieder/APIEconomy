import com.graphhopper.util.InstructionList;


/**
 *
 * Klasse zum verwalten von Zeiten und Distanzen einer Route.
 * <br> Beinhaltet außerdem die Liste an Instruktion für eine Route
 *
 */
public class TransportationCost {

    private int time;
    private int distance;
    private InstructionList instructions;

    public TransportationCost(int time, int distance, InstructionList instructions) {
        this.time = time;
        this.distance = distance;
        this.instructions = instructions;
    }

    public int getDistance() {
        return distance;
    }

    public int getTime() {
        return time;
    }

    public InstructionList getInstructions() {
        return instructions;
    }
}
