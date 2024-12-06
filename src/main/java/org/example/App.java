package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import de.micromata.opengis.kml.v_2_2_0.*;
import org.example.modelos.Caminhao;
import org.example.modelos.Localidade;
import org.example.modelos.Resultado;
import org.example.view.KmlFile;
import org.example.view.PanAndZoom;

import java.io.File;
import java.io.FileReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

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

            ThreadPoolExecutor executor =
                    (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

            List<Callable<Resultado>> tasks = new ArrayList<>();
            for (double i = 0; i < 5; i += 0.1) {

                AntColonyOptimization aco = new AntColonyOptimization(1.0 + i, 1.5 + (i * 1.5), 0.9, 1000, 0.01, 1000, 100, localidadeArrayList, hoteisArrayList);
                double finalI = i;
                tasks.add(new Callable<Resultado>() {
                    @Override
                    public Resultado call() throws Exception {
                        long tempoInicial = System.currentTimeMillis();
                        double custo =  aco.comecarOtimizacao();
                        long tempoTotal = System.currentTimeMillis() - tempoInicial;

                        return new Resultado(custo, tempoTotal, finalI);
                    }
                });
            }

            List<Future<Resultado>> resultados =  executor.invokeAll(tasks);
            Resultado melhorResultado = new Resultado(0, 0, 0);

            for (Future<Resultado> tarefaResultado: resultados) {
                Resultado resultado = tarefaResultado.get();

                if(resultado.getCustoTotal() < melhorResultado.getCustoTotal() || melhorResultado.getCustoTotal() == 0) {
                    melhorResultado = resultado;
                }

            }

            System.out.printf("O índice %f teve o  menor custo %.2f com o tempo de %d \n", melhorResultado.getIndex(),melhorResultado.getCustoTotal(), melhorResultado.getTempo() / 1000);




//            List<Caminhao> melhorCaminho = aco.comecarOtimizacao();
//
//            javax.swing.SwingUtilities.invokeLater(new Runnable() {
//                public void run() {
//                    new PanAndZoom(melhorCaminho, localidadeArrayList, hoteisArrayList);
//                }
//            });
//
//            KmlFile kmlFile = new KmlFile();
//            kmlFile.criarArquivo(melhorCaminho);


        } catch (Exception e) {
            System.out.println("Erro ao ler arquivo " + e.getMessage());
        }
        
        
        System.out.println(" Finalizado ");
    }

}
