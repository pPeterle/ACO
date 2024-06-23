package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/*
 * default
 * private double c = 1.0;             //number of trails
 * private double alpha = 1;           //pheromone importance
 * private double beta = 5;            //distance priority
 * private double evaporation = 0.5;
 * private double Q = 500;             //pheromone left on trail per ant
 * private double antFactor = 0.8;     //no of ants per node
 * private double randomFactor = 0.01; //introducing randomness
 * private int maxIterations = 1000;
 */

public class AntColonyOptimization {
    public String s = "";
    private final double c;
    private final double alpha;
    private final double beta;
    private final double evaporacao;
    private final double Q;
    private final double qtdFormigasCidade;
    private final double fatorAleatoriedade;
    
    private final int interacoesMaximas;
    
    private final int qtdCidades;
    private final int qtdFormigas;
    private final double[][] cidades;
    private final double[][] feromonios;
    private final List<Formiga> formigas = new ArrayList<>();
    private final Random random = new Random();
    private final double[] probabilidades;
    
    private int indexAtual;
    
    private int[] melhorCaminho;
    private double comprimentoMelhorCaminho;
    
    public AntColonyOptimization(double tr, double al, double be, double ev, double q, double af, double rf, int iter, int noOfCities) {
        c = tr;
        alpha = al;
        beta = be;
        evaporacao = ev;
        Q = q;
        qtdFormigasCidade = af;
        fatorAleatoriedade = rf;
        interacoesMaximas = iter;
        
        cidades = gerarMatrixAleatoria(noOfCities);
        qtdCidades = noOfCities;
        qtdFormigas = (int) (qtdCidades * qtdFormigasCidade);
        
        feromonios = new double[qtdCidades][qtdCidades];
        probabilidades = new double[qtdCidades];
        
        for (int i = 0; i < qtdFormigas; i++)
            formigas.add(new Formiga(qtdCidades));
    }
    
    
    public double[][] gerarMatrixAleatoria(int n) {
        double[][] randomMatrix = new double[n][n];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) randomMatrix[i][j] = 0;
                else randomMatrix[i][j] = Math.abs(random.nextInt(100) + 1);
            }
        }
        
        s += ("\t");
        for (int i = 0; i < n; i++)
            s += (i + "\t");
        s += "\n";
        
        for (int i = 0; i < n; i++) {
            s += (i + "\t");
            for (int j = 0; j < n; j++)
                s += (randomMatrix[i][j] + "\t");
            s += "\n";
        }
        
        int sum = 0;
        
        for (int i = 0; i < n - 1; i++)
            sum += randomMatrix[i][i + 1];
        sum += randomMatrix[n - 1][0];
        s += ("\nMatrix 0-1-2-...-n-0 = " + sum + "\n");
        return randomMatrix;
    }
    
    public void comecarOtimizacao() {
        for (int i = 1; i <= 5; i++) {
            s += ("\nTentaiva #" + i);
            otimizar();
            s += "\n";
        }
    }
    
    public int[] otimizar() {
        resetarFormigas();
        limparFeromonioRotas();
        for (int i = 0; i < interacoesMaximas; i++) {
            moverFormigas();
            atualizarFeromonioRotas();
            autalizarMelhorSolucao();
        }
        s += ("\nMelhor caminho comprimento: " + (comprimentoMelhorCaminho - qtdCidades));
        s += ("\nMelhor caminho ordem: " + Arrays.toString(melhorCaminho));
        return melhorCaminho.clone();
    }
    
    private void resetarFormigas() {
        for (int i = 0; i < qtdFormigas; i++) {
            for (Formiga formiga : formigas) {
                formiga.limpar();
                formiga.visitarCidade(0, random.nextInt(qtdCidades));
            }
        }
        indexAtual = 0;
    }
    
    private void moverFormigas() {
        for (int i = indexAtual; i < qtdCidades - 1; i++) {
            for (Formiga formiga : formigas) {
                formiga.visitarCidade(indexAtual + 1, selecionarProximaCidade(formiga));
            }
            indexAtual++;
        }
    }
    
    private int selecionarProximaCidade(Formiga formiga) {
        if (random.nextDouble() < fatorAleatoriedade) {
            int cityIndex = -999;
            int cidadeAleatoriaEscolhida = random.nextInt(qtdCidades - indexAtual);
            for (int indexCidade = 0; indexCidade < qtdCidades; indexCidade++) {
                if (indexCidade == cidadeAleatoriaEscolhida && !formiga.visitouCidade(indexCidade)) {
                    cityIndex = indexCidade;
                    break;
                }
            }
            if (cityIndex != -999) return cityIndex;
        }
        calcularProbabilidadeCidades(formiga);
        double r = random.nextDouble();
        double total = 0;
        for (int i = 0; i < qtdCidades; i++) {
            total += probabilidades[i];
            if (total >= r) return i;
        }
        throw new RuntimeException("Não possui outras cidades");
    }
    
    public void calcularProbabilidadeCidades(Formiga formiga) {
        int cidadeAtual = formiga.caminho[indexAtual];
        double totalFeromonio = 0.0;
        for (int l = 0; l < qtdCidades; l++) {
            if (!formiga.visitouCidade(l))
                totalFeromonio += Math.pow(feromonios[cidadeAtual][l], alpha) * Math.pow(1.0 / cidades[cidadeAtual][l], beta);
        }
        for (int j = 0; j < qtdCidades; j++) {
            if (formiga.visitouCidade(j)) probabilidades[j] = 0.0;
            else {
                double numerator = Math.pow(feromonios[cidadeAtual][j], alpha) * Math.pow(1.0 / cidades[cidadeAtual][j], beta);
                probabilidades[j] = numerator / totalFeromonio;
            }
        }
    }
    
    private void atualizarFeromonioRotas() {
        for (int i = 0; i < qtdCidades; i++) {
            for (int j = 0; j < qtdCidades; j++)
                feromonios[i][j] *= evaporacao;
        }
        for (Formiga a : formigas) {
            double contribution = Q / a.distanciaPercorrida(cidades);
            for (int i = 0; i < qtdCidades - 1; i++)
                feromonios[a.caminho[i]][a.caminho[i + 1]] += contribution;
            feromonios[a.caminho[qtdCidades - 1]][a.caminho[0]] += contribution;
        }
    }
    
    private void autalizarMelhorSolucao() {
        if (melhorCaminho == null) {
            melhorCaminho = formigas.get(0).caminho;
            comprimentoMelhorCaminho = formigas.get(0).distanciaPercorrida(cidades);
        }
        
        for (Formiga a : formigas) {
            if (a.distanciaPercorrida(cidades) < comprimentoMelhorCaminho) {
                comprimentoMelhorCaminho = a.distanciaPercorrida(cidades);
                melhorCaminho = a.caminho.clone();
            }
        }
    }
    
    private void limparFeromonioRotas() {
        for (int i = 0; i < qtdCidades; i++) {
            for (int j = 0; j < qtdCidades; j++)
                feromonios[i][j] = c;
        }
    }
}