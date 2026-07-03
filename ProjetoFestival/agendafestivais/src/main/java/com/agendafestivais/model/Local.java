package com.agendafestivais.model;

public class Local {
    private int id;
    private String nome;
    private String cidade;

    // Construtor vazio (Obrigatório para o Gson)
    public Local() {}

    // Construtor com parâmetros
    public Local(int id, String nome, String cidade) {
        this.id = id;
        this.nome = nome;
        this.cidade = cidade;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
}
