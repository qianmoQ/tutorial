package org.devlive.tutorial.multithreading.chapter05;

public class SynchronizedBytecode
{
    public void syncBlock()
    {
        synchronized (this) {
            System.out.println("同步代码块");
        }
    }

    public synchronized void syncMethod()
    {
        System.out.println("同步方法");
    }
}
