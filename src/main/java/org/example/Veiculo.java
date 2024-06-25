package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Veiculo extends Formiga {
    
    final int velocidadeMediaKmPorMinuto = 1;
    
    // pode trabalhar no maximo 8 horas por dia, se trabalhar mais no outro dia é compensado
    final int jornadaDiaria = 8 * 60;
    
    boolean primeiroDia;
    
    // maximo 12 horas de trabalho no primeiro dia
    int jornadaDeTrabalhoDia1 = 12 * 60;
    int jornadaDeTrabalhoDia2;
    
    int qtdCarga = 50;
    
    Map<String, Integer> qtdCargaCidade;
    
    ArrayList<Cidade> cidades;
    
    Veiculo(ArrayList<Cidade> cidades) {
        primeiroDia = true;
        qtdCargaCidade = new HashMap<>();
        this.cidades = cidades;
        
        for (Cidade cidade : cidades) {
            qtdCargaCidade.put(cidade.getNome(), cidade.getQtdItensReceber());
        }
    }
    
    @Override
    protected void visitarCidade(Cidade cidade) {
        if (!cidadesVisitadas.isEmpty()) {
            Cidade ultimaCidade = cidadesVisitadas.get(cidadesVisitadas.size() - 1);
            
            if (cidade.getNome().equals(ultimaCidade.getNome())) return;
        }
        
        
        super.visitarCidade(cidade);
        
        int itemsAEntregar = qtdCargaCidade.get(cidade.getNome());
        
        if (qtdCarga > itemsAEntregar) {
            qtdCarga -= itemsAEntregar;
            qtdCargaCidade.put(cidade.getNome(), 0);
        } else {
            qtdCargaCidade.put(cidade.getNome(), itemsAEntregar - qtdCarga);
            qtdCarga = 0;
        }
        
        Cidade ultimaCidade = cidadesVisitadas.get(cidadesVisitadas.size() - 1);
        double distancia = ultimaCidade.calcularDistancia(cidade);
        // minutos do descolamento
        reduzirJornadaTrabalho((int) Math.ceil(distancia * velocidadeMediaKmPorMinuto));
        // minutos para descarga
        reduzirJornadaTrabalho(15);
    }
    
    @Override
    protected void limpar() {
        super.limpar();
        
        primeiroDia = true;
        qtdCargaCidade = new HashMap<>();
        qtdCarga = 50;
        jornadaDeTrabalhoDia1 = 12 * 60;
        
        for (Cidade cidade : cidades) {
            qtdCargaCidade.put(cidade.getNome(), cidade.getQtdItensReceber());
        }
    }
    
    protected boolean podeVoltarDeposito(ArrayList<Cidade> cidades) {
        if (qtdCarga <= 0) return true;
        
        Double totalCarga = 0d;
        for (Cidade cidade : cidades) {
            totalCarga += qtdCargaCidade.get(cidade.getNome());
        }
        
        return totalCarga == 0;
    }
    
    protected boolean finalizouPercurso(ArrayList<Cidade> cidades) {
        Double totalCarga = 0d;
        for (Cidade cidade : cidades) {
            totalCarga += qtdCargaCidade.get(cidade.getNome());
        }
        
        Cidade deposito = cidadesVisitadas.get(0);
        Cidade ultimaCidade = cidadesVisitadas.get(cidadesVisitadas.size() - 1);
        
        boolean voltouAoDeposito = deposito.getNome().equals(ultimaCidade.getNome());
        
        
        return totalCarga == 0 && voltouAoDeposito;
    }
    
    protected Cidade escolherProxCidadeAleatoria() {
        
        ArrayList<Cidade> cidadesQueFaltamVisitar = new ArrayList<>();
        for(Cidade cidade: cidades) {
            Integer qtd = qtdCargaCidade.get(cidade.getNome());
            if(qtd > 0) cidadesQueFaltamVisitar.add(cidade);
        }
        
        Random random = new Random();
        return cidadesQueFaltamVisitar.get(random.nextInt(cidadesQueFaltamVisitar.size()));
    }
    
    protected boolean podeVisitarCidade(Cidade proxCidade) {
        Cidade deposito = this.cidadesVisitadas.get(0);
        Cidade cidadeAtual = this.cidadesVisitadas.get(this.cidadesVisitadas.size() - 1);
        
        if (proxCidade.getQtdItensReceber() == 0) return false;
        
        Double distanciaProxCidade = cidadeAtual.calcularDistancia(proxCidade);
        Double distanciaVoltar = cidadeAtual.calcularDistancia(deposito);
        
        return getJornadaTrabalho() > ((distanciaProxCidade + distanciaVoltar) * velocidadeMediaKmPorMinuto);
    }
    
    protected void descansar() {
        jornadaDeTrabalhoDia2 = jornadaDiaria - (jornadaDeTrabalhoDia1 - jornadaDiaria);
        primeiroDia = false;
    }
    
    private int getJornadaTrabalho() {
        if (primeiroDia) return jornadaDeTrabalhoDia1;
        else return jornadaDeTrabalhoDia2;
    }
    
    private void reduzirJornadaTrabalho(int minutosTrabalhados) {
        if (primeiroDia) {
            jornadaDeTrabalhoDia1 -= minutosTrabalhados;
        } else {
            jornadaDeTrabalhoDia2 -= minutosTrabalhados;
        }
    }
    
    
}
