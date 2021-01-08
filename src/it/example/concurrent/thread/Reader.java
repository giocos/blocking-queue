package it.example.concurrent.thread;

import it.example.concurrent.queue.BlockingQueue;

public class Reader extends Thread {

    private final BlockingQueue<Integer> queue;

    public  Reader(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep((long) Math.random() * 1000);
                queue.take();

            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
}
