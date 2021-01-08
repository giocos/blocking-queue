package it.example.concurrent.queue;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("unchecked")
public class BlockingQueue<T> implements Iterable<T>, RandomAccess, Cloneable, Serializable {

    private static final int HEAD = 0;
    private static final int DEFAULT_CAPACITY = 1;

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private int size = 0;
    private T[] elements;
    private boolean putAwait = false;
    private boolean takeAwait = false;

    public BlockingQueue() {
        elements = (T[]) new Object[DEFAULT_CAPACITY];
    }

    public BlockingQueue(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException();
        }
        elements = (T[]) new Object[capacity];
    }

    /**
     * Iterator class
     */
    private class IteratorImpl implements Iterator<T> {

        private int count = 0;

        protected IteratorImpl() { }

        @Override
        public boolean hasNext() {
            return count < size;
        }

        @Override
        public T next() {
            if (count < size) {
                return elements[count++];
            }
            throw new NoSuchElementException();
        }
    }

    /**
     * Non-blocking method
     * @param e
     */
    public void add(T e) {
        if (size < elements.length) {
            elements[size++] = e;
        }
    }

    /**
     * Non-blocking method
     * @return
     */
    public T poll() {
        if (HEAD < size) {
            T e = elements[HEAD];
            elements[HEAD] = null;
            shift();

            return e;
        }
        throw new NoSuchElementException();
    }

    /**
     * Thread safe method
     * @param e
     */
    public void put(T e) {
        try {
            lock.lock();
            while (size == elements.length) {
                System.err.println("Writer-" + Thread.currentThread().getId() + " awaiting...");
                putAwait = true;
                condition.await();
                System.out.println("Writer-" + Thread.currentThread().getId() + " woke up!");
            }
            elements[size++] = e;
            System.out.println("Writer-" + Thread.currentThread().getId() + " put element " + e + " --- size: " + size);

        } catch (final InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            if (takeAwait) {
                condition.signalAll();
                takeAwait = false;
            }
            lock.unlock();
        }
    }

    /**
     * Thread safe method
     * @param
     */
    public T take() {
        try {
            lock.lock();
            while (size == 0) {
                System.err.println("Reader-" + Thread.currentThread().getId() + " awaiting...");
                takeAwait = true;
                condition.await();
                System.out.println("Reader-" + Thread.currentThread().getId() + " woke up!");
            }
            T e = elements[HEAD];
            elements[HEAD] = null;
            shift();

            System.out.println("Reader-" + Thread.currentThread().getId() + " get element " + e + " --- size: " + size);

            return e;

        } catch (final InterruptedException e) {
            e.printStackTrace();
            return null;

        } finally {
            if (putAwait) {
                condition.signalAll();
                putAwait = false;
            }
            lock.unlock();
        }
    }

    public int getCapacity() {
        return elements.length;
    }

    private void shift() {
        final T[] shiftedElements = (T[]) new Object[elements.length];
        for (int i = 1, j = 0; i < size; i++) {
            shiftedElements[j++] = elements[i];
        }
        size--;
        elements = shiftedElements;
    }

    @Override
    public BlockingQueue<T> clone() {
        try {
            return (BlockingQueue<T>) super.clone();
        } catch (final CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new IteratorImpl();
    }
}
