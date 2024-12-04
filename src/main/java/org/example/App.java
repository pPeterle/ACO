package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import de.micromata.opengis.kml.v_2_2_0.*;
import org.example.modelos.Caminhao;
import org.example.modelos.Localidade;
import org.example.view.KmlFile;
import org.example.view.PanAndZoom;

import java.io.File;
import java.io.FileReader;
import java.time.Duration;
import java.time.Instant;
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
                    .map(item -> new Localidade(item[0], Double.parseDouble(item[1]), Double.parseDouble(item[2]), Integer.parseInt(item[3]), false))
                    .toList();

            CSVReader hoteisCsv = new CSVReaderBuilder(new FileReader("src/main/files/Hoteis.csv")).build();
            List<String[]> hoteisString = hoteisCsv.readAll();

            List<Localidade> hoteisArrayList = hoteisString.stream()
                    .skip(1)
                    .map(item -> new Localidade(item[1], Double.parseDouble(item[3]), Double.parseDouble(item[4]), 0, true))
                    .toList();

            
            AntColonyOptimization aco = new AntColonyOptimization(1.0, 5, 0.9, 5, 0.01, 1000, 20, localidadeArrayList, hoteisArrayList);
            
            List<Caminhao> melhorCaminho = aco.comecarOtimizacao();
            
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new PanAndZoom(melhorCaminho, localidadeArrayList, hoteisArrayList);
                }
            });

            KmlFile kmlFile = new KmlFile();
            kmlFile.criarArquivo(melhorCaminho);


        } catch (Exception e) {
            System.out.println("Erro ao ler arquivo " + e.getMessage());
        }
        
        
        System.out.println("Finalizado ");
    }

}
