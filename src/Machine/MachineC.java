import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class MachineC extends UnicastRemoteObject implements Machine, Notification {
    private Socket clientSocket;
    private PrintWriter out;
    String name = null;
    private int charge = 0;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);


    }
    public MachineC(String name) throws RemoteException {
        super();
        this.name = name;
    }


    @Override
    public void read(String name, String host, int port) throws IOException {
        this.charge++;
        InputStream read = new BufferedInputStream(new FileInputStream(".//src//data//" + name));
        this.startConnection(host, port);
        this.out.println(new String(read.readAllBytes()));
        this.charge--;
    }

    @Override
    public void write(String name, byte[] data) throws IOException {
        this.charge++;
        try {
            System.out.println(InetAddress.getLocalHost().getHostAddress());
            FileOutputStream sortie = new FileOutputStream(name);
            sortie.write(data);
        } catch (Exception ae){
            ae.printStackTrace();
        }
        this.charge--;
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

    @Override
    public int Charge() throws IOException {
        return this.charge;
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
