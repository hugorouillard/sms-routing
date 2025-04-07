import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String antennaName = args[0];

        Map<String, List<String>> userMap = new HashMap<>() {{
            put("A", List.of("alice"));
            put("B", List.of("bob"));
            put("C", List.of("charlie"));
        }};

        Map<String, List<String>> neighborMap = new HashMap<>() {{
            put("A", List.of("B"));
            put("B", List.of("A", "C"));
            put("C", List.of("B"));
        }};

        Antenna node = new Antenna(antennaName, userMap.get(antennaName), neighborMap.get(antennaName));

        if (antennaName.equals("A")) {
            User alice = new User("alice", "A");
            Thread.sleep(3000); // let system stabilize
            alice.sendSMS("charlie", "hello from alice from antenna A");
            Thread.sleep(3000);
            alice.moveTo("C");
            Thread.sleep(3000);
            alice.sendSMS("bob", "hello from alice from antenna C");
        }
    }
}
