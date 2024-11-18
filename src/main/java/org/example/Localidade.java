package org.example;

import java.util.HashMap;
import java.util.Map;

public class Localidade {
    final private double x;
    final private double y;
    final private String nome;
    final private Boolean hotel;
    final private int qtdItensReceber;
    private Map<String, Double> feromonios;
    
    private Double probabilidade;
    
    
    
    private boolean recebeuEntrega = false;
    
    public Localidade(String cityName, double x, double y, int qtdItensReceber, boolean hotel) {
        this.nome = cityName;
        this.x = x;
        this.y = y;
        feromonios = new HashMap<>();
        probabilidade = 0d;
        this.qtdItensReceber = qtdItensReceber;
        this.hotel = hotel;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
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
        
        return distance(getX(), proxLocalidade.getX(), getY(), proxLocalidade.getY(), 0, 0) / 1000;
    }
    
    private double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {
        
        final int R = 6371; // Radius of the earth
        final double weight = 1.1;
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        
        double height = el1 - el2;
        
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        
        return Math.sqrt(distance) * weight;
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
        return new Localidade(nome, x, y, qtdItensReceber, hotel);
    }
    
    
}