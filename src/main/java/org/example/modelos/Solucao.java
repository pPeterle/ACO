package org.example.modelos;

import java.util.*;
import java.util.stream.Collectors;

public class Solucao {
    
    public ArrayList<Formiga> caminhoes;
    
    public List<Localidade> localidades;

    public  List<Localidade> hoteis;
    
    public Set<String> localidadesVisitadas;
    
    public Solucao(List<Localidade> localidades, List<Localidade> hoteis) {
        this.localidades = new ArrayList<>();
        this.caminhoes = new ArrayList<>();
        this.localidadesVisitadas = new HashSet<>();
        this.hoteis = hoteis;
        
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
        
        for (Formiga formiga : caminhoes) {
            Localidade deposito = formiga.cidadesVisitadas.get(0);
            Localidade ultimaLocalidade = formiga.cidadesVisitadas.get(formiga.cidadesVisitadas.size() - 1);
            
            todosCaminhoesVoltaramAoDeposito = deposito.getNome().equals(ultimaLocalidade.getNome());
        }
        
        boolean finalizou = !possuiLocalidadeNaoAtendidaDentroDaCapacidade && todosCaminhoesVoltaramAoDeposito;
        
        return finalizou;
    }
    
    public boolean utilizarNovoCaminhao() {
        if (this.caminhoes.isEmpty()) {
            this.caminhoes.add(new Formiga(localidades, hoteis));
            
            return true;
        }
        
        Formiga ultimaFormiga = getUltimoCaminhao();
        Localidade deposito = ultimaFormiga.cidadesVisitadas.get(0);
        Localidade ultimaLocalidade = ultimaFormiga.cidadesVisitadas.get(ultimaFormiga.cidadesVisitadas.size() - 1);
        
        if (!deposito.getNome().equals(ultimaLocalidade.getNome())) return false;
        
        if(ultimaFormiga.getQtdCarga() == 0) {
            throw new RuntimeException("Gerando caminhão infinitos");
        }
        
        this.caminhoes.add(new Formiga(localidades, hoteis));
        return true;
    }
    
    public Viagem escolherProxCidadeAleatoria() {
        Formiga formiga = caminhoes.get(caminhoes.size() - 1);
        
        ArrayList<Viagem> possiveisViagens = new ArrayList<>();
        for (Localidade localidade : localidades) {
            boolean recebeuEntrega = localidade.recebeuEntrega();
            Viagem viagem = formiga.podeVisitarCidade(localidade);
            boolean podeVisitarCidade = viagem.tipoViagem != TipoViagem.IMPOSSIVEL;
            if (!recebeuEntrega && podeVisitarCidade) possiveisViagens.add(viagem);
        }
        
        Random random = new Random();
        return possiveisViagens.get(random.nextInt(possiveisViagens.size()));
    }
    
    public void visitarLocalidade(Viagem viagem) {
        Formiga ultimaFormiga = getUltimoCaminhao();
        localidadesVisitadas.add(viagem.localidade.getNome());
        for (Localidade l : localidades) {
            if (l.getNome().equals(viagem.localidade.getNome())) l.setRecebeuEntrega(true);
        }
        ultimaFormiga.visitarLocalidade(viagem);
    }
    
    public boolean visitouLocalidade(Localidade localidade) {
        return localidadesVisitadas.contains(localidade.getNome());
    }
    
    public List<Viagem> getPossiveisLocalidadesParaVisitar() {
        Formiga formiga = caminhoes.get(caminhoes.size() - 1);
        return localidades.stream()
                .filter(localidade -> !localidade.recebeuEntrega())
                .map(formiga::podeVisitarCidade)
                .filter(viagem ->
                    viagem.tipoViagem != TipoViagem.IMPOSSIVEL
                )
                .collect(Collectors.toList());
    }
    
    public Formiga getUltimoCaminhao() {
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
    
    public double distanciaPercorrida() {
        double distanciaTotal = 0;
        for (Formiga formiga : caminhoes) {
            for (int i = 0; i < formiga.cidadesVisitadas.size() - 1; i++) {
                Localidade localidade = formiga.cidadesVisitadas.get(i);
                Localidade localidade2 = formiga.cidadesVisitadas.get(i + 1);
                distanciaTotal += localidade.calcularDistancia(localidade2);
            }
        }
        
        return distanciaTotal;
    }
    
    
}
