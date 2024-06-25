package org.example;

import java.util.ArrayList;

public class Formiga {
    protected ArrayList<Cidade> cidadesVisitadas;
    
    public Formiga() {
        cidadesVisitadas = new ArrayList<>();
    }
    
    protected void visitarCidade(Cidade cidade) {
        cidadesVisitadas.add(cidade);
    }
    
    protected boolean visitouCidade(String nome) {
        for(Cidade cidade: cidadesVisitadas) {
            if(cidade.getNome().equals(nome)) {
                return true;
            }
        }
        
        return false;
    }
    
    protected Cidade getUltimaCidade() {
        return cidadesVisitadas.get(cidadesVisitadas.size() -1);
    }
    
    protected double distanciaPercorrida() {
        double distanciaTotal = 0;
        for (int i = 0; i < cidadesVisitadas.size() - 1; i++) {
            Cidade cidade = cidadesVisitadas.get(i);
            Cidade cidade2 = cidadesVisitadas.get(i + 1);
            distanciaTotal += cidade.calcularDistancia(cidade2);
        }
        return distanciaTotal;
    }
    
    protected void limpar() {
        cidadesVisitadas = new ArrayList<>();
    }
}