import java.util.ArrayList;
import java.util.List;

public class Floor {
    private int floorNumber;
    private List<Person> waitingPeopleUp;
    private List<Person> waitingPeopleDown;

    private boolean editing = false; // Flag to indicate if the floor is being edited

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.waitingPeopleUp = new ArrayList<>();
        this.waitingPeopleDown = new ArrayList<>();
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public synchronized List<Person> getWaitingPeopleUp() {
        return waitingPeopleUp;
    }
    public synchronized List<Person> getWaitingPeopleDown() {
        return waitingPeopleDown;
    }
    public synchronized void addPerson(Person person) {
        while (editing) {
            try {
                wait(); // Wait until the person is removed from the list
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        editing = true; // Set the editing flag to true while modifying the list
        if(person.getDestinationFloor() > this.floorNumber) {
            waitingPeopleUp.add(person);
        } else {
            waitingPeopleDown.add(person);
        }
        editing = false;
        notifyAll();
    }

    public synchronized void removePerson(Person person) {
        while (editing) {
            try {
                wait(); // Wait until the person is added to the list
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        editing = true; // Set the editing flag to true while modifying the list
        if(person.getDestinationFloor() > this.floorNumber) {
            waitingPeopleUp.remove(person);
        } else {
            waitingPeopleDown.remove(person);
        }
        editing = false;
        notifyAll();
    }
}
