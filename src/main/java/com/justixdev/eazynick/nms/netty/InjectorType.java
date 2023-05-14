package com.justixdev.eazynick.nms.netty;

public enum InjectorType {

    INCOMING("client_handler"),
    OUTGOING("server_handler");

    private final String handlerName;

    InjectorType(String handlerName) {
        this.handlerName = handlerName;
    }

    public String getHandlerName() {
        return handlerName;
    }

}
