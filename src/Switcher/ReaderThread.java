import java.util.concurrent.Semaphore;

public class ReaderThread extends Thread {

    Semaphore semaphore;
    String threadName;


    public ReaderThread(Semaphore semaphore, String threadName){
        super(threadName);
        this.semaphore = semaphore;
    }

    public void run() {
        System.out.println("Starting " + threadName);
        try
        {
            // First, get a permit.
            System.out.println(threadName + " is waiting for a permit.");

            // acquiring the lock
            semaphore.acquire();

            System.out.println(threadName + " gets a permit.");

            // Now, accessing the shared resource.


        } catch (InterruptedException exc) {
            System.out.println(exc);
        }

        // Release the permit.
        System.out.println(threadName + " releases the permit.");
        semaphore.release();
    }
}