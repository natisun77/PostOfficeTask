package com.opinta.entity;

public enum DeliveryType {
    W2W("Warehouse to Warehouse"),
    W2D("Warehouse to Door"),
    D2W("Door to Warehouse"),
    D2D("Door to Door");

    private String name;

    DeliveryType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
