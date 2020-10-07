import java.io.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class MachineC extends UnicastRemoteObject implements Machine {

    String nom = null;

    public MachineC(String nom) throws RemoteException {
        super();
        this.nom = nom;
    }


    @Override
    public byte[] lecture(String nom) throws IOException {
        InputStream  lecture = new BufferedInputStream(new FileInputStream("C://Users//loicv//Documents//1. Polytech//FI4//PROJ731 - Flux de données et accès concurents//Projet//src//data//" + nom));
        return lecture.readAllBytes();
    }

    @Override
    public Boolean ecriture(String nom, byte[] donnees) throws IOException {
        try {
            FileOutputStream sortie = new FileOutputStream(nom);
            sortie.write(donnees);
            return true;
        } catch (Exception ae){
            return false;
        }
    }

    public String getNom() {
        return nom;
    }

//    public static void main(String[] args) throws IOException, NotBoundException, AlreadyBoundException {
//        MachineC maM = new MachineC("m1");
//        Remote r = Naming.lookup("rmi://localhost:1099/Aiguilleur");
//
//        if (r instanceof Controle) {
//            boolean s = ((Controle) r).ajout(maM.getNom(), maM);
//            System.out.println(s);
//        }
//    }

        public void lancement() throws IOException, NotBoundException, AlreadyBoundException {
        MachineC maM = new MachineC(this.getNom());
        Remote r = Naming.lookup("rmi://localhost:1099/Aiguilleur");

        if (r instanceof Controle) {
            boolean s = ((Controle) r).ajout(maM.getNom(), maM);
            System.out.println(s);
        }
        }


}
