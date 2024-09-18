package udesc.trabalho;

public enum Tag {
    S0("Pag. Manual", "carex_pagmanual"), S1("Veloe", "carex_veloe"), S2("Sem Parar", "carex_semparar"), 
    S3("Taggy", "carex_taggy"), S4("ConnectCar", "carex_connectcar"), S5("Zul+", "carex_zul");

    private String name;
    private String routingKey;

    private Tag(String name, String routingKey) {
        this.name = name;
        this.routingKey = routingKey;
    }

    public String getName() {
        return this.name;
    }

    public String getRoutingKey() {
        return this.routingKey;
    }
}
