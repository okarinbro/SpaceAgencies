import com.google.common.collect.ImmutableList;
import model.ServiceType;
import model.Shipper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class ShipperRunner {

    public static void main(String[] args) {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            ServiceType firstService;
            ServiceType secondService;
            while (true) {
                System.out.println("type: ct (cargo), pt (person), st (satellite)");
                System.out.print(">");
                firstService = ServiceType.fromString(br.readLine());
                System.out.print(">");
                secondService = ServiceType.fromString(br.readLine());
                if (areArgumentsValid(firstService, secondService)) {
                    break;
                } else {
                    System.out.println("Arguments are invalid");
                }
            }
            Shipper shipper = new Shipper(ImmutableList.of(firstService, secondService), "exchange1", "adminExchange");
            shipper.init();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private static boolean areArgumentsValid(ServiceType firstService, ServiceType secondService) {
        return !firstService.equals(secondService) && !firstService.equals(ServiceType.Unknown) && !secondService.equals(ServiceType.Unknown);
    }
}
