package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.example.modelos.*;
import org.example.view.KmlFile;
import org.example.view.PanAndZoom;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class Rotas {
    public static void main(String[] args) {
        try {

            CSVReader localidadesFile = new CSVReaderBuilder(new FileReader("src/main/files/Demanda.csv")).build();
            List<String[]> localidadesString = localidadesFile.readAll();

            List<Localidade> localidadeArrayList = localidadesString.stream()
                    .skip(1)
                    .map(item -> new Localidade(item[0], Double.parseDouble(item[1]), Double.parseDouble(item[2]), Integer.parseInt(item[3]), false))
                    .toList();

            CSVReader rotasFile = new CSVReaderBuilder(new FileReader("src/main/files/Rotas.csv")).build();
            List<String[]> rotasString = rotasFile.readAll();

            ArrayList<ArrayList<Localidade>> caminhoes = new ArrayList<>();

            rotasString.stream()
                    .skip(1)
                    .forEach(item -> {
                        if(caminhoes.isEmpty()) {
                            caminhoes.add(new ArrayList<>());
                        }
                        System.out.println(item[2]);

                        ArrayList<Localidade> localidadesVisitadas = caminhoes.get(caminhoes.size() - 1);
                        Localidade localidade = localidadeArrayList.stream()
                                .filter(localidade1 -> localidade1.getNome().equals(item[2]))
                                .toList()
                                .get(0);

                        localidadesVisitadas.add(localidade);
                        if(localidade.getNome().equals("DEPÓSITO") && localidadesVisitadas.size() > 1) {
                            caminhoes.add(new ArrayList<>());
                        }
                    });


            double distanciaTotal = 0d;
            for (ArrayList<Localidade> rotas: caminhoes) {
                for (int i = 0; i < rotas.size() - 1; i++) {
                    Localidade localidade = rotas.get(i);
                    Localidade localidade2 = rotas.get(i + 1);
                    distanciaTotal += localidade.calcularDistancia(localidade2);
                }
            }
            Double total = (distanciaTotal * AntColonyOptimization.custoPoKm) + ((caminhoes.size() -1) * AntColonyOptimization.custoCaminhao);
            System.out.printf("Resultado: %.3f", total);

            System.out.println(caminhoes.size());
            System.out.println(caminhoes.get(caminhoes.size() -1).size());




        } catch (Exception e) {
            System.out.println("Erro ao ler arquivo " + e.getMessage());
        }
        
        
        System.out.println("Finalizado ");
    }

}
