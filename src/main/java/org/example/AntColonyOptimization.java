package org.example;

import java.util.ArrayList;
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
 *
 * TODO lembrar de adicionar a volta ao inicio
 */

public class AntColonyOptimization {
    public String s = "";
    private final double alpha;
    private final double beta;
    private final double evaporacao;
    private final double Q;
    private final double fatorAleatoriedade;
    
    private final int interacoesMaximas;
    
    private final int qtdCidades;
    
    private final ArrayList<Cidade> cidades;
    private final List<Veiculo> veiculos = new ArrayList<>();
    private final Random random = new Random();
    
    private int tentativaAtual;
    
    private ArrayList<Cidade> melhorCaminho;
    private double comprimentoMelhorCaminho;
    
    public AntColonyOptimization(double alpha, double beta, double evaporacao, double q, double fatorAleatoriedade, int interacoesMaximas, int qtdFormigas) {
        this.alpha = alpha;
        this.beta = beta;
        this.evaporacao = evaporacao;
        Q = q;
        this.fatorAleatoriedade = fatorAleatoriedade;
        this.interacoesMaximas = interacoesMaximas;
        
        cidades = gerarMatrixAleatoria();
        qtdCidades = 5;
        
        for (int i = 0; i < qtdFormigas; i++)
            veiculos.add(new Veiculo(cidades));
    }
    
    
    public ArrayList<Cidade> gerarMatrixAleatoria() {
        ArrayList<Cidade> cidadeArrayList = new ArrayList<>();
        
        cidadeArrayList.add(new Cidade("Cidade1", 1, 1, 0));
        cidadeArrayList.add(new Cidade("Cidade2", 1, 3, 5));
        cidadeArrayList.add(new Cidade("Cidade3", 1, 5, 5));
        cidadeArrayList.add(new Cidade("Cidade4", 2, 2, 5));
        cidadeArrayList.add(new Cidade("Cidade5", 2, 5, 5));
        
        return cidadeArrayList;
    }
    
    public void comecarOtimizacao() {
        for (int i = 1; i <= interacoesMaximas; i++) {
            tentativaAtual = i;
            s += ("\nTentaiva #" + i);
            otimizar();
            s += "\n";
        }
    }
    
    public ArrayList<Cidade> otimizar() {
        resetarFormigas();
        if(tentativaAtual % 10 == 0) {
            limparFeromonioRotas();
        }
        moverFormigas();
        atualizarFeromonioRotas();
        autalizarMelhorSolucao();
        s += ("\nMelhor caminho comprimento: " + (comprimentoMelhorCaminho - qtdCidades));
        
        String caminho = "";
        
        for (Cidade cidade : melhorCaminho) {
            caminho += cidade.getNome() + " -> ";
        }
        
        s += ("\nMelhor caminho ordem: " + caminho);
        return new ArrayList<>(melhorCaminho);
    }
    
    private void resetarFormigas() {
        for (Veiculo veiculo : veiculos) {
            veiculo.limpar();
            veiculo.visitarCidade(cidades.get(0));
        }
    }
    
    private void moverFormigas() {
        for (Veiculo veiculo : veiculos) {
            while (!veiculo.finalizouPercurso(cidades)) veiculo.visitarCidade(selecionarProximaCidade(veiculo));
        }
    }
    
    private Cidade selecionarProximaCidade(Veiculo veiculo) {
        if (veiculo.podeVoltarDeposito(cidades)) return cidades.get(0);
        
        if (random.nextDouble() < fatorAleatoriedade) {
            return veiculo.escolherProxCidadeAleatoria();
        }
        calcularProbabilidadeCidades(veiculo);
        double r = random.nextDouble();
        Double total = 0d;
        for (Cidade cidade : cidades) {
            total += cidade.getProbabilidade();
            if (total >= r) return cidade;
        }
        
        throw new RuntimeException("Não possui outras cidades");
    }
    
    public void calcularProbabilidadeCidades(Veiculo veiculo) {
        Cidade cidadeAtual = veiculo.getUltimaCidade();
        double totalFeromonio = 0.0;
        for (Cidade cidade : cidades) {
            if (!veiculo.visitouCidade(cidade.getNome()))
                totalFeromonio += Math.pow(cidadeAtual.getFeromonio(cidade.getNome()), alpha) * Math.pow(1.0 / cidadeAtual.calcularDistancia(cidade), beta);
        }
        for (Cidade cidade : cidades) {
            if (veiculo.visitouCidade(cidade.getNome())) cidade.setProbabilidade(0);
            else {
                double numerator = Math.pow(cidadeAtual.getFeromonio(cidade.getNome()), alpha) * Math.pow(1.0 / cidadeAtual.calcularDistancia(cidade), beta);
                cidade.setProbabilidade(numerator / totalFeromonio);
            }
        }
    }
    
    private void atualizarFeromonioRotas() {
        for (Cidade cidadeA : cidades) {
            for (Cidade cidadeB : cidades)
                cidadeA.setFeromonio(cidadeB.getNome(), cidadeA.getFeromonio(cidadeB.getNome()) * evaporacao);
        }
        for (Formiga formiga : veiculos) {
            double contribuicao = Q / formiga.distanciaPercorrida();
            for (int i = 0; i < formiga.cidadesVisitadas.size() - 1; i++) {
                Cidade cidadeA = formiga.cidadesVisitadas.get(i);
                Cidade cidadeB = formiga.cidadesVisitadas.get(i + 1);
                
                if (cidadeB != null)
                    cidadeA.setFeromonio(cidadeB.getNome(), cidadeA.getFeromonio(cidadeB.getNome()) * contribuicao);
            }
            //feromonios[formiga.caminho[qtdCidades - 1]][formiga.caminho[0]] += contribution;
        }
    }
    
    private void autalizarMelhorSolucao() {
        if (melhorCaminho == null) {
            melhorCaminho = veiculos.get(0).cidadesVisitadas;
            comprimentoMelhorCaminho = veiculos.get(0).distanciaPercorrida();
        }
        
        for (Formiga a : veiculos) {
            if (a.distanciaPercorrida() < comprimentoMelhorCaminho) {
                comprimentoMelhorCaminho = a.distanciaPercorrida();
                melhorCaminho = new ArrayList<>(a.cidadesVisitadas);
            }
        }
    }
    
    private void limparFeromonioRotas() {
        for (Cidade cidade : cidades) {
            cidade.limparFeromonios();
        }
    }
}