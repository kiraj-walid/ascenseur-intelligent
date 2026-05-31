public class Person {
    private int id;

    private int currentFloor;
    private int destinationFloor;
    // Constructor
    public Person(int id, int currentFloor, int destinationFloor) {
        this.id = id;
        this.currentFloor = currentFloor;
        this.destinationFloor = destinationFloor;
    }
    // Getters and Setters
    public int getId() {
        return id;
    }
    public int getCurrentFloor() {
        return currentFloor;
    }
    public int getDestinationFloor() {
        return destinationFloor;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }
    public void setDestinationFloor(int destinationFloor) {
        this.destinationFloor = destinationFloor;
    }
}
