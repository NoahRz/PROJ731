import java.util.concurrent.Semaphore;

public class WriterThreadV extends Thread {

    Semaphore semaphore;
    String threadName;

    public WriterThreadV(Semaphore semaphore, String threadName){
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
