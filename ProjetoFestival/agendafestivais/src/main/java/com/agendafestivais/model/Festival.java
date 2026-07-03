package com.agendafestivais.model;

import java.util.Date;

public class Festival {
    private int id;
    private String nome;
    private Date data;
    private double preco;
    private int localId; // Requisito 4.1: Entidade filho contendo o id do pai como atributo (FK)

    // Construtor vazio (Obrigatório para o Gson)
    public Festival() {}

    // Construtor com parâmetros
    public Festival(int id, String nome, Date data, double preco, int localId) {
        this.id = id;
        this.nome = nome;
        this.data = data;
        this.preco = preco;
        this.localId = localId;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }
    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }
    public int getLocalId() { return localId; }
    public void setLocalId(int localId) { this.localId = localId; }
}
