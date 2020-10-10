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

    public byte[] read(String name) throws RemoteException, IOException, NotBoundException, InterruptedException;
    public Boolean write(String name, byte[] data) throws RemoteException, IOException;
}