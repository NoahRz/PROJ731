import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface Controle extends Remote{
    public boolean ajout(String url,Machine ma) throws RemoteException, AlreadyBoundException;
    public boolean supprimer(String url) throws RemoteException;
}
