import it.example.concurrent.queue.BlockingQueue;
import it.example.concurrent.thread.Reader;
import it.example.concurrent.thread.Writer;

public class Application {

    private static final int CAPACITY = 10;
    private static final int WRITER = 10;
    private static final int READER = 5;

    public static void main(String[] args) {
        // shared resource
        final BlockingQueue<Integer> blockingQueue = new BlockingQueue<>(CAPACITY);
        // threads
        final Writer[] writers = new Writer[WRITER];
        final Reader[] readers = new Reader[READER];

        for (int i = 0; i < writers.length; i++) {
            writers[i] = new Writer(blockingQueue);
            writers[i].start();
        }

        for (int i = 0; i < readers.length; i++) {
            readers[i] = new Reader(blockingQueue);
            readers[i].start();
        }
    }
}
