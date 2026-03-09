
import java.util.*;

class ParkingSlot {
    String licensePlate;
    long entryTime;
    boolean deleted;

    ParkingSlot(String plate) {
        this.licensePlate = plate;
        this.entryTime = System.currentTimeMillis();
        this.deleted = false;
    }
}

public class ParkingLot {

    private static final int SIZE = 500;
    private ParkingSlot[] table = new ParkingSlot[SIZE];

    private int occupied = 0;
    private int totalProbes = 0;
    private int parkOperations = 0;

    private Map<Integer, Integer> hourlyTraffic = new HashMap<>();

    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % SIZE;
    }

    public void parkVehicle(String licensePlate) {

        int index = hash(licensePlate);
        int probes = 0;

        while (table[index] != null && !table[index].deleted) {
            index = (index + 1) % SIZE;
            probes++;
        }

        table[index] = new ParkingSlot(licensePlate);
        occupied++;

        totalProbes += probes;
        parkOperations++;

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        hourlyTraffic.put(hour, hourlyTraffic.getOrDefault(hour, 0) + 1);

        System.out.println("parkVehicle(\"" + licensePlate + "\") → Assigned spot #" +
                index + " (" + probes + " probes)");
    }

    public void exitVehicle(String licensePlate) {

        int index = hash(licensePlate);

        while (table[index] != null) {

            if (!table[index].deleted &&
                    table[index].licensePlate.equals(licensePlate)) {

                long durationMillis = System.currentTimeMillis() - table[index].entryTime;
                double hours = durationMillis / 3600000.0;

                double fee = hours * 5.5;

                table[index].deleted = true;
                occupied--;

                System.out.printf(
                        "exitVehicle(\"%s\") → Spot #%d freed, Duration: %.2f hours, Fee: $%.2f%n",
                        licensePlate, index, hours, fee);

                return;
            }

            index = (index + 1) % SIZE;
        }

        System.out.println("Vehicle not found.");
    }

    public void getStatistics() {

        double occupancyRate = (occupied * 100.0) / SIZE;

        double avgProbes = parkOperations == 0 ? 0 :
                (double) totalProbes / parkOperations;

        int peakHour = -1;
        int maxTraffic = 0;

        for (Map.Entry<Integer, Integer> entry : hourlyTraffic.entrySet()) {

            if (entry.getValue() > maxTraffic) {
                maxTraffic = entry.getValue();
                peakHour = entry.getKey();
            }
        }

        System.out.printf("Occupancy: %.2f%%, Avg Probes: %.2f, Peak Hour: %d-%d%n",
                occupancyRate, avgProbes, peakHour, peakHour + 1);
    }

    public static void main(String[] args) {

        ParkingLot parking = new ParkingLot();

        parking.parkVehicle("ABC-1234");
        parking.parkVehicle("ABC-1235");
        parking.parkVehicle("XYZ-9999");

        try { Thread.sleep(2000); } catch (Exception e) {}

        parking.exitVehicle("ABC-1234");

        parking.getStatistics();
    }
}