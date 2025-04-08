import java.util.ArrayList;
import java.util.List;

public class AntennaLauncher {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java AntennaLauncher <antenna-name> [neighbor1 neighbor2 ...]");
            return;
        }

        String antennaName = args[0];
        List<String> neighbors = new ArrayList<>();

        for (int i = 1; i < args.length; i++) {
            neighbors.add(args[i].trim());
        }

        System.out.println("Starting Antenna " + antennaName);
        System.out.println("Connected to neighbors: " + neighbors);

        // Create antenna with empty initial user list
        new Antenna(antennaName, new ArrayList<>(), neighbors);

        // Keep the application running
        Thread.currentThread().join();
    }
}
