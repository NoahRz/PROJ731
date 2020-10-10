import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

/**
 * The Switcher class receives requests from clients and distributes them among machines.
 * Thus it his server for client and client for machine.
 */
public class Switcher extends UnicastRemoteObject implements Machine, Controle {
    private Registry registry = null;
    private int turn = 0;

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
    public byte[] read(String name) throws IOException, NotBoundException, InterruptedException {
//        this.voirRegistre();
//        String mach = this.aTourDeRole();
        String mach = this.machineAlive();
        Machine rem = (Machine) this.registry.lookup(mach);
        byte[] s = rem.read(name);
        return s;
    }

    @Override
    public Boolean write(String name, byte[] data) throws IOException {
        return null;
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

    public String roundRobin() throws RemoteException {
        String[] value = this.registry.list();
        int length = value.length;
        value = Arrays.copyOfRange(value, 0, length-1);
        int a = this.turn%length;
        this.turn++;
        return value[a];
    }

    public String machineAlive() throws IOException, NotBoundException {
        String url = this.roundRobin();
        try {
            Notification rem = (Notification) this.registry.lookup(url);
            rem.alive();
            return url;
        } catch (NotBoundException | IOException e) {
            this.registry.unbind(url);
            url = this.roundRobin();
            this.machineAlive();
            return url;
        }

    }

    // ---------------------------------------------------------------------------------------------
    public static void main(String[] args) {
        try {

            Registry registry = LocateRegistry.createRegistry(1099);

            Switcher switcher = new Switcher(registry);

            String url = "rmi://localhost:1099/Switcher";
            Naming.rebind(url, switcher);

            System.out.println("Starting server ...");
        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }


}
