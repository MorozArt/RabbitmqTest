package com.moroz.rabbitmqTest;

public enum ExchangeType {
    FANOUT("fanout"),
    DIRECT("direct"),
    TOPIC("topic"),
    HEADERS("headers");

    private String description;

    private ExchangeType(String description) {
        this.description = description;
    }

    public String getDescription() {return description;}
}
