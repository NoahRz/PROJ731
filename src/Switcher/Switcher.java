import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;


/**
 * The Switcher class receives requests from clients and distributes them among machines.
 * Thus it is server for client and client for machine.
 */
public class Switcher extends UnicastRemoteObject implements Machine, Controle {

    private int turn = 0;
    private ArrayList<Remote> machines = new ArrayList<Remote>();
    private ArrayList<String> filenames = new ArrayList<String>();


    protected Switcher() throws RemoteException {
        super();
    }

    @Override
    public boolean createFile(String filename, byte[] data, String host, int port) {
        return false;
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
    public void read(String filename,String host, int port) throws IOException, NotBoundException, InterruptedException {
        /**
         * Read 'filename' document.
         * This method is distributed with RMI, 'Machine' is a remote object
         * Method of Machine interface
         */

        Machine machine = (Machine) this.machineAlive(1);
        machine.read(filename, host, port);
    }

    @Override
    public void write(String filename, byte[] data, String host, int port) throws IOException, NotBoundException {
        /**
         * Writes in the document 'filename'
         * This method is distributed with RMI, 'Machine' is a remote object
         * Method of Machine interface
         */

        Machine machine = (Machine) this.machineAlive(1);
        if(this.filenames.contains(filename)) {
            machine.write(filename, data, host, port);
        }else {
            machine.createFile(filename, data, host, port);
            this.filenames.add(filename);
        }
    }

    // =============================================================================================================

    @Override
    public boolean add(Remote machine) throws RemoteException, AlreadyBoundException {
        /**
         * This method adds a remote object (Machine) in the list machines
         * Methode of control
         */

        /*if (this.machines.length !=0) {
            for (String filename : this.filenames) {
                File file = this.machines.get(0).getFile(filename);
                machine.add(file);
            }
        }*/
        this.machines.add(machine);
        return true;
    }


    @Override
    public boolean remove(Machine machine) {
        /**
         * This method removes a remote object (Machine) from the list machines
         * Methode of control
         */

        try{
            this.machines.remove(machine);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void writeCharge(Remote remote, int charge) throws IOException { // to change
        /**
         * //Change a charge of Machine
         */

        Notification machine = (Notification) remote;
        machine.Charge();
    }

    // =============================================================================================================

    @Override
    public void createFileInEachMachine(String filename) throws RemoteException, NotBoundException {
        for (Remote machine : this.machines){
            Machine machineC = (Machine) machine;
            machineC.createFile(filename);
        }
    }

    // =============================================================================================================

    public Notification roundRobin() throws RemoteException {
        /**
         * This method is used to distribute the charge between machines
         */

        int length = this.machines.size();
        this.turn = (this.turn+1)%length;
        return (Notification) this.machines.get(this.turn);

    }

    public Notification lessCharges() throws IOException {
        /**
         * This method finds the machine which has the lowest charge
         */

        Remote minRemote = machines.get(0);
        Notification minMachine = (Notification) minRemote;
        int minCharge = minMachine.Charge();
        for (int i = 1; i<this.machines.size(); i++){
            Remote remote = machines.get(i);
            Notification machine = (Notification) remote;
            if  (machine.Charge()< minCharge){
                minMachine = machine;
                minCharge = minMachine.Charge();
            }
        }
        return minMachine;
    }

    public Notification machineAlive(int methode) throws IOException { // to change (don't understand the purpose)
        /**
         *  At first they execute a distribute algorithme
         *  If methode = 1 ==> roundRobin
         *  If methode = 2 ==> lessCharges
         *  For the machine used this method checks whether it is alive, otherwise the machine is removed and the method is rerun.
        */

        if (methode == 1) { // Round robbin
            Notification machine = this.roundRobin();
            if (machine.alive()) {
                return machine;
            }
        }
        if (methode == 2) {
            Notification machine = this.lessCharges();
            if (machine.alive()){
                return machine;
            }
        }
        return null;
    }

    // ---------------------------------------------------------------------------------------------

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry();

            Switcher switcher = new Switcher();

            String url = "rmi:/localhost/Switcher";
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