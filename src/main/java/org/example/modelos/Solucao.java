package org.example.modelos;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class Solucao {
    
    public ArrayList<Formiga> formigas;
    
    public List<Localidade> localidades;

    public  List<Localidade> hoteis;
    
    public Set<String> localidadesVisitadas;
    
    public Solucao(@NotNull List<Localidade> localidades, List<Localidade> hoteis) {
        this.localidades = new ArrayList<>();
        this.formigas = new ArrayList<>();
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
        
        for (Formiga formiga : formigas) {
            Localidade deposito = formiga.cidadesVisitadas.get(0);
            Localidade ultimaLocalidade = formiga.cidadesVisitadas.get(formiga.cidadesVisitadas.size() - 1);
            
            todosCaminhoesVoltaramAoDeposito = deposito.getNome().equals(ultimaLocalidade.getNome());
        }
        
        boolean finalizou = !possuiLocalidadeNaoAtendidaDentroDaCapacidade && todosCaminhoesVoltaramAoDeposito;
        
        return finalizou;
    }
    
    public boolean utilizarNovaFormiga() {
        if (this.formigas.isEmpty()) {
            this.formigas.add(new Formiga(localidades, hoteis));
            
            return true;
        }
        
        Formiga ultimaFormiga = getUltimoCaminhao();
        Localidade deposito = ultimaFormiga.cidadesVisitadas.get(0);
        Localidade ultimaLocalidade = ultimaFormiga.cidadesVisitadas.get(ultimaFormiga.cidadesVisitadas.size() - 1);
        
        if (!deposito.getNome().equals(ultimaLocalidade.getNome())) return false;
        
        if(ultimaFormiga.getQtdCarga() == 0) {
            throw new RuntimeException("Gerando caminhão infinitos");
        }
        
        this.formigas.add(new Formiga(localidades, hoteis));
        return true;
    }
    
    public Viagem escolherProxCidadeAleatoria() {
        Formiga formiga = formigas.get(formigas.size() - 1);
        
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
    
    public void visitarLocalidade(@NotNull Viagem viagem) {
        Formiga ultimaFormiga = getUltimoCaminhao();
        localidadesVisitadas.add(viagem.localidade.getNome());
        for (Localidade l : localidades) {
            if (l.getNome().equals(viagem.localidade.getNome())) l.setRecebeuEntrega(true);
        }
        ultimaFormiga.visitarLocalidade(viagem);
    }
    
    public boolean visitouLocalidade(@NotNull Localidade localidade) {
        return localidadesVisitadas.contains(localidade.getNome());
    }
    
    public List<Viagem> getPossiveisLocalidadesParaVisitar() {
        Formiga formiga = formigas.get(formigas.size() - 1);
        return localidades.stream()
                .filter(localidade -> !localidade.recebeuEntrega())
                .map(formiga::podeVisitarCidade)
                .filter(viagem ->
                    viagem.tipoViagem != TipoViagem.IMPOSSIVEL
                )
                .collect(Collectors.toList());
    }
    
    public Formiga getUltimoCaminhao() {
        return this.formigas.get(this.formigas.size() - 1);
    }
    
    public void limpar() {
        ArrayList<Localidade> novasLocalidades = new ArrayList<>();
        for (Localidade l : localidades) {
            novasLocalidades.add(l.copiar());
        }
        this.formigas = new ArrayList<>();
        this.localidadesVisitadas = new HashSet<>();
        
        //inicia no deposito
        this.localidadesVisitadas.add(localidades.get(0).getNome());
        this.localidades = novasLocalidades;
    }
    
    public double distanciaPercorrida() {
        double distanciaTotal = 0;
        for (Formiga formiga : formigas) {
            for (int i = 0; i < formiga.cidadesVisitadas.size() - 1; i++) {
                Localidade localidade = formiga.cidadesVisitadas.get(i);
                Localidade localidade2 = formiga.cidadesVisitadas.get(i + 1);
                distanciaTotal += localidade.calcularDistancia(localidade2);
            }
        }
        
        return distanciaTotal;
    }
    
    
}
