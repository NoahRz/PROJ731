import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public interface Machine extends Remote{
    /**
     * Cette classe est l'interface pour les machines, elle a plusieurs objectifs, lire un fichier ou écrire des données
     * @param nom :  Nom du fichier sur la machine
     *
     * N.B : Cette interface implémente la classe : Machine mais aussi la classe Aiguilleur
     */

    public byte[] lecture(String nom) throws IOException, NotBoundException, InterruptedException;
    public Boolean ecriture(String nom, byte[] donnees) throws IOException;
}

interface Notification extends Remote{
    Boolean enVie() throws IOException;
}