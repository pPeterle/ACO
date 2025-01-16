package org.example;

import me.tongfei.progressbar.ProgressBar;
import org.example.modelos.*;

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
 */

public class AntColonyOptimization {
    private final double alpha;
    private final double beta;
    private final double evaporacao;
    private final double Q;
    private final double fatorAleatoriedade;
    
    private final int interacoesMaximas;

    public static final int custoCaminhao = 15000;
    public static final double custoPoKm = 1.1;
    
    private final List<Localidade> localidades;
    private final List<Solucao> solucoes = new ArrayList<>();
    private final Random random = new Random();
    
    private List<Formiga> melhorCaminho;
    private double custoMelhorCaminho;
    
    public AntColonyOptimization(double alpha, double beta, double evaporacao, double q, double fatorAleatoriedade, int interacoesMaximas, int qtdFormigas, List<Localidade> localidades, List<Localidade> hoteis) {
        this.alpha = alpha;
        this.beta = beta;
        this.evaporacao = evaporacao;
        Q = q;
        this.fatorAleatoriedade = fatorAleatoriedade;
        this.interacoesMaximas = interacoesMaximas;
        
        this.localidades = localidades;
        
        for (int i = 0; i < qtdFormigas; i++)
            solucoes.add(new Solucao(localidades, hoteis));
    }

    public List<Formiga> comecarOtimizacao() {
        ProgressBar pb = new ProgressBar("Iterações", interacoesMaximas);
        pb.start();
        for (int i = 1; i <= interacoesMaximas; i++) {
            pb.step();
            otimizar();
        }
        pb.stop();
        
        exibirRelatorio();
        
        return melhorCaminho;
    }
    
    public void exibirRelatorio() {
        
//        StringBuilder caminho = new StringBuilder();
//
//        for (int i = 0; i < melhorCaminho.size(); i++) {
//            Caminhao caminhao = melhorCaminho.get(i);
//            caminho.append("\nCaminhão ").append(i + 1).append("\n");
//            caminho.append(caminhao.getHistorico());
//        }
//
//        caminho.append("\n").append("CUSTO TOTAL: ").append(custoMelhorCaminho);
//
//        System.out.println("\nMelhor caminho ordem: " + caminho);
    }
    
    public void otimizar() {
        resetarFormigas();
        limparFeromonioRotas();
        moverFormigas();
        atualizarFeromonioRotas();
        autalizarMelhorSolucao();
    }
    
    private void resetarFormigas() {
        for (Solucao solucao : solucoes) {
            solucao.limpar();
        }
    }
    
    private void moverFormigas() {
        for (Solucao solucao : solucoes) {
            while (!solucao.finalizouPercurso()) {
                solucao.utilizarNovoCaminhao();
                Viagem viagem = selecionarProximaViagem(solucao);
                solucao.visitarLocalidade(viagem);
            }
            
        }
    }
    
    private Viagem selecionarProximaViagem(Solucao solucao) {
        if (solucao.getUltimoCaminhao().podeVoltarDeposito()) return new Viagem(localidades.get(0), TipoViagem.VIAGEM_ENTRE_CIDADES);
        
        if (random.nextDouble() < fatorAleatoriedade) {
            return solucao.escolherProxCidadeAleatoria();
        }
        
        List<Viagem> possiveisLocalidades = solucao.getPossiveisLocalidadesParaVisitar();
        
        calcularProbabilidadeCidades(solucao, possiveisLocalidades);
        double r = random.nextDouble();
        Double total = 0d;
        for (Viagem viagem : possiveisLocalidades) {
            total += viagem.getProbabilidade();
            if (total >= r) return viagem;
        }
        
        throw new RuntimeException("Não possui outras cidades " + possiveisLocalidades.size());
    }
    
    public void calcularProbabilidadeCidades(Solucao solucao, List<Viagem> possiveisViagens) {
        Localidade localidadeAtual = solucao.getUltimoCaminhao().getUltimaLocalidade();
        double totalFeromonio = 0.0;
        for (Viagem viagem : possiveisViagens) {
            Localidade localidade = viagem.localidade;
            if (!solucao.visitouLocalidade(localidade)) {
                Double feromonio = localidadeAtual.getFeromonio(localidade.getNome());
                double distancia = localidadeAtual.calcularDistancia(localidade);
                totalFeromonio += Math.pow(feromonio, alpha) * Math.pow(1.0 / distancia, beta);
            }
        }
        for (Viagem viagem : possiveisViagens) {
            Localidade localidade = viagem.localidade;
            if (solucao.visitouLocalidade(localidade)) viagem.setProbabilidade(0d);
            else {
                double numerator = Math.pow(localidadeAtual.getFeromonio(localidade.getNome()), alpha) * Math.pow(1.0 / localidadeAtual.calcularDistancia(localidade), beta);
                //System.out.println("numerador: " + numerator + " denominador " + totalFeromonio);
                viagem.setProbabilidade(numerator / totalFeromonio);
            }
        }
    }
    
    private void atualizarFeromonioRotas() {
        for (Localidade localidadeA : localidades) {
            for (Localidade localidadeB : localidades) {
                double feromonioAtualizado = localidadeA.getFeromonio(localidadeB.getNome()) * (1 - evaporacao);
                localidadeA.setFeromonio(localidadeB.getNome(), feromonioAtualizado);
            }
            
        }
        for (Solucao solucao : solucoes) {
            double contribuicao = Q / solucao.distanciaPercorrida();
            for (Formiga formiga : solucao.caminhoes) {
                for (int i = 0; i < formiga.cidadesVisitadas.size() - 1; i++) {
                    Localidade localidadeA = formiga.cidadesVisitadas.get(i);
                    Localidade localidadeB = formiga.cidadesVisitadas.get(i + 1);
                    
                    if (localidadeB != null)
                        localidadeA.setFeromonio(localidadeB.getNome(), localidadeA.getFeromonio(localidadeB.getNome()) * contribuicao);
                }
            }
        }
    }
    
    private void autalizarMelhorSolucao() {
        if (melhorCaminho == null || melhorCaminho.isEmpty()) {
            melhorCaminho = solucoes.get(0).caminhoes;
            custoMelhorCaminho = calcularCusto(solucoes.get(0));
        }
        
        for (Solucao solucao : solucoes) {
            double custo = calcularCusto(solucao);
            if (custo < custoMelhorCaminho) {
                custoMelhorCaminho = custo;
                melhorCaminho = new ArrayList<>(solucao.caminhoes);
            }
        }
    }

    public static double calcularCusto(Solucao solucao) {
        return (solucao.distanciaPercorrida() * custoPoKm) + (solucao.caminhoes.size() * custoCaminhao) ;
    }
    
    private void limparFeromonioRotas() {
        for (Localidade localidade : localidades) {
            localidade.limparFeromonios();
        }
    }
}