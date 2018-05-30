public class PDRoute {

    private Place pickup;
    private Place delivery;

    public PDRoute(Place pickup, Place delivery) {
        this.delivery = delivery;
        this.pickup = pickup;
    }

    public Place getDelivery() {
        return delivery;
    }

    public Place getPickup() {
        return pickup;
    }
}
