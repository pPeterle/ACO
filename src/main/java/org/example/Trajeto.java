package org.example;

import java.util.ArrayList;
import java.util.Collections;

public class Trajeto{
    
    private ArrayList<Cidade> tour = new ArrayList<Cidade>();
    
    private int distance = 0;
    
    public Trajeto(){
        for (int i = 0; i < TrajetoHelper.numeroCidades(); i++) {
            tour.add(null);
        }
    }
    
    @SuppressWarnings("unchecked")
    public Trajeto(ArrayList<Cidade> tour){
        this.tour = (ArrayList<Cidade>) tour.clone();
    }
    
    public ArrayList<Cidade> getCaminho(){
        return tour;
    }
    
    public void gerarTrajetoAleatorio() {
        for (int CidadeIndex = 0; CidadeIndex < TrajetoHelper.numeroCidades(); CidadeIndex++) {
            setCidade(CidadeIndex, TrajetoHelper.getCidade(CidadeIndex));
        }
        Collections.shuffle(tour);
    }
    
    public Cidade getCidade(int index) {
        return tour.get(index);
    }
    
    public void setCidade(int index, Cidade Cidade) {
        tour.set(index, Cidade);
        distance = 0;
    }
    
    public int getDistanciaTotal(){
        if (distance == 0) {
            int tourDistance = 0;
            for (int CidadeIndex = 0; CidadeIndex < getTamanhoTrajeto(); CidadeIndex++) {
                Cidade fromCidade = getCidade(CidadeIndex);
                Cidade destinationCidade;
                if(CidadeIndex+1 < getTamanhoTrajeto()){
                    destinationCidade = getCidade(CidadeIndex+1);
                }
                else{
                    destinationCidade = getCidade(0);
                }
                tourDistance += Util.calcularDistancia(fromCidade, destinationCidade);
            }
            distance = tourDistance;
        }
        return distance;
    }
    
    public int getTamanhoTrajeto() {
        return tour.size();
    }
    
    @Override
    public String toString() {
        String s = getCidade(0).getNome();
        for (int i = 1; i < getTamanhoTrajeto(); i++) {
            s += " -> " + getCidade(i).getNome();
        }
        return s;
    }
}
