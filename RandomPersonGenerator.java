import java.util.ArrayList;
import java.util.List;

public class RandomPersonGenerator implements Runnable {
    @Override
    public void run() {
        while (true) {
            for (Floor floor : floors) {
                System.out.println("Generating people on Floor " + floor.getFloorNumber());
                generateRandomPeople(floor);
            }
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                System.out.println("RandomPersonGenerator thread interrupted. Stopping generation.");
                Thread.currentThread().interrupt();
            }
        }
    }

    private int numberOfFloors;
    private int personIdCounter;
    private List<Floor> floors;

    public RandomPersonGenerator(int numberOfFloors) {
        this.numberOfFloors = numberOfFloors;
        this.personIdCounter = 0;
        this.floors = new ArrayList<Floor>();
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public void setFloors(List<Floor> floors) {
        this.floors = floors;
    }

    public void generateRandomPeople(Floor floor) {

        int numberOfPeople = 2;
        for (int i = 0; i < numberOfPeople; i++) {
            int destinationFloor;
            do {
                destinationFloor = (int) (Math.random() * numberOfFloors); // Random destination floor
            } while (destinationFloor == floor.getFloorNumber()); // Ensure destination is different from current floor

            Person person = new Person(personIdCounter++, floor.getFloorNumber(), destinationFloor);
            floor.addPerson(person);
            System.out.println("Generated Person " + person.getId() + " on Floor " + floor.getFloorNumber() + " with destination Floor " + person.getDestinationFloor());
        }
    }




}
