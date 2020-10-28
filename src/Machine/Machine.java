import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public interface Machine extends Remote{
    /**
     * this interface reads files and writes data in files
     * @param filename : name of the file
     */

    boolean createFile(String filename) throws RemoteException, NotBoundException;

    boolean createFile(String filename, byte[] data, String host, int port) throws RemoteException;

    void read(String filename, String host, int port) throws IOException, NotBoundException, InterruptedException;

    void write(String filename, byte[] data, String host, int port) throws IOException, NotBoundException;
}