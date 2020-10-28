import java.io.*;
import java.net.MalformedURLException;
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
    String name = null;
    private int charge = 0;
    private Remote switcher = null;
    public String getName() {
        return name;
    }

    public MachineC(String name) throws IOException, NotBoundException, AlreadyBoundException {
        super();
        this.switcher = GlobalConfiguration.switcher;
        this.name = name;
        this.launch();
    }
    // =============================================================================================================


    // =============================================================================================================
    public void startConnection(String ip, int port) throws IOException {
        /**
         * Connect the to Client socket
         */
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
    }
    // =============================================================================================================


    // =============================================================================================================

    @Override
    public boolean createFile(String filename) throws RemoteException { // RemoteException useless ?
        try {
            File file = new File("/root/switcher_rmi_docker/data/" + filename);
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
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


    // =============================================================================================================
    @Override
    public void read(String name, String host, int port) throws IOException {
        /**
         * This method read the file 'name' and return the result to client with socket
         * method of Machine interface
         */

        this.charge++;
        //InputStream read = new BufferedInputStream(new FileInputStream(".//src//data//" + name));
        InputStream read = new BufferedInputStream(new FileInputStream("/root/switcher_rmi_docker/data"));
        this.startConnection(host, port);
        this.out.println(new String(read.readAllBytes()));
        this.charge--;
    }

    @Override
    public void write(String name, byte[] data, String host, int port) throws IOException {
        /**
         * This method write the file 'name' and return the result to client with socket
         * method of machine interface
         */
        this.charge++;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("./src/data/" +name);
            fileOutputStream.write(data);
            this.startConnection(host, port);
            this.out.println("Modification faite");
        } catch (Exception ae) {
            ae.printStackTrace();
        }
        this.charge--;
    }

    // =============================================================================================================

    public void launch() throws IOException, NotBoundException, AlreadyBoundException {
        /**
         * Add the machine to Switcher registry
         */

        if (switcher instanceof Controle) {
            boolean s = ((Controle) switcher).add(name , this);
            this.createDirectory();
            System.out.println(s);
        }

        //System.out.println(this.createFile("file1.txt"));

    }

    // =============================================================================================================
    @Override
    public Boolean alive() throws IOException {
        /**
         * This method notify if they are a life
         */
        return true;
    }

    public void createDirectory() throws IOException {
        try {
            Path path = Paths.get("/root/switcher_rmi_docker/data");
            Files.createDirectory(path);
        } catch (Exception e){
            ;
        }
    }
    // =============================================================================================================


    public void checkOut() throws RemoteException, NotBoundException, MalformedURLException {
            ((Controle) switcher).remove(this.getName());
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
    public int Charge() throws IOException {
        return this.charge;
    }

    @Override
    public void run() {
        /**
         * Creat a run methode for give machine charge to switcher
         */


        while (true) {
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                ((Controle)switcher).writeCharge(this.name, this.charge);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        try {
            //String machineName = args[0];
            Registry registry = LocateRegistry.getRegistry(); // default port is 1099

            MachineC machineC = new MachineC("machine1");
            //MachineC machineC = new MachineC(machineName);
            machineC.launch();

            new Thread(machineC).start();

            System.out.println("machine1" + " is running ...");

        } catch (AlreadyBoundException | IOException | NotBoundException e) {
            e.printStackTrace();
        }


    }
}


// method to synchro each file of each server :
// switcher has a list of file name that a server (already in the system) has.
// when we add a new server, we copy past each file of the first server find by the switcher into the new server
// (we do this with the first server find because all the files are supposed to be the same for all servers)