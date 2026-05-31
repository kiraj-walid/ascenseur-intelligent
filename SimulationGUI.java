import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Interface Swing pour visualiser la simulation sans modifier la logique existante.
 * Lancez cette classe à la place de Main pour afficher l'interface graphique.
 */
public class SimulationGUI extends JFrame {

    private static final int FLOOR_COUNT = 5;
    private static final int ELEVATOR_CAPACITY = 4;
    private static final int REFRESH_MS = 300;

    private final Ascenseur ascenseur;
    private final List<Floor> floors;
    private final BuildingPanel buildingPanel;

    public SimulationGUI(Ascenseur ascenseur, List<Floor> floors) {
        this.ascenseur = ascenseur;
        this.floors = floors;

        setTitle("Simulation Ascenseur — Immeuble 5 étages");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        buildingPanel = new BuildingPanel();
        add(buildingPanel, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        statusBar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        statusBar.add(new JLabel("Étages 0 → 4  |  Capacité cabine : " + ELEVATOR_CAPACITY));
        statusBar.add(new JLabel("■ Montée (file UP)   ■ Descente (file DOWN)   ■ En cabine"));
        add(statusBar, BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(920, 620));
        setLocationRelativeTo(null);

        Timer refreshTimer = new Timer(REFRESH_MS, e -> buildingPanel.repaint());
        refreshTimer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<Floor> floors = new ArrayList<>();
            for (int i = 0; i < FLOOR_COUNT; i++) {
                floors.add(new Floor(i));
            }

            RandomPersonGenerator generator = new RandomPersonGenerator(FLOOR_COUNT);
            generator.setFloors(floors);

            Ascenseur ascenseur = new Ascenseur();
            ascenseur.setFloors(floors);

            SimulationGUI gui = new SimulationGUI(ascenseur, floors);
            gui.setVisible(true);

            Thread generatorThread = new Thread(generator);
            Thread ascenseurThread = new Thread(ascenseur);

            generatorThread.start();
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ascenseurThread.start();
        });
    }

    /** Panneau principal : étages + cabine animée */
    private class BuildingPanel extends JPanel {

        private static final int FLOOR_HEIGHT = 100;
        private static final int MARGIN = 24;
        private static final int SHAFT_WIDTH = 140;
        private static final int CABIN_HEIGHT = 88;
        private static final int PERSON_SIZE = 28;

        BuildingPanel() {
            setPreferredSize(new Dimension(880, FLOOR_COUNT * FLOOR_HEIGHT + 2 * MARGIN + 40));
            setBackground(new Color(245, 247, 250));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int buildingLeft = MARGIN;
            int buildingWidth = w - MARGIN * 2 - SHAFT_WIDTH - 16;
            int shaftX = buildingLeft + buildingWidth + 16;
            int topY = MARGIN + 28;

            drawTitle(g2, w);

            Snapshot snap = captureSnapshot();

            for (int floorNum = FLOOR_COUNT - 1; floorNum >= 0; floorNum--) {
                int rowIndex = FLOOR_COUNT - 1 - floorNum;
                int y = topY + rowIndex * FLOOR_HEIGHT;
                Floor floor = floors.get(floorNum);
                FloorQueues queues = snap.queuesByFloor.get(floorNum);

                drawFloorRow(g2, floor, queues, buildingLeft, buildingWidth, y);
            }

            drawShaft(g2, shaftX, topY, snap);
            g2.dispose();
        }

        private void drawTitle(Graphics2D g2, int w) {
            g2.setColor(new Color(40, 44, 52));
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
            String title = "Immeuble — simulation en temps réel";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(title, (w - fm.stringWidth(title)) / 2, MARGIN);
        }

        private void drawFloorRow(Graphics2D g2, Floor floor, FloorQueues queues,
                                  int x, int width, int y) {
            int floorNum = floor.getFloorNumber();

            g2.setColor(new Color(220, 224, 232));
            g2.fillRoundRect(x, y, width, FLOOR_HEIGHT - 8, 12, 12);
            g2.setColor(new Color(180, 186, 198));
            g2.drawRoundRect(x, y, width, FLOOR_HEIGHT - 8, 12, 12);

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
            g2.setColor(new Color(50, 54, 62));
            g2.drawString("Étage " + floorNum, x + 12, y + 22);

            int queueY = y + 36;
            int half = (width - 100) / 2;

            drawQueueLabel(g2, "↑ Montée", x + 12, queueY - 4, new Color(30, 100, 180));
            drawPersonQueue(g2, queues.up, x + 12, queueY, half - 8, true);

            drawQueueLabel(g2, "↓ Descente", x + width / 2 + 8, queueY - 4, new Color(180, 70, 30));
            drawPersonQueue(g2, queues.down, x + width / 2 + 8, queueY, half - 8, false);
        }

        private void drawQueueLabel(Graphics2D g2, String text, int x, int y, Color c) {
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 11f));
            g2.setColor(c);
            g2.drawString(text, x, y);
        }

        private void drawPersonQueue(Graphics2D g2, List<Person> people, int x, int y, int maxWidth, boolean goingUp) {
            int px = x;
            for (Person p : people) {
                if (px + PERSON_SIZE > x + maxWidth) break;
                drawPersonIcon(g2, px, y, p, goingUp ? new Color(66, 133, 244) : new Color(234, 120, 50), false);
                px += PERSON_SIZE + 6;
            }
            if (people.isEmpty()) {
                g2.setFont(g2.getFont().deriveFont(Font.ITALIC, 10f));
                g2.setColor(new Color(150, 155, 165));
                g2.drawString("(vide)", x, y + 20);
            }
        }

        private void drawShaft(Graphics2D g2, int shaftX, int topY, Snapshot snap) {
            int shaftHeight = FLOOR_COUNT * FLOOR_HEIGHT;
            g2.setColor(new Color(200, 205, 215));
            g2.fillRect(shaftX, topY, SHAFT_WIDTH, shaftHeight);
            g2.setColor(new Color(160, 168, 180));
            g2.drawRect(shaftX, topY, SHAFT_WIDTH, shaftHeight);

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
            g2.setColor(new Color(60, 64, 72));
            g2.drawString("Ascenseur", shaftX + 28, topY - 6);

            int rowIndex = FLOOR_COUNT - 1 - snap.currentFloor;
            int cabinY = topY + rowIndex * FLOOR_HEIGHT + (FLOOR_HEIGHT - CABIN_HEIGHT) / 2 - 4;

            g2.setColor(new Color(90, 96, 110));
            g2.fillRoundRect(shaftX + 8, cabinY, SHAFT_WIDTH - 16, CABIN_HEIGHT, 10, 10);
            g2.setColor(new Color(55, 60, 72));
            g2.drawRoundRect(shaftX + 8, cabinY, SHAFT_WIDTH - 16, CABIN_HEIGHT, 10, 10);

            drawDirectionArrow(g2, shaftX + SHAFT_WIDTH / 2, cabinY - 18, snap.direction);

            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 11f));
            String cap = snap.cabin.size() + "/" + ELEVATOR_CAPACITY;
            g2.drawString(cap, shaftX + SHAFT_WIDTH / 2 - 12, cabinY + 14);

            int px = shaftX + 14;
            int py = cabinY + 28;
            for (Person p : snap.cabin) {
                drawPersonIcon(g2, px, py, p, new Color(46, 160, 90), true);
                px += PERSON_SIZE + 4;
                if (px > shaftX + SHAFT_WIDTH - PERSON_SIZE - 10) {
                    px = shaftX + 14;
                    py += PERSON_SIZE + 4;
                }
            }
        }

        private void drawDirectionArrow(Graphics2D g2, int cx, int y, Direction direction) {
            g2.setStroke(new BasicStroke(2.5f));
            if (direction == Direction.UP) {
                g2.setColor(new Color(30, 130, 220));
                int[] xs = {cx, cx - 10, cx + 10};
                int[] ys = {y, y + 14, y + 14};
                g2.fillPolygon(xs, ys, 3);
                g2.drawString("HAUT", cx - 18, y + 28);
            } else {
                g2.setColor(new Color(220, 90, 40));
                int[] xs = {cx, cx - 10, cx + 10};
                int[] ys = {y + 14, y, y};
                g2.fillPolygon(xs, ys, 3);
                g2.drawString("BAS", cx - 14, y + 28);
            }
        }

        private void drawPersonIcon(Graphics2D g2, int x, int y, Person p, Color fill, boolean inCabin) {
            g2.setColor(fill);
            g2.fillOval(x, y, PERSON_SIZE, PERSON_SIZE);
            g2.setColor(fill.darker());
            g2.drawOval(x, y, PERSON_SIZE, PERSON_SIZE);

            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 9f));
            String label = inCabin ? String.valueOf(p.getId()) : "P" + p.getId();
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, x + (PERSON_SIZE - fm.stringWidth(label)) / 2, y + 12);

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 8f));
            String dest = "→" + p.getDestinationFloor();
            g2.drawString(dest, x + 2, y + PERSON_SIZE + 10);
        }

        private Snapshot captureSnapshot() {
            Snapshot snap = new Snapshot();
            snap.currentFloor = ascenseur.getCurrentFloorNumber();
            snap.direction = ascenseur.getDirection();

            try {
                snap.cabin = new ArrayList<>(ascenseur.getPersons());
            } catch (Exception ex) {
                snap.cabin = new ArrayList<>();
            }

            for (Floor floor : floors) {
                FloorQueues q = new FloorQueues();
                synchronized (floor) {
                    q.up = new ArrayList<>(floor.getWaitingPeopleUp());
                    q.down = new ArrayList<>(floor.getWaitingPeopleDown());
                }
                snap.queuesByFloor.put(floor.getFloorNumber(), q);
            }
            return snap;
        }
    }

    private static class Snapshot {
        int currentFloor;
        Direction direction;
        List<Person> cabin = new ArrayList<>();
        final java.util.Map<Integer, FloorQueues> queuesByFloor = new java.util.HashMap<>();
    }

    private static class FloorQueues {
        List<Person> up = new ArrayList<>();
        List<Person> down = new ArrayList<>();
    }
}
