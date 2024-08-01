package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class Formiga {
    
    public ArrayList<Caminhao> caminhoes;
    
    public ArrayList<Localidade> localidades;
    
    public Set<String> localidadesVisitadas;
    
    Formiga(ArrayList<Localidade> localidades) {
        this.localidades = new ArrayList<>();
        this.caminhoes = new ArrayList<>();
        this.localidadesVisitadas = new HashSet<>();
        
        //inicia no deposito
        this.localidadesVisitadas.add(localidades.get(0).getNome());
        
        for (Localidade l : localidades) {
            this.localidades.add(l.copiar());
        }
    }
    
    public boolean finalizouPercurso() {
        
        boolean possuiLocalidadeNaoAtendidaDentroDaCapacidade = false;
        for (Localidade localidade : localidades) {
            possuiLocalidadeNaoAtendidaDentroDaCapacidade = !localidade.recebeuEntrega();
            if(possuiLocalidadeNaoAtendidaDentroDaCapacidade) break;
        }
        
        boolean todosCaminhoesVoltaramAoDeposito = true;
        
        for (Caminhao caminhao : caminhoes) {
            Localidade deposito = caminhao.cidadesVisitadas.get(0);
            Localidade ultimaLocalidade = caminhao.cidadesVisitadas.get(caminhao.cidadesVisitadas.size() - 1);
            
            todosCaminhoesVoltaramAoDeposito = deposito.getNome().equals(ultimaLocalidade.getNome());
        }
        
        boolean finalizou = !possuiLocalidadeNaoAtendidaDentroDaCapacidade && todosCaminhoesVoltaramAoDeposito;
        
        return finalizou;
    }
    
    public boolean utilizarNovoCaminhao() {
        if (this.caminhoes.isEmpty()) {
            this.caminhoes.add(new Caminhao(localidades));
            
            return true;
        }
        
        Caminhao ultimoCaminhao = getUltimoCaminhao();
        Localidade deposito = ultimoCaminhao.cidadesVisitadas.get(0);
        Localidade ultimaLocalidade = ultimoCaminhao.cidadesVisitadas.get(ultimoCaminhao.cidadesVisitadas.size() - 1);
        
        if (!deposito.getNome().equals(ultimaLocalidade.getNome())) return false;
        
        this.caminhoes.add(new Caminhao(localidades));
        return true;
    }
    
    public Localidade escolherProxCidadeAleatoria() {
        Caminhao caminhao = caminhoes.get(caminhoes.size() - 1);
        
        ArrayList<Localidade> cidadesQueFaltamVisitar = new ArrayList<>();
        for (Localidade localidade : localidades) {
            boolean recebeuEntrega = localidade.recebeuEntrega();
            boolean podeVisitarCidade = caminhao.podeVisitarCidade(localidade);
            if (!recebeuEntrega && podeVisitarCidade) cidadesQueFaltamVisitar.add(localidade);
        }
        
        Random random = new Random();
        return cidadesQueFaltamVisitar.get(random.nextInt(cidadesQueFaltamVisitar.size()));
    }
    
    public void visitarLocalidade(Localidade localidade) {
        Caminhao ultimoCaminhao = getUltimoCaminhao();
        localidadesVisitadas.add(localidade.getNome());
        for (Localidade l : localidades) {
            if (l.getNome().equals(localidade.getNome())) l.setRecebeuEntrega(true);
        }
        ultimoCaminhao.visitarLocalidade(localidade);
    }
    
    public boolean visitouLocalidade(Localidade localidade) {
        return localidadesVisitadas.contains(localidade.getNome());
    }
    
    public List<Localidade> getPossiveisLocalidadesParaVisitar() {
        // verificar quais restricoes para visitar cidades
        Caminhao caminhao = caminhoes.get(caminhoes.size() - 1);
        return localidades.stream()
                .filter(localidade -> !localidade.recebeuEntrega() && caminhao.podeVisitarCidade(localidade))
                .collect(Collectors.toList());
    }
    
    public Caminhao getUltimoCaminhao() {
        return this.caminhoes.get(this.caminhoes.size() - 1);
    }
    
    public void limpar() {
        ArrayList<Localidade> novasLocalidades = new ArrayList<>();
        for (Localidade l : localidades) {
            novasLocalidades.add(l.copiar());
        }
        this.caminhoes = new ArrayList<>();
        this.localidadesVisitadas = new HashSet<>();
        
        //inicia no deposito
        this.localidadesVisitadas.add(localidades.get(0).getNome());
        this.localidades = novasLocalidades;
    }
    
    protected double distanciaPercorrida() {
        double distanciaTotal = 0;
        for (Caminhao caminhao : caminhoes) {
            for (int i = 0; i < caminhao.cidadesVisitadas.size() - 1; i++) {
                Localidade localidade = caminhao.cidadesVisitadas.get(i);
                Localidade localidade2 = caminhao.cidadesVisitadas.get(i + 1);
                distanciaTotal += localidade.calcularDistancia(localidade2);
            }
        }
        
        return distanciaTotal;
    }
    
    
}
