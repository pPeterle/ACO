package org.example.modelos;

import java.util.*;
import java.util.stream.Collectors;

public class Formiga {
    
    public ArrayList<Caminhao> caminhoes;
    
    public List<Localidade> localidades;

    public  List<Localidade> hoteis;
    
    public Set<String> localidadesVisitadas;
    
    public Formiga(List<Localidade> localidades, List<Localidade> hoteis) {
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
            this.caminhoes.add(new Caminhao(localidades, hoteis));
            
            return true;
        }
        
        Caminhao ultimoCaminhao = getUltimoCaminhao();
        Localidade deposito = ultimoCaminhao.cidadesVisitadas.get(0);
        Localidade ultimaLocalidade = ultimoCaminhao.cidadesVisitadas.get(ultimoCaminhao.cidadesVisitadas.size() - 1);
        
        if (!deposito.getNome().equals(ultimaLocalidade.getNome())) return false;
        
        if(ultimoCaminhao.getQtdCarga() == 0) {
            throw new RuntimeException("Gerando caminhão infinitos");
        }
        
        this.caminhoes.add(new Caminhao(localidades, hoteis));
        return true;
    }
    
    public Viagem escolherProxCidadeAleatoria() {
        Caminhao caminhao = caminhoes.get(caminhoes.size() - 1);
        
        ArrayList<Viagem> possiveisViagens = new ArrayList<>();
        for (Localidade localidade : localidades) {
            boolean recebeuEntrega = localidade.recebeuEntrega();
            Viagem viagem = caminhao.podeVisitarCidade(localidade);
            boolean podeVisitarCidade = viagem.tipoViagem != TipoViagem.IMPOSSIVEL;
            if (!recebeuEntrega && podeVisitarCidade) possiveisViagens.add(viagem);
        }
        
        Random random = new Random();
        return possiveisViagens.get(random.nextInt(possiveisViagens.size()));
    }
    
    public void visitarLocalidade(Viagem viagem) {
        Caminhao ultimoCaminhao = getUltimoCaminhao();
        localidadesVisitadas.add(viagem.localidade.getNome());
        for (Localidade l : localidades) {
            if (l.getNome().equals(viagem.localidade.getNome())) l.setRecebeuEntrega(true);
        }
        ultimoCaminhao.visitarLocalidade(viagem);
    }
    
    public boolean visitouLocalidade(Localidade localidade) {
        return localidadesVisitadas.contains(localidade.getNome());
    }
    
    public List<Viagem> getPossiveisLocalidadesParaVisitar() {
        Caminhao caminhao = caminhoes.get(caminhoes.size() - 1);
        return localidades.stream()
                .filter(localidade -> !localidade.recebeuEntrega())
                .map(caminhao::podeVisitarCidade)
                .filter(viagem ->
                    viagem.tipoViagem != TipoViagem.IMPOSSIVEL
                )
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
    
    public double distanciaPercorrida() {
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
