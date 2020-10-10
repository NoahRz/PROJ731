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
     *
     * N.B : Cette interface implémente la classe : Machine mais aussi la classe Aiguilleur
     */

    public byte[] read(String name) throws IOException, NotBoundException, InterruptedException;
    public Boolean write(String name, byte[] donnees) throws IOException;
}

interface Notification extends Remote{
    Boolean alive() throws IOException;
}