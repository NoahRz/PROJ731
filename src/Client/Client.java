import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.*;

public class Client {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    int port;

    public Client(int port){
        this.port = port;
    }

    public void startListen(int port) throws IOException {
        /**
         * Open port
         */

        serverSocket = new ServerSocket(port);
    }

    public void waitMessage() throws IOException {
        /**
         * Client waits MachineC responce
         */

        clientSocket = this.serverSocket.accept();
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String read(String name){
        /**
         * Method to read a document
         */

        try {
            Remote r = GlobalConfiguration.switcher;
            this.startListen(this.port);
            if (r instanceof Machine) {
                ((Machine) r).read(name, InetAddress.getLocalHost().getHostAddress(), this.port);
            }
            this.waitMessage();
            return this.in.readLine();
        } catch (NotBoundException | IOException | InterruptedException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String write(String name, String data){
        /**
         * Method to write
         */

        System.out.println();
        try {
            Remote r = GlobalConfiguration.switcher;
            this.startListen(this.port);
            if (r instanceof Machine) {
                ((Machine) r).write(name, data.getBytes(), InetAddress.getLocalHost().getHostAddress(), this.port);
            }
            this.waitMessage();
            return this.in.readLine();
        } catch (NotBoundException | IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void main(String[] args) {
        /**
         * Main method
         */

        int port;
        try{
             port = Integer.parseInt(args[0]);
        }catch (Exception e){
             port = 8080;
        }
        System.out.println(port);
        Client client1 = new Client(port);

        //String result = client1.read("ressource_1.txt");
        String result = client1.write("ressource_1.txt", "Bonjour je suis un nouveau texte");
        System.out.println(result);
    }
}

