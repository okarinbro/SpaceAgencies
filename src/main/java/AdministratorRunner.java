import model.Administrator;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class AdministratorRunner {
    public static void main(String[] args) {
        try {
            Administrator administrator = new Administrator("adminExchange",
                    "exchange1");
            administrator.init();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }
}
