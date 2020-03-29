import model.Administrator;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class AdministratorRunner {
    public static void main(String[] args) {
        Administrator administrator = new Administrator("admin",
                "exchange1");
        try {
            administrator.init();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }
}
