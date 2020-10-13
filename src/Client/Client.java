import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
        serverSocket = new ServerSocket(port);
    }

    public void waitMessage() throws IOException {
        clientSocket = this.serverSocket.accept();
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }
    public String read(String name){

        try {
                Remote r = Naming.lookup("rmi://localhost:1099/Switcher");
                this.startListen(this.port);
                if (r instanceof Machine) {
                    ((Machine) r).read(name, InetAddress.getLocalHost().getHostAddress(), this.port);
                }
                //On att le résultat
                this.waitMessage();
                return this.in.readLine();
            } catch (NotBoundException | IOException | InterruptedException e) {
                e.printStackTrace();
                return "";
            }
        }


    public static <Information> void main(String[] args) throws IOException {
        int port;
        try{
             port = Integer.parseInt(args[0]);
        }catch (Exception e){
             port = 8080;
        }
        Client client1 = new Client(port);

        String result = client1.read("ressource_1.txt");
        System.out.println(result);
    }
}

