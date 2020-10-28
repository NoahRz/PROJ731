import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface Controle extends Remote{
    //public boolean add(String url, Machine ma) throws RemoteException, AlreadyBoundException;
    boolean add(Remote machine) throws RemoteException, AlreadyBoundException;
    boolean remove(Machine machine) throws RemoteException;
    //void writeCharge(String name, int charge) throws RemoteException;
    void writeCharge(Remote machine, int charge) throws IOException;

    void createFileInEachMachine(String filename) throws RemoteException, NotBoundException;
}
