import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

/**
 * La classe Aiguilleur a pour objectif de recevoir les requetes des différents client et de les distri au autre machine
 * Il est donc serveur pour les clients et client pour les machines.
 */
public class Aiguilleur extends UnicastRemoteObject implements Machine, Controle {
    private Registry leRegistre = null;
    private int tdr = 0;

    protected Aiguilleur(Registry r) throws RemoteException {
        super();
        this.leRegistre = r;
    }

    @Override
    public byte[] lecture(String nom) throws IOException, NotBoundException {
        String mach = this.aTourDeRole();
        System.out.println(mach);
        Remote rem = this.leRegistre.lookup("m1");
        byte[] s = null;
        if (rem instanceof Machine) {
            s = ((Machine) rem).lecture(nom);
        }

        return s;
    }

    @Override
    public Boolean ecriture(String nom, byte[] donnees) throws IOException {
        return null;
    }

    @Override
    public boolean ajout(String url,Machine ma) throws RemoteException, AlreadyBoundException {
        try{
            this.leRegistre.rebind(url, ma);
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean supprimer(String url) throws RemoteException {
        return false;
    }


    public String aTourDeRole() throws RemoteException {
        String[] valeur = this.leRegistre.list();
        valeur = Arrays.copyOfRange(valeur, 1, valeur.length);
        int longeur = valeur.length;
        int a = this.tdr%longeur;
        this.tdr++;
        return valeur[a];

    }



    // ---------------------------------------------------------------------------------------------
    public static void main(String[] args) {
        try {

            Registry a = LocateRegistry.createRegistry(1099);

            Aiguilleur monA = new Aiguilleur(a);

            String url = "rmi://localhost:1099/Aiguilleur";
            Naming.rebind(url, monA);

            System.out.println("Serveur lancé");
        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }


}
