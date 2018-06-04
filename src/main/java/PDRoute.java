/**
 * Eine Route bestehend aus Pickup und Delivery Locations
 */
public class PDRoute {

    private Place pickup;
    private Place delivery;

    /**
     *
     * @param pickup Place-Objekt der Pickup-Location
     * @param delivery Place-Objekt der Delivery-Location
     */
    public PDRoute(Place pickup, Place delivery) {
        // TODO: (04.06.2018, MFO) integrate P/D-capacity; here or in class Place...?
        this.delivery = delivery;
        this.pickup = pickup;
    }

    public Place getDelivery() {
        return delivery;
    }

    public Place getPickup() {
        return pickup;
    }

    @Override
    public String toString() {
        return pickup.toString() + " nach " + delivery.toString();
    }

}
