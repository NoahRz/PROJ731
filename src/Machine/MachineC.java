import java.io.*;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class MachineC extends UnicastRemoteObject implements Machine, Notification {

    String name = null;

    public MachineC(String name) throws RemoteException {
        super();
        this.name = name;
    }


    @Override
    public byte[] read(String name) throws IOException {
        InputStream read = new BufferedInputStream(new FileInputStream(".//src//data//" + name));
        return read.readAllBytes();
    }

    @Override
    public Boolean write(String name, byte[] donnees) throws IOException {
        try {
            FileOutputStream sortie = new FileOutputStream(name);
            sortie.write(donnees);
            return true;
        } catch (Exception ae){
            return false;
        }
    }

    public String getName() {
        return name;
    }


    public void launch() throws IOException, NotBoundException, AlreadyBoundException {
        Remote r = Naming.lookup("rmi://localhost:1099/Switcher");

        if (r instanceof Controle) {
            boolean s = ((Controle) r).add(name, this);
            System.out.println(s);
        }
    }

    public void checkOut() throws RemoteException, NotBoundException, MalformedURLException {
        Controle r = (Controle) Naming.lookup("rmi://localhost:1099/Switcher");
        r.remove(this.getName());
    }

    @Override
    public Boolean alive() throws IOException {
        return true;
    }
}
