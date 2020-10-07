import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class LancementMachines {
    public static void main(String[] args) throws IOException, NotBoundException, AlreadyBoundException {
        MachineC m1 = new MachineC("m1");
        MachineC m2 = new MachineC("m2");

        m1.lancement();
        m2.lancement();
    }
}
