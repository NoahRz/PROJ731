import java.util.concurrent.*;

public class SwitcherSemaphore {

    private Semaphore writingSem;
    private Semaphore readingSem;
    private String filename;


    public SwitcherSemaphore(String filename) {
        this.writingSem = new Semaphore(1);
        this.readingSem = new Semaphore(1);
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void readingP() throws InterruptedException {
        ReaderThreadP reader = new ReaderThreadP(readingSem, "reader " + filename);

        reader.start();

        reader.join(); // waiting for the thread
    }

    public void readingV() throws InterruptedException {
        ReaderThreadV reader = new ReaderThreadV(readingSem, "reader " + filename);

        reader.start();
        reader.join(); // waiting for the thread
    }

    public void writingP() throws InterruptedException {
        WriterThreadP writer = new WriterThreadP(writingSem, "writer " + filename);

        writer.start();
        writer.join(); // waiting for the thread
    }

    public void writingV() throws InterruptedException {
        WriterThreadV writer = new WriterThreadV(writingSem, "writer " + filename);

        writer.start();
        writer.join(); // waiting for the thread
    }



}
