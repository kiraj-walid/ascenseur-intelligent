import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class Main {

    public static void main(String[] args) {
        Floor floor0 = new Floor(0);
        Floor floor1 = new Floor(1);
        Floor floor2 = new Floor(2);
        Floor floor3 = new Floor(3);
        Floor floor4 = new Floor(4);

        ArrayList<Floor> floors = new ArrayList<>();
        floors.add(floor0);
        floors.add(floor1);
        floors.add(floor2);
        floors.add(floor3);
        floors.add(floor4);

        RandomPersonGenerator generator = new RandomPersonGenerator(5);

        generator.setFloors(floors);

        Ascenseur ascenseur = new Ascenseur();

        ascenseur.setFloors(floors);

        Thread generatorThread = new Thread(generator);
        Thread ascenseurThread = new Thread(ascenseur);

        generatorThread.start();
        try {
            sleep(1000); // Wait for a moment to allow the generator to create some people before starting the elevator
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ascenseurThread.start();
    }
}
