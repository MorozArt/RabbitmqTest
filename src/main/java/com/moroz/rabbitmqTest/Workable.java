package com.moroz.rabbitmqTest;

@FunctionalInterface
public interface Workable<T> {
    void doWork(T message);
}
