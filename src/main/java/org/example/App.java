package org.example;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        AntColonyOptimization aco = new AntColonyOptimization(1.0, 1, 5, 0.5, 0.4, 1000, 30);
        aco.gerarMatrixAleatoria();
        aco.comecarOtimizacao();
        
        System.out.println(aco.s);
        
        System.out.println("Finalizado ");
    }
}
