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

public class AntColonyOptimization
{
    public String s="";
    private final double c;             //number of trails
    private final double alpha;           //pheromone importance
    private final double beta;            //distance priority
    private final double evaporacao;
    private final double Q;             //pheromone left on trail per ant
    private final double qtdFormigasCidade;     //no of ants per node
    private final double fatorAleatoriedade; //introducing randomness
    
    private final int interacoesMaximas;
    
    private final int qtdCidades;
    private final int qtdFormigas;
    private final double[][] cidades;
    private final double[][] caminhos;
    private final List<Formiga> formigas = new ArrayList<>();
    private final Random random = new Random();
    private final double[] probabilidades;
    
    private int indexAtual;
    
    private int[] melhorCaminho;
    private double comprimentoMelhorCaminho;
    
    public AntColonyOptimization(double tr, double al, double be, double ev,
                                 double q, double af, double rf, int iter, int noOfCities)
    {
        c=tr; alpha=al; beta=be; evaporacao =ev; Q=q; qtdFormigasCidade =af; fatorAleatoriedade =rf; interacoesMaximas =iter;
        
        cidades = gerarMatrixAleatoria(noOfCities);
        qtdCidades = noOfCities;
        qtdFormigas = (int) (qtdCidades * qtdFormigasCidade);
        
        caminhos = new double[qtdCidades][qtdCidades];
        probabilidades = new double[qtdCidades];
        
        for(int i = 0; i< qtdFormigas; i++)
            formigas.add(new Formiga(qtdCidades));
    }
    
    /**
     * Generate initial solution
     */
    public double[][] gerarMatrixAleatoria(int n)
    {
        double[][] randomMatrix = new double[n][n];
        
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<n;j++)
            {
                if(i==j)
                    randomMatrix[i][j]=0;
                else
                    randomMatrix[i][j]=Math.abs(random.nextInt(100)+1);
            }
        }
        
        s+=("\t");
        for(int i=0;i<n;i++)
            s+=(i+"\t");
        s+="\n";
        
        for(int i=0;i<n;i++)
        {
            s+=(i+"\t");
            for(int j=0;j<n;j++)
                s+=(randomMatrix[i][j]+"\t");
            s+="\n";
        }
        
        int sum=0;
        
        for(int i=0;i<n-1;i++)
            sum+=randomMatrix[i][i+1];
        sum+=randomMatrix[n-1][0];
        s+=("\nNaive solution 0-1-2-...-n-0 = "+sum+"\n");
        return randomMatrix;
    }
    
    /**
     * Perform ant optimization
     */
    public void comecarOtimizacao()
    {
        for(int i=1;i<=5;i++)
        {
            s+=("\nAttempt #" +i);
            otimizar();
            s+="\n";
        }
    }
    
    /**
     * Use this method to run the main logic
     */
    public int[] otimizar()
    {
        resetarFormigas();
        limparFeromonioRotas();
        for(int i = 0; i< interacoesMaximas; i++)
        {
            moverFormigas();
            atualizarFeromonioRotas();
            autalizarMelhorSolucao();
        }
        s+=("\nBest tour length: " + (comprimentoMelhorCaminho - qtdCidades));
        s+=("\nBest tour order: " + Arrays.toString(melhorCaminho));
        return melhorCaminho.clone();
    }
    
    /**
     * Prepare ants for the simulation
     */
    private void resetarFormigas()
    {
        for(int i = 0; i< qtdFormigas; i++)
        {
            for(Formiga formiga : formigas)
            {
                formiga.clear();
                formiga.visitCity(-1, random.nextInt(qtdCidades));
            }
        }
        indexAtual = 0;
    }
    
    /**
     * At each iteration, move ants
     */
    private void moverFormigas()
    {
        for(int i = indexAtual; i< qtdCidades -1; i++)
        {
            for(Formiga formiga : formigas)
            {
                formiga.visitCity(indexAtual, selecionarProximaCidade(formiga));
            }
            indexAtual++;
        }
    }
    
    /**
     * Select next city for each ant
     */
    private int selecionarProximaCidade(Formiga formiga)
    {
        int t = random.nextInt(qtdCidades - indexAtual);
        if (random.nextDouble() < fatorAleatoriedade)
        {
            int cityIndex=-999;
            for(int i = 0; i< qtdCidades; i++)
            {
                if(i==t && !formiga.visited(i))
                {
                    cityIndex=i;
                    break;
                }
            }
            if(cityIndex!=-999)
                return cityIndex;
        }
        calcularProbabilidadeCidades(formiga);
        double r = random.nextDouble();
        double total = 0;
        for (int i = 0; i < qtdCidades; i++)
        {
            total += probabilidades[i];
            if (total >= r)
                return i;
        }
        throw new RuntimeException("There are no other cities");
    }
    
    /**
     * Calculate the next city picks probabilites
     */
    public void calcularProbabilidadeCidades(Formiga formiga)
    {
        int i = formiga.caminho[indexAtual];
        double pheromone = 0.0;
        for (int l = 0; l < qtdCidades; l++)
        {
            if (!formiga.visited(l))
                pheromone += Math.pow(caminhos[i][l], alpha) * Math.pow(1.0 / cidades[i][l], beta);
        }
        for (int j = 0; j < qtdCidades; j++)
        {
            if (formiga.visited(j))
                probabilidades[j] = 0.0;
            else
            {
                double numerator = Math.pow(caminhos[i][j], alpha) * Math.pow(1.0 / cidades[i][j], beta);
                probabilidades[j] = numerator / pheromone;
            }
        }
    }
    
    /**
     * Update trails that ants used
     */
    private void atualizarFeromonioRotas()
    {
        for (int i = 0; i < qtdCidades; i++)
        {
            for (int j = 0; j < qtdCidades; j++)
                caminhos[i][j] *= evaporacao;
        }
        for (Formiga a : formigas)
        {
            double contribution = Q / a.trailLength(cidades);
            for (int i = 0; i < qtdCidades - 1; i++)
                caminhos[a.caminho[i]][a.caminho[i + 1]] += contribution;
            caminhos[a.caminho[qtdCidades - 1]][a.caminho[0]] += contribution;
        }
    }
    
    /**
     * Update the best solution
     */
    private void autalizarMelhorSolucao()
    {
        if (melhorCaminho == null)
        {
            melhorCaminho = formigas.get(0).caminho;
            comprimentoMelhorCaminho = formigas.get(0).trailLength(cidades);
        }
        
        for (Formiga a : formigas)
        {
            if (a.trailLength(cidades) < comprimentoMelhorCaminho)
            {
                comprimentoMelhorCaminho = a.trailLength(cidades);
                melhorCaminho = a.caminho.clone();
            }
        }
    }
    
    /**
     * Clear trails after simulation
     */
    private void limparFeromonioRotas()
    {
        for(int i = 0; i< qtdCidades; i++)
        {
            for(int j = 0; j< qtdCidades; j++)
                caminhos[i][j]=c;
        }
    }
}