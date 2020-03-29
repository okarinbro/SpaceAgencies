import model.Agency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class AgencyRunner {
    public static void main(String[] args) {
        System.out.println("Type agency name: ");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String agencyName = br.readLine();
            Agency agency = new Agency(agencyName, "exchange1", "adminExchange");
            agency.init();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
