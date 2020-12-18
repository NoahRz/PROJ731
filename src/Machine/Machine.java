import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Machine extends Remote{
    /**
     * this interface reads files and writes data in files
     * @param filename : name of the file
     */

    boolean createFile(String filename) throws RemoteException, NotBoundException;

    boolean createFile(String filename, byte[] data, String host, int port) throws RemoteException;

    void read(String filename, String host, int port) throws IOException, NotBoundException, InterruptedException;

    void write(String filename, byte[] data, String host, int port) throws IOException, NotBoundException;

    byte[] getContentFile(String filename) throws RemoteException;

    boolean add(String filename, byte[] contentFile) throws IOException, RemoteException;

    void openWriting(String filename) throws RemoteException, InterruptedException;

    void closeWriting(String filename) throws RemoteException, InterruptedException;;

    void openReading(String filename) throws RemoteException, InterruptedException;

    void closeReading(String filename)throws RemoteException, InterruptedException;
}