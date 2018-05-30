import com.graphhopper.util.InstructionList;

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
