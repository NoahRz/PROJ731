import java.util.concurrent.*;

public class SwitcherSemaphore {

    private Semaphore writingSem;
    private Semaphore readingSem;
    private String filename;
    private int nbLecteur = 0;


    public SwitcherSemaphore(String filename) {
        this.writingSem = new Semaphore(1);
        this.readingSem = new Semaphore(1);
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void readingP() throws InterruptedException {
        ReaderThreadP readerP = new ReaderThreadP(readingSem, "reader " + filename);
        readerP.start();
        readerP.join(); // waiting for the thread

        nbLecteur = nbLecteur + 1;
        if(nbLecteur == 1){
            this.writingP();
        }
        ReaderThreadP readerV = new ReaderThreadP(readingSem, "reader " + filename);
        readerV.start();
        readerV.join();

    }

    public void readingV() throws InterruptedException {
        ReaderThreadP readerP = new ReaderThreadP(readingSem, "reader " + filename);
        readerP.start();
        readerP.join(); // waiting for the thread

        nbLecteur = nbLecteur - 1;
        if(nbLecteur == 0){
            this.writingV();
        }

        ReaderThreadP readerV = new ReaderThreadP(readingSem, "reader " + filename);
        readerV.start();
        readerV.join();
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
