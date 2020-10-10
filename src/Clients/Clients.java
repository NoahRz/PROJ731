import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.*;

public class Clients {
    public static <Information> void main(String[] args) {
        System.out.println("Lancement du client");

        try {
            Remote r = Naming.lookup("rmi://localhost:1099/Aiguilleur");

            if (r instanceof Machine) {
                byte[] s = ((Machine) r).lecture("ressource_1.txt");
                System.out.println("chaine renvoyee = " + new String(s));
            }

        } catch (NotBoundException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

