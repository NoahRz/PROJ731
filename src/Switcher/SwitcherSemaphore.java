import java.util.concurrent.*;

public class SwitcherSemaphore {

    private Semaphore writingSem;
    private Semaphore readingSem;

    public SwitcherSemaphore() {
        this.writingSem = new Semaphore(1);
        this.readingSem = new Semaphore(1);

    }

    public void readingP() {
        ReaderThreadP reader = new ReaderThreadP(readingSem, "reader");

        reader.start();
    }

    public void readingV() {
        ReaderThreadV reader = new ReaderThreadV(readingSem, "reader");

        reader.start();
    }

    public void writingP() {
        WriterThreadV writer = new WriterThreadV(writingSem, "reader");

        writer.start();
    }

    public void writingV() {
        WriterThreadV writer = new WriterThreadV(writingSem, "reader");

        writer.start();
    }




}
