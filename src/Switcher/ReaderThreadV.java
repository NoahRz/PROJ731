import java.util.concurrent.Semaphore;

public class ReaderThreadV extends Thread {

    Semaphore semaphore;
    String threadName;


    public ReaderThreadV(Semaphore semaphore, String threadName){
        super(threadName);
        this.semaphore = semaphore;
        this.threadName = threadName;
    }

    public void run() {
        System.out.println("Starting " + threadName);
        // Release the permit.
        System.out.println(threadName + " releases the permit.");
        semaphore.release();
    }
}