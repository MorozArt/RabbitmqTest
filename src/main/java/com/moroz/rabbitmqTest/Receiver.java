package com.moroz.rabbitmqTest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public abstract class Receiver<T> {

    private String userName;
    private String password;
    private String virtualHost;
    private String host;
    private String queueName;
    private int port;
    private String exchangeName;
    private ExchangeType exchangeType;
    private Workable<T> work;

    public Receiver(String userName, String password, String virtualHost, String host, int port,
                    String exchangeName, ExchangeType exchangeType) {

        this.userName = userName;
        this.password = password;
        this.virtualHost = virtualHost;
        this.host = host;
        this.port = port;
        this.exchangeName = exchangeName;
        this.exchangeType = exchangeType;
    }

    public abstract void receive(String routingKey) throws IOException, TimeoutException;

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public String getHost() {
        return host;
    }

    public String getQueue() {
        return queueName;
    }

    public void setQueue(String queueName) {
        this.queueName = queueName;
    }

    public int getPort() {
        return port;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public ExchangeType getExchangeType() {
        return exchangeType;
    }

    public void setWork(Workable<T> work) {
        this.work = work;
    }

    public Workable<T> getWork() {
        return work;
    }

    public void doWork(Workable<T> work, T message) {
        work.doWork(message);
    }
}
