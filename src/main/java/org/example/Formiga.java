package org.example;

public class Formiga {
    protected int tamanhoCaminho;
    protected int[] caminho;
    protected boolean[] cidadesVisitadas;
    
    public Formiga(int tourSize) {
        this.tamanhoCaminho = tourSize;
        this.caminho = new int[tourSize];
        this.cidadesVisitadas = new boolean[tourSize];
    }
    
    protected void visitarCidade(int quantasCidadesJaForamVisitadas, int cidade) {
        caminho[quantasCidadesJaForamVisitadas] = cidade; //add to trail
        cidadesVisitadas[cidade] = true;           //update flag
    }
    
    protected boolean visitouCidade(int i) {
        return cidadesVisitadas[i];
    }
    
    protected double distanciaPercorrida(double[][] graph) {
        double length = graph[caminho[tamanhoCaminho - 1]][caminho[0]];
        for (int i = 0; i < tamanhoCaminho - 1; i++)
            length += graph[caminho[i]][caminho[i + 1]];
        return length;
    }
    
    protected void limpar() {
        for (int i = 0; i < tamanhoCaminho; i++)
            cidadesVisitadas[i] = false;
    }
}