import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class Ascenseur implements Runnable {
    @Override
    public void run() {
        while (true) {
            logic();
            try {
                sleep(2000);
            }
            catch (InterruptedException e) {
                System.out.println("Ascenseur thread interrupted. Stopping elevator.");
                throw new RuntimeException(e);
            }
        }
    }

    private int currentFloorNumber;
    private List<Person> persons;
    private List<Floor> floors;
    private Direction direction;

    public Ascenseur() {
        this.currentFloorNumber = 0; // Assuming the elevator starts at the ground floor
        this.persons = new ArrayList<>();
        this.floors = new ArrayList<>();
        this.direction = Direction.UP; // Initial direction can be set to UP or DOWN
    }


    public int getCurrentFloorNumber() {
        return currentFloorNumber;
    }
    public List<Person> getPersons() {
        return persons;
    }
    public List<Floor> getFloors() {
        return floors;
    }
    private Floor getCurrentFloor() {
        for (Floor floor : floors) {
            if (floor.getFloorNumber() == currentFloorNumber) {
                return floor;
            }
        }
        return null; // Return null if the current floor is not found in the list
    }
    public Direction getDirection() {
        return direction;
    }

    public void setCurrentFloorNumber(int floorNumber) {
            this.currentFloorNumber = floorNumber;
    }
    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
    public void setFloors(List<Floor> floors) {
        this.floors = floors;
    }
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void logic() {
        System.out.println("Elevator is at floor " + currentFloorNumber + " with " + persons.size() + " people inside.its going "+ this.getDirection().toString()+ " their IDs are : " + persons.stream().map(Person::getId).toList());

        for (int i = 0; i < persons.size(); i++) {
            Person person = persons.get(i);
            if (person.getDestinationFloor() == currentFloorNumber) {
                // Person has reached their destination floor, remove them from the elevator
                persons.remove(person);
                i--;
                System.out.println("Person " + person.getId() + " has exited the elevator at floor " + currentFloorNumber);
            }
        }
        if (this.currentFloorNumber == 0) {
            this.direction = Direction.UP;
        } else if (this.currentFloorNumber == 4) {
            this.direction = Direction.DOWN;
        }
        if (this.direction == Direction.UP) {
            List<Person> queueFloors = this.getCurrentFloor().getWaitingPeopleUp();
            Person person;
            for(int i = 0; i < queueFloors.size(); i++) {
                person = queueFloors.get(i);
                if(this.persons.size() < 4) { // Assuming the elevator has a capacity of 4 people
                    this.persons.add(person);
                    this.getCurrentFloor().removePerson(person);
                    i--;
                    System.out.println("Person " + person.getId() + " has entered the elevator at floor " + currentFloorNumber);
                }
                else break;
            }
        }
        if (this.direction == Direction.DOWN) {
            List<Person> queueFloors = this.getCurrentFloor().getWaitingPeopleDown();
            Person person;
            for(int i = 0; i < queueFloors.size(); i++) {
                person = queueFloors.get(i);
                if(this.persons.size() < 4) { // Assuming the elevator has a capacity of 4 people
                    this.persons.add(person);
                    this.getCurrentFloor().removePerson(person);
                    i--;
                    System.out.println("Person " + person.getId() + " has entered the elevator at floor " + currentFloorNumber);
                }
                else break;
            }
        }
        try {
            sleep(1000); // Simulate time delay for processing logic
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (this.direction == Direction.UP) {
            this.setCurrentFloorNumber(this.getCurrentFloor().getFloorNumber() + 1);
        }
        if (this.direction == Direction.DOWN) {
            this.setCurrentFloorNumber(this.getCurrentFloor().getFloorNumber() - 1);
        }
    }



}


enum Direction {
    UP,
    DOWN
}
