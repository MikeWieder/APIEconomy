/**
 * Route containing pickup and delivery location
 * + relevant capacity concerning a specific route
 */
public class PDRoute implements Comparable<PDRoute> {

    private String jobName;
    private Place pickup;
    private Place delivery;
    private int capacity;

    /**
     *
     * @param jobName identifying a PD-job concerning a certain route; defined by client
     * @param pickup Place-Objekt der Pickup-Location
     * @param delivery Place-Objekt der Delivery-Location
     * @param capacity integer containing the capacity for a specific P-D-Route
     */
    public PDRoute(String jobName, Place pickup, Place delivery, int capacity) {
        this.jobName = jobName;
        this.pickup = pickup;
        this.delivery = delivery;
        this.capacity = capacity;
    }

    @Override
    public boolean equals(Object obj) {
        return this.delivery.equals(((PDRoute) obj).getDelivery()) && this.pickup.equals(((PDRoute) obj).getPickup());
    }

    @Override
    public int hashCode() {
        return this.delivery.hashCode() + this.pickup.hashCode();
    }

    public String getJobName(){ return jobName; }

    public Place getDelivery() {
        return delivery;
    }

    public Place getPickup() {
        return pickup;
    }

    public int getCapacity(){ return capacity; }

    @Override
    public String toString() {
        return pickup.toString() + " nach " + delivery.toString();
    }

    @Override
    public int compareTo(PDRoute o) {
        return Integer.compare((int)this.getPickup().getLat(),(int)o.getPickup().getLat());
    }
}
