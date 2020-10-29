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
    private final String dataPath = "/root/switcher_rmi_docker/data/";

    public MachineC() throws IOException, NotBoundException, AlreadyBoundException {
        super();
        this.switcher = GlobalConfiguration.switcher;
        this.launch();
    }

    // =============================================================================================================

    public void startConnection(String ip, int port) throws IOException {
        /**
         * Connect the to Client socket
         */

        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    // =============================================================================================================

    public boolean createFile(String filename, byte[] data, String host, int port) { // RemoteException useless ?
        try {
            File file = new File(dataPath+ filename);
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
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

    // =============================================================================================================

    @Override
    public boolean createFile(String filename) {
        return false;
    }

    // =============================================================================================================

    @Override
    public void read(String filename, String host, int port) throws IOException { // to change
        /**
         * This method reads the file 'filename' and return the result to client with socket
         * method of Machine interface
         */

        this.charge++;
        InputStream read = new BufferedInputStream(new FileInputStream(dataPath));
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
            FileOutputStream fileOutputStream = new FileOutputStream(dataPath +filename);
            fileOutputStream.write(data);
            this.startConnection(host, port);
            this.out.println("file " + filename + " modified");
        } catch (Exception ae) {
            ae.printStackTrace();
        }
        this.charge--;
    }

    // =============================================================================================================

    public void launch() throws IOException, AlreadyBoundException {
        /**
         * Add the machine to Switcher machines list
         */

        if (switcher instanceof Controle) {
            boolean s = ((Controle) switcher).add(this);
            this.createDirectory();
            System.out.println(s);
        }
    }

    // =============================================================================================================
    @Override
    public Boolean alive() { // to change (why wouldn't be it alive ?)
        /**
         * This method notifies if it is a life
         */

        return true;
    }

    public void createDirectory() {
        try {
            Path path = Paths.get(dataPath);
            Files.createDirectory(path);
        } catch (Exception e){
            ;
        }
    }

    // =============================================================================================================

    public void checkOut() throws RemoteException {
        ((Controle) switcher).remove(this);
    }

    public static void copy(File pathOfFileToCopy, File pathOfFileWhereToPaste) throws IOException { // amybe should use the Apache IO method
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(pathOfFileToCopy);
            outputStream = new FileOutputStream(pathOfFileWhereToPaste); // buffer size 1K
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            inputStream.close();
            outputStream.close();
        }
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