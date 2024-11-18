package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.example.view.PanAndZoom;

import java.io.FileReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        try {
            
            CSVReader localidadesFile = new CSVReaderBuilder(new FileReader("src/main/files/Demanda.csv")).build();
            List<String[]> localidadesString = localidadesFile.readAll();
            
            List<Localidade> localidadeArrayList = localidadesString.stream()
                    .skip(1)
                    .map(item -> new Localidade(item[0], Double.parseDouble(item[1]), Double.parseDouble(item[2]), Integer.parseInt(item[3])))
                    .toList();

            CSVReader hoteisCsv = new CSVReaderBuilder(new FileReader("src/main/files/Hoteis.csv")).build();
            List<String[]> hoteisString = hoteisCsv.readAll();

            List<Localidade> hoteisArrayList = localidadesString.stream()
                    .skip(1)
                    .map(item -> new Localidade(item[0], Double.parseDouble(item[1]), Double.parseDouble(item[2]), Integer.parseInt(item[3])))
                    .toList();

            
            AntColonyOptimization aco = new AntColonyOptimization(1.0, 5, 0.9, 5, 0.01, 100, 30, localidadeArrayList);
            
            Instant start = Instant.now();
            
            List<Caminhao> melhorCaminho = aco.comecarOtimizacao();
            
            Instant end = Instant.now();
            System.out.println(Duration.between(start, end).getSeconds());
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new PanAndZoom(melhorCaminho, localidadeArrayList);
                }
            });
        } catch (Exception e) {
            System.out.println("Erro ao ler arquivo " + e.getMessage());
        }
        
        
        System.out.println("Finalizado ");
    }
}
