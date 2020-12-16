import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MachineC extends UnicastRemoteObject implements Machine, Notification, Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private int charge = 0;
    private Remote switcher = null;
    private final String dataPath = "./data/";

    public MachineC() throws IOException, NotBoundException, AlreadyBoundException {
        super();
        this.switcher = GlobalConfiguration.switcher;
        this.launch();
    }

    public void launch() throws IOException, AlreadyBoundException {
        /**
         * Add the machine to Switcher machines list
         */

        if (switcher instanceof Controle) {
            this.createDirectory();
            boolean s = ((Controle) switcher).add(this);
            System.out.println("machine added to switcher : " + s);
        }
    }

    public void createDirectory() {
        try {
            Path path = Paths.get(dataPath);
            Files.createDirectory(path);
        } catch (Exception e){
            ;
        }
    }

    public void startConnection(String ip, int port) throws IOException {
        /**
         * Connect the to Client socket
         */

        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public boolean createFile(String filename, byte[] data, String host, int port) { // RemoteException useless ?
        try {
            File file = new File(dataPath+ filename);
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
                this.write(filename, data);
                this.startConnection(host, port);
                this.out.println("File "+ file.getName() + " created");
                return true;
            } else {
                System.out.println("File already exists.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return false;
    }
    public Boolean write(String filename, byte[] data) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(dataPath + filename);
            fileOutputStream.write(data);
            System.out.println(("file writted"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean createFile(String filename) {
        return false;
    }

    @Override
    public void read(String filename, String host, int port) throws IOException { // to change
        /**
         * This method reads the file 'filename' and return the result to client with socket
         * method of Machine interface
         */

        this.charge++;
        InputStream read = new BufferedInputStream(new FileInputStream(dataPath + filename));
        this.startConnection(host, port);
        this.out.println(new String(read.readAllBytes()));
        this.charge--;
    }

    @Override
    public void write(String filename, byte[] data, String host, int port) { // to change
        /**
         * This method writes the file 'filename' and returns the result to client with socket
         * method of machine interface
         */

        this.charge++;
        try {
            this.write(filename, data);
            this.startConnection(host, port);
            this.out.println("file " + filename + " modified");
        } catch (Exception ae) {
            ae.printStackTrace();
        }
        this.charge--;
    }


    @Override
    public byte[] getContentFile(String filename) throws RemoteException {
        String filePath = dataPath + filename;
        byte[] content = new byte[0];
        try {
            content =  Files.readAllBytes( Paths.get(filePath) ) ;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    @Override
    public boolean add(String filename, byte[] contentFile) throws IOException, RemoteException {
        return this.write(filename, contentFile);
    }

    @Override
    public Boolean alive() { // to change (why wouldn't be it alive ?)
        /**
         * This method notifies if it is a life
         */

        return true;
    }

    public void checkOut() throws RemoteException {
        ((Controle) switcher).remove(this);
    }

    @Override
    public int Charge() {
        return this.charge;
    }

    @Override
    public void run() {
        /**
         * Create a run methode for give machine charge to switcher
         */

        while (true) {
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                ((Controle)switcher).writeCharge(this, this.charge);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(); // default port is 1099

            MachineC machineC = new MachineC();

            new Thread(machineC).start();

            System.out.println("Machine" + " is running ...");

        } catch (AlreadyBoundException | IOException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}

// method to synchro each file of each server :
// switcher has a list of file name that a server (already in the system) has.
// when we add a new server, we copy past each file of the first server find by the switcher into the new server
// (we do this with the first server find because all the files are supposed to be the same for all servers)