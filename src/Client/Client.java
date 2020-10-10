import java.io.IOException;
import java.rmi.*;

public class Client {
    public static <Information> void main(String[] args) {
        System.out.println("Starting client");

        try {
            Remote r = Naming.lookup("rmi://localhost:1099/Switcher");

            if (r instanceof Machine) {
                byte[] s = ((Machine) r).read("ressource_1.txt");
                System.out.println("returned String = " + new String(s));
            }

        } catch (NotBoundException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

