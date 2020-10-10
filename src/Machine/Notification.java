import java.io.IOException;
import java.rmi.Remote;

public interface Notification extends Remote {
        Boolean alive() throws IOException;
}