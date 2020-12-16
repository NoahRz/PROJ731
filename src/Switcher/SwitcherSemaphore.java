import java.util.concurrent.*;

public class SwitcherSemaphore {

    private Semaphore writingSem;
    private Semaphore readingSem;

    public SwitcherSemaphore() {
        this.writingSem = new Semaphore(1);
        this.readingSem = new Semaphore(1);

    }

    public void reading() {
        ReaderThread reader = new ReaderThread(readingSem, "reader");

        reader.start();
    }

    public void writing() {
        WriterThread writer = new WriterThread(writingSem, "reader");

        writer.start();
    }




}
