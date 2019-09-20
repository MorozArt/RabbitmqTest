package com.moroz.rabbitmqTest;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Sender {

    private String userName;
    private String password;
    private String virtualHost;
    private String host;
    private Map<String, String> queues;
    private int port;
    private String exchangeName;
    private ExchangeType exchangeType;
    private List< Pair<String, Serializable> > messages;
    private ConnectionFactory factory;

    public Sender(String userName, String password, String virtualHost, String host, int port,
                  String exchangeName, ExchangeType exchangeType) {
        this.userName = userName;
        this.password = password;
        this.virtualHost = virtualHost;
        this.host = host;
        this.port = port;
        this.exchangeName = exchangeName;
        this.exchangeType = exchangeType;

        messages = new ArrayList<>();
        queues = new HashMap<>();

        factory = new ConnectionFactory();
        if(!getUserName().isEmpty()) { factory.setUsername(getUserName()); }
        if(!getPassword().isEmpty()) { factory.setPassword(getPassword()); }
        if(!getVirtualHost().isEmpty()) { factory.setVirtualHost(getVirtualHost()); }
        if(getPort() > 0) { factory.setPort(getPort()); }
        factory.setHost(getHost());
    }

    public Sender(String host, String exchangeName, ExchangeType exchangeType) {
        this("", "", "", host, -1, exchangeName, exchangeType);
    }

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

    public int getPort() {
        return port;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public ExchangeType getExchangeType() {
        return exchangeType;
    }

    public Map<String, String> getQueues() {
        return queues;
    }

    public void addQueue(String queueName, String queueRoutingKey) {
        queues.put(queueRoutingKey,queueName);
    }

    public void addMessage(String routingKey, Serializable message) {
        messages.add(new Pair<>(routingKey, message));
    }

    public boolean hasMessage() {
        return messages.isEmpty() ? false : true;
    }

    public Pair<String, Serializable> getMessage() {
        Pair<String, Serializable> message = messages.get(messages.size()-1);
        messages.remove(messages.size()-1);
        return message;
    }

    public boolean isMessageEmpty() {
        return messages.isEmpty();
    }

    public void sendMessage() {
        if(!isMessageEmpty()) {
            try(Channel channel = getChannel()) {
                sendMessage(channel);
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendAllMessages() {
        if(!isMessageEmpty()) {
            try(Channel channel = getChannel()) {
                while (hasMessage()) {
                    sendMessage(channel);
                }
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(Channel channel) throws IOException {
        Pair<String, Serializable> message = getMessage();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(bytes);
        outputStream.writeObject(message.getValue());

        channel.basicPublish(getExchangeName(), message.getKey(), null, bytes.toByteArray());
        System.out.println(" [x] Sent '"+message.getValue().toString()+"' with: "+message.getKey()+" routing key");
    }

    private Channel getChannel() throws IOException, TimeoutException {

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(getExchangeName(), getExchangeType().getDescription());
        Map<String, String> queues = getQueues();
        if(queues.size() != 0) {
            for (Map.Entry<String, String> queue : queues.entrySet()) {
                channel.queueDeclare(queue.getValue(), true, false, false, null);
                channel.queueBind(queue.getValue(), getExchangeName(), queue.getKey());
            }
        }

        return channel;
    }
}