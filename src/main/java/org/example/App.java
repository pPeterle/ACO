package org.example;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        AntColonyOptimization aco = new AntColonyOptimization(1.0, 5, 0.9, 5, 0.01, 1000, 30);
        aco.gerarMatrixAleatoria();
        aco.comecarOtimizacao();
        
        System.out.println("Finalizado ");
    }
}
