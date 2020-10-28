import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public interface Machine extends Remote{
    /**
     * this interface reads files and write data in files
     * @param name : name of the file
     */

    public boolean createFile(String filename) throws RemoteException, NotBoundException;

    public boolean createFile(String filename, byte[] data, String host, int port) throws RemoteException;

    public void read(String filename, String host, int port) throws RemoteException, IOException, NotBoundException, InterruptedException;

    public void write(String filename, byte[] data, String host, int port) throws RemoteException, IOException, NotBoundException;
}