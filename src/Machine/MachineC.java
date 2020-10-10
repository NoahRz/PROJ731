import java.io.*;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MachineC extends UnicastRemoteObject implements Machine, Notification {

    String name = null;

    public MachineC(String name) throws RemoteException {
        super();
        this.name = name;
    }


    @Override
    public byte[] read(String name) throws IOException {
        InputStream read = new BufferedInputStream(new FileInputStream("data//" + name));
        return read.readAllBytes();
    }

    @Override
    public Boolean write(String name, byte[] data) throws IOException {
        try {
            FileOutputStream sortie = new FileOutputStream(name);
            sortie.write(data);
            return true;
        } catch (Exception ae){
            return false;
        }
    }

    public String getName() {
        return name;
    }


    public void launch() throws IOException, NotBoundException, AlreadyBoundException {
        Remote r = Naming.lookup("rmi://localhost:1099/Switcher");
        //Remote r = Naming.lookup("switcher");

        if (r instanceof Controle) {
            boolean s = ((Controle) r).add(name, this);
            System.out.println(s);
        }
    }

    public void checkOut() throws RemoteException, NotBoundException, MalformedURLException {
        Controle r = (Controle) Naming.lookup("rmi://localhost:1099/Switcher");
        r.remove(this.getName());
    }

    @Override
    public Boolean alive() throws IOException {
        return true;
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(); // default port is 1099

            MachineC machineC = new MachineC(args[0]);

            machineC.launch();

            System.out.println("Starting " + args[0] + " : ...");
        } catch (IOException | NotBoundException | AlreadyBoundException e) {
            e.printStackTrace();
        }
    }
}
