package org.example;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;



public class Main {

    public AtomicInteger normalCount = new AtomicInteger(1);

    public void normalCount(int limit) {
        while(normalCount.get() <= limit) {
            System.out.println("Normal execution : Number is " + normalCount.getAndIncrement());
        }
    }

    public Thread createThread(String threadName, AtomicInteger count, int limit, Object lock) {
        return new Thread(() -> {
            while(count.get() <= limit) {
                synchronized(lock) {
                    if(count.get() <= limit) {
                        System.out.println("Thread " + threadName + " : Number is " + count.getAndIncrement());
                        lock.notify();
                        if(count.get() <= limit) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        });
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int limit;
        AtomicInteger count = new AtomicInteger(1);
        final Object lock = new Object();

        System.out.println("Please enter the limit till which you want to print numbers");

        limit = sc.nextInt();


        Main main = new Main();
        long startNormal = System.nanoTime();
        main.normalCount(limit);
        long endNormal = System.nanoTime();
        long executionTimeNormal = (endNormal - startNormal) / 1_000_000;
        System.out.println("Execution time is " + executionTimeNormal + " ms");

        Thread t1 = main.createThread("Thread 1", count, limit, lock);
        Thread t2 = main.createThread("Thread 2", count, limit, lock);
        Thread t3 = main.createThread("Thread 3", count, limit, lock);
        Thread t4 = main.createThread("Thread 4", count, limit, lock);

        long startTime = System.nanoTime();

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        // Wait for both threads to finish
        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long endTime = System.nanoTime();

        long executionTime = (endTime - startTime) / 1_000_000;
        System.out.println("Execution time for normal is " + executionTimeNormal + " ms");
        System.out.println("AND");
        System.out.println("Execution time for multiThreaded is " + executionTime + " ms");

        sc.close();
    }
}