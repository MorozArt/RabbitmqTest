package com.moroz.rabbitmqTest;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class StringReceiver extends Receiver<String> {

    public StringReceiver(String userName, String password, String virtualHost, String host, int port,
                          String exchangeName, ExchangeType exchangeType) {
        super(userName, password, virtualHost, host, port, exchangeName, exchangeType);
    }

    public StringReceiver(String host, String exchangeName, ExchangeType exchangeType) {
        super("", "", "", host, -1, exchangeName, exchangeType);
    }

    @Override
    public void receive(String routingKey) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        if(!getUserName().isEmpty()) { factory.setUsername(getUserName()); }
        if(!getPassword().isEmpty()) { factory.setPassword(getPassword()); }
        if(!getVirtualHost().isEmpty()) { factory.setVirtualHost(getVirtualHost()); }
        if(getPort() > 0) { factory.setPort(getPort()); }
        factory.setHost(getHost());
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(getExchangeName(), getExchangeType().getDescription());
        String queueName;
        if(getQueue() != null) {
            queueName = getQueue();
        } else {
            queueName = channel.queueDeclare().getQueue();
        }
        channel.queueBind(queueName, getExchangeName(), routingKey);

        System.out.println(" [*] Waiting for messages with routingKey: "+routingKey);

        channel.basicQos(1);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");

            try {
                doWork(getWork(), message);
            } finally {
                System.out.println(" [x] Done");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };

        boolean autoAck = false;
        channel.basicConsume(queueName, autoAck, deliverCallback, consumerTag -> { });
    }
}

