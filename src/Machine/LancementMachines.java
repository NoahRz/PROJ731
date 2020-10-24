import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;

public class LancementMachines {
    public static void main(String[] args) throws IOException, NotBoundException, AlreadyBoundException {
        MachineC machine1 = new MachineC("machine1");
        MachineC machine2 = new MachineC("machine2");
    }
}
