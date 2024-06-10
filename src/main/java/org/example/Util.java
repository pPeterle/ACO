package org.example;

import java.util.Random;

public class Util {
    
    
    public static double calcularDistancia(Cidade city1, Cidade city2){
        int xDistance = Math.abs(city1.getX() - city2.getX());
        int yDistance = Math.abs(city1.getY() - city2.getY());
        double distance = Math.sqrt( (xDistance*xDistance) + (yDistance*yDistance) );
        
        return distance;
    }
    

    public static double probabilidadeDeAceitacao(int currentDistance, int newDistance, double temperature) {
        // If the new solution is better, accept it
        if (newDistance < currentDistance) {
            return 1.0;
        }
        // If the new solution is worse, calculate an acceptance probability
        return Math.exp((currentDistance - newDistance) / temperature);
    }
    

    static double doubleAletorio()
    {
        Random r = new Random();
        return r.nextInt(1000) / 1000.0;
    }
    

    public static int inteiroAleatorio(int min , int max) {
        Random r = new Random();
        double d = min + r.nextDouble() * (max - min);
        return (int)d;
    }
}
