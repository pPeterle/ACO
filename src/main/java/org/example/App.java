package org.example;

import org.example.view.PanAndZoom;

import java.util.ArrayList;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        
        ArrayList<Localidade> localidadeArrayList = new ArrayList<>();
        
        localidadeArrayList.add(new Localidade("Cidade1", 1, 1, 0));
        localidadeArrayList.add(new Localidade("Cidade2", 2, 2, 5));
        localidadeArrayList.add(new Localidade("Cidade3", 1, 5, 5));
        localidadeArrayList.add(new Localidade("Cidade4", 6, 3, 5));
        localidadeArrayList.add(new Localidade("Cidade5", 2, 7, 5));
        
        
        AntColonyOptimization aco = new AntColonyOptimization(1.0, 5, 0.9, 5, 0.01, 1000, 30);
        aco.gerarMatrixAleatoria();
        ArrayList<Caminhao> melhorCaminho = aco.comecarOtimizacao();
        
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PanAndZoom(melhorCaminho, localidadeArrayList);
            }
        });
        
        
        System.out.println("Finalizado ");
    }
}
