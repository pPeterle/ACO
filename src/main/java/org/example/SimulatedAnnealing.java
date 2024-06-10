package org.example;

public class SimulatedAnnealing {
    
    public static void main(String[] args) {
        // Create and add our cities
        Cidade Cidade = new Cidade("Paris",60, 200);
        TrajetoHelper.addCidade(Cidade);
        Cidade Cidade2 = new Cidade("Lyon",180, 200);
        TrajetoHelper.addCidade(Cidade2);
        Cidade Cidade3 = new Cidade("La Rochelle",80, 180);
        TrajetoHelper.addCidade(Cidade3);
        Cidade Cidade4 = new Cidade("Bordeaux",140, 180);
        TrajetoHelper.addCidade(Cidade4);
        Cidade Cidade5 = new Cidade("Lenz",20, 160);
        TrajetoHelper.addCidade(Cidade5);
        Cidade Cidade6 = new Cidade("Nice",100, 160);
        TrajetoHelper.addCidade(Cidade6);
        Cidade Cidade7 = new Cidade("Lille",200, 160);
        TrajetoHelper.addCidade(Cidade7);
        Cidade Cidade8 = new Cidade("Rennes",140, 140);
        TrajetoHelper.addCidade(Cidade8);
        Cidade Cidade9 = new Cidade("Brest",40, 120);
        TrajetoHelper.addCidade(Cidade9);
        Cidade Cidade10 = new Cidade("Toulon",100, 120);
        TrajetoHelper.addCidade(Cidade10);
        Cidade Cidade11 = new Cidade("Nancy",180, 100);
        TrajetoHelper.addCidade(Cidade11);
        Cidade Cidade12 = new Cidade("Calais",60, 80);
        TrajetoHelper.addCidade(Cidade12);
        
        double temp = 100000;
        
        double coolingRate = 0.003;
        
        Trajeto currentSolution = new Trajeto();
        currentSolution.gerarTrajetoAleatorio();
        
        System.out.println("Disância da solução inicial: " + currentSolution.getDistanciaTotal());
        System.out.println("Trajeto: " + currentSolution);
        
        Trajeto best = new Trajeto(currentSolution.getCaminho());
        
        while (temp > 1) {
            Trajeto newSolution = new Trajeto(currentSolution.getCaminho());
            
            int tourPos1 = Util.inteiroAleatorio(0 , newSolution.getTamanhoTrajeto());
            int tourPos2 = Util.inteiroAleatorio(0 , newSolution.getTamanhoTrajeto());
            
            while(tourPos1 == tourPos2) {tourPos2 = Util.inteiroAleatorio(0 , newSolution.getTamanhoTrajeto());}
            
            Cidade CidadeSwap1 = newSolution.getCidade(tourPos1);
            Cidade CidadeSwap2 = newSolution.getCidade(tourPos2);
            
            newSolution.setCidade(tourPos2, CidadeSwap1);
            newSolution.setCidade(tourPos1, CidadeSwap2);
            
            int currentDistance   = currentSolution.getDistanciaTotal();
            int neighbourDistance = newSolution.getDistanciaTotal();
            
            double rand = Util.doubleAletorio();
            if (Util.probabilidadeDeAceitacao(currentDistance, neighbourDistance, temp) > rand) {
                currentSolution = new Trajeto(newSolution.getCaminho());
            }
            
            if (currentSolution.getDistanciaTotal() < best.getDistanciaTotal()) {
                best = new Trajeto(currentSolution.getCaminho());
            }
            
            temp *= 1 - coolingRate;
        }
        
        System.out.println("Distância solução final: " + best.getDistanciaTotal());
        System.out.println("Trajeto: " + best);
    }
}