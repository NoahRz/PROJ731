import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface Controle extends Remote{
    public boolean add(String url, Machine ma) throws RemoteException, AlreadyBoundException;
    public boolean remove(String url) throws RemoteException;
    public void writeCharge(String name, int charge) throws RemoteException;

    void createFileInEachMachine(String filename) throws RemoteException, NotBoundException;
}
