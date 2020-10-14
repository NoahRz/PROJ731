import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Switcher class receives requests from clients and distributes them among machines.
 * Thus it is server for client and client for machine.
 */
public class Switcher extends UnicastRemoteObject implements Machine, Controle {
    private Registry registry = null;
    private int turn = 1;
    private ArrayList<String> filenames = new ArrayList<String>();

    protected Switcher(Registry registry) throws RemoteException {
        super();
        this.registry = registry;
    }

    private void voirRegistre() throws RemoteException {
        System.out.println("====================================");
        for(String i : this.registry.list()){
            System.out.println(i);
        }
    }

    @Override
    public boolean createFile(String filename) throws RemoteException, NotBoundException {
        if (filenames.contains(filename)){
            return false;
        }else {
            filenames.add(filename);
            this.createFileInEachMachine(filename);
            return true;
        }

    }

    @Override
    public void read(String name,String host, int port) throws IOException, NotBoundException, InterruptedException {
        String mach = this.machineAlive(1);
        Machine rem = (Machine) this.registry.lookup(mach);
        rem.read(name,host,port);
    }

    @Override
    public void write(String name, byte[] data) throws IOException {
        ;
    }

    @Override
    public boolean add(String url, Machine machine) throws RemoteException, AlreadyBoundException {
        try{
            this.registry.rebind("rmi://localhost:1099//"  + url, machine);
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean remove(String url) throws RemoteException {
        try{
            this.registry.unbind(url);
            return true;
        } catch (NotBoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void createFileInEachMachine(String filename) throws RemoteException, NotBoundException {
        String[] remoteObjectNames = registry.list();
        for (String remoteObjectName : remoteObjectNames) {
            Remote remoteObject = this.registry.lookup(remoteObjectName);
            if (remoteObject instanceof Notification) { // it it is a machine
                MachineC machineC = (MachineC) remoteObject;
                machineC.createFile(filename);
            }
        }
    }

    public String roundRobin() throws RemoteException {
        String[] value = this.registry.list(); // WARNING : there is the switcher in the list
        int length = value.length;
        value = Arrays.copyOfRange(value, 0, length-1);
        int a = this.turn%(length-1);
        this.turn++;
        return value[a];
    }


    public String machineAlive(int methode) throws IOException, NotBoundException {
        if (methode == 1) {
            String url = this.roundRobin();
            try {
                Notification rem = (Notification) this.registry.lookup(url);
                rem.alive();
                return url;
            } catch (NotBoundException | IOException e) {
                this.registry.unbind(url);
                url = this.roundRobin();
                this.machineAlive(methode);
                return url;
            }
        } else if ( methode == 2) {
            String url = this.lessCharges();
            try {
                Notification rem = (Notification) this.registry.lookup(url);
                rem.alive();
                return url;
            } catch (NotBoundException | IOException e) {
                this.registry.unbind(url);
                url = this.lessCharges();
                this.machineAlive(methode);
                return url;
            }
        }
        return "Error";
    }

    public String lessCharges() throws IOException, NotBoundException {
        String machineMin = "";
        int chargeMin = 0;
        for(int i = 0; i < this.registry.list().length-1; i++){
            String name = this.registry.list()[i];
            Notification rem = (Notification) this.registry.lookup(name);
            int val = rem.Charge();
            if(val >= chargeMin){
                if (val == 0){
                    return name;
                }
                chargeMin = val;
                machineMin = name;
            }
        }
        return machineMin;
    }

    // ---------------------------------------------------------------------------------------------
    public static void main(String[] args) {
        try {

            Registry registry = LocateRegistry.createRegistry(1099);
            // did this because still got the error : java.rmi.UnmarshalException: error unmarshalling arguments

            Switcher switcher = new Switcher(registry);

            String url = "rmi://localhost:1099/Switcher";
            Naming.rebind(url, switcher);

            System.out.println("Switcher is running ...");

        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
}

// command line (NB: have to be in the src folder):
// javac -d destDir */*.java
// java -classpath destDir -Djava.rmi.server.codebase=file:destDir/ Switcher/Switcher.java -> start the switcher
// java -classpath destDir -Djava.rmi.server.codebase=file:destDir/ Machine/MachineC.java machineN -> start one machine
// java -classpath destDir -Djava.rmi.server.codebase=file:destDir/ Client/Client.java -> start the client