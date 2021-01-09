package it.example.concurrent.thread;

import it.example.concurrent.queue.BlockingQueue;

import java.util.Random;

public class Writer extends Thread {

    private final BlockingQueue<Integer> queue;

    public Writer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep((long) (Math.random() * 1000));
                final int element = new Random().nextInt(10) + 1;
                queue.put(element);

            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
}
