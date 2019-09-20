package com.moroz.rabbitmqTest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) {
        //Start Receiver
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringReceiver stringReceiver = new StringReceiver("localhost", "StringExchange",
                        ExchangeType.DIRECT);
                stringReceiver.setQueue("test");
                stringReceiver.setWork((String message) -> {
                    System.out.println(" [x] Processing '" + message + "'");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

                try {
                    stringReceiver.receive("1");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //Start Sender
        new Thread(new Runnable() {
            @Override
            public void run() {
                Sender stringSender = new Sender("localhost", "StringExchange",
                        ExchangeType.DIRECT);
                stringSender.addQueue("test", "1");
                stringSender.addQueue("another test", "2");

                stringSender.addMessage("2", "Hello World Again!");
                stringSender.addMessage("1", "Hello World!");
                stringSender.sendAllMessages();
            }
        }).start();
    }
}
