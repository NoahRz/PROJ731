import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class GlobalConfiguration {
    public static Remote switcher;
    static {
        try {
            switcher = Naming.lookup("rmi://localhost:1099/Switcher");

        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }
}
