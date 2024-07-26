package org.example;

import java.util.HashMap;
import java.util.Map;

public class Localidade {
    final private int x;
    final private int y;
    final private String nome;
    final private int qtdItensReceber;
    private Map<String, Double> feromonios;
    
    private Double probabilidade;
    
    
    
    private boolean recebeuEntrega = false;
    
    public Localidade(String cityName, int x, int y, int qtdItensReceber) {
        this.nome = cityName;
        this.x = x;
        this.y = y;
        feromonios = new HashMap<>();
        probabilidade = 0d;
        this.qtdItensReceber = qtdItensReceber;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public String getNome() {
        return nome;
    }
    
    public int getQtdItensReceber() {
        return qtdItensReceber;
    }
    
    public Double getFeromonio(String nomeCidade) {
        Double feromonio = this.feromonios.get(nomeCidade);
        
        if (feromonio == null) return 1d;
        
        return feromonio;
    }
    
    public void setFeromonio(String nomeCidade, double feromonio) {
        this.feromonios.put(nomeCidade, feromonio);
    }
    
    public Double getProbabilidade() {
        
        return probabilidade;
    }
    
    public void setProbabilidade(double probabilidade) {
        this.probabilidade = probabilidade;
    }
    
    public double calcularDistancia(Localidade proxLocalidade) {
        if (proxLocalidade == null) return 0;
        
        Double distanciaX = Math.pow(proxLocalidade.getX() - getX(), 2);
        Double distanciaY = Math.pow(proxLocalidade.getY() - getY(), 2);
        
        return Math.sqrt(distanciaX + distanciaY);
    }
    
    public boolean recebeuEntrega() {
        if(qtdItensReceber == 0) return true;
        
        return recebeuEntrega;
    }
    
    public void setRecebeuEntrega(boolean recebeuEntrega) {
        this.recebeuEntrega = recebeuEntrega;
    }
    
    public void limparFeromonios() {
        feromonios = new HashMap<>();
    }
    
    public Localidade copiar() {
        return new Localidade(nome, x, y, qtdItensReceber);
    }
    
    
}