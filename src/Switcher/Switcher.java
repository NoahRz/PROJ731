import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * The Switcher class receives requests from clients and distributes them among machines.
 * Thus it is server for client and client for machine.
 */
public class Switcher extends UnicastRemoteObject implements Machine, Controle {
    // Variable
    private HashMap<String, Remote> privateRegistry = new HashMap<>();
//    private Registry registry = null;
    private int turn = 1;
    private ArrayList<String> filenames = new ArrayList<String>();
    private HashMap<String, Integer> fileCharge = new HashMap<String, Integer>();


    protected Switcher(Registry registry) throws RemoteException {
        super();
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



    // =============================================================================================================
    @Override
    public void read(String name,String host, int port) throws IOException, NotBoundException, InterruptedException {
        /**
         * Read 'name' document document.
         * This method is distributed with RMI, 'Machine' is a remote object
         * Method of Machine interface
         */

        String mach = this.machineAlive(1);
        Machine rem = (Machine) this.privateRegistry.get(mach);
        rem.read(name,host,port);
    }


    @Override
    public void write(String name, byte[] data, String host, int port) throws IOException, NotBoundException {
        /**
         * Write in the document 'name'
         * This method is distributed with RMI, 'Machine' is a remote object
         * Method of Machine interface
         */
        String mach = this.machineAlive(1);
        Machine rem = (Machine) this.privateRegistry.get(mach);
        rem.write(name,data, host, port);
        ;
    }

    // =============================================================================================================



    // =============================================================================================================
    @Override
    public boolean add(String url, Machine machine) throws RemoteException, AlreadyBoundException {
        /**
         * This method add a remote object (Machine) in this.registre
         * Methode of control
         */
        this.privateRegistry.put(url, machine);
//            this.registry.rebind("rmi://localhost:1099//"  + url, machine);
        this.fileCharge.put(url,0);
        return true;
    }


    @Override
    public boolean remove(String url) throws RemoteException {
        /**
         * This method remove a remote object (Machine) of this.registre
         * Methode of control
         */
        try{
            this.privateRegistry.remove(url);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public void writeCharge(String name, int charge) throws RemoteException {
        /**
         * Change a charge of Machine
         */
        this.fileCharge.replace(name,charge);
    }
    // =============================================================================================================




    @Override
    public void createFileInEachMachine(String filename) throws RemoteException, NotBoundException {
        ArrayList<String> remoteObjectNames = new ArrayList<>(this.privateRegistry.keySet());

        for (String remoteObjectName : remoteObjectNames) {
            Remote remoteObject = this.privateRegistry.get(remoteObjectName);
            if (remoteObject instanceof Notification) { // it it is a machine
                MachineC machineC = (MachineC) remoteObject;
                machineC.createFile(filename);
            }
        }
    }


    // =============================================================================================================
    public String roundRobin() throws RemoteException {
        /**
         * This method is used for distribute the charge between the machines
         */
        ArrayList<String> nameListe = new ArrayList<>(this.privateRegistry.keySet());
        int length = nameListe.size();
        int a = this.turn%(length-1);
        this.turn++;
        return nameListe.get(a);


    }


    public String lessCharges() throws IOException, NotBoundException {
        /**
         * This method find the machine with the less charged.
         */
        String name = "";
        int min = -1;
        for(String i : this.privateRegistry.keySet()){
            Notification charged = (Notification) this.privateRegistry.get(i);
            int valMachine = charged.Charge();
            if (valMachine > min){
                min = valMachine;
                if(min == 0){
                    return i;
                }
                name = i;
            }
        }

        return name;
    }


    public String machineAlive(int methode) throws IOException, NotBoundException {
        /**
         *  At first they execute a distribute algorithme
         *  If methode = 1 ==> roundRobin
         *  If methode = 2 ==> lessCharges
         *  For the machine used this method checks whether it is alive, otherwise the machine is remove and the method is rerun.
        */
        if (methode == 1) { // Round robbin
            String url = this.roundRobin();
            try {
                Notification rem = (Notification) this.privateRegistry.get(url);
                rem.alive();
                return url;
            } catch (IOException e) {
                this.privateRegistry.get(url);
                url = this.roundRobin();
                this.machineAlive(methode);
                return url;
            }
        } else if ( methode == 2) { // according to machine charges
            String url = this.lessCharges();
            try {
                Notification rem = (Notification) this.privateRegistry.get(url);
                rem.alive();
                return url;
            } catch (IOException e) {
                this.privateRegistry.get(url);
                url = this.lessCharges();
                this.machineAlive(methode);
                return url;
            }
        }
        return "Error";
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