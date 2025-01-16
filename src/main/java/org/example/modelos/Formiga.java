package org.example.modelos;

import java.util.ArrayList;
import java.util.List;

public class Formiga {

    private final int velocidadeMediaKmPorMinuto = 1;

    private final int maxCarga = 120;

    private final int tempoDescarga = 15;
    final private StringBuilder historico;
    final private List<Localidade> localidades;
    final private List<Localidade> hoteis;
    private double jornadaDeTrabalhoDia1 = (8 * 60) + 30;
    
    public ArrayList<Localidade> cidadesVisitadas;
    private double jornadaDeTrabalhoDia2 = (8 * 60) + 30;
    private boolean primeiroDia = true;
    private int qtdCarga = 0;


    
    Formiga(List<Localidade> localidades, List<Localidade> hoteis) {
        this.localidades = localidades;
        this.cidadesVisitadas = new ArrayList<>();
        
        // inicia o caminhão do depósito
        this.cidadesVisitadas.add(localidades.get(0));
        this.historico = new StringBuilder();
        this.historico.append(localidades.get(0).getNome());
        this.hoteis = hoteis;
    }

    public void visitarLocalidade(Viagem viagem) {
        if (!cidadesVisitadas.isEmpty()) {
            Localidade ultimaLocalidade = cidadesVisitadas.get(cidadesVisitadas.size() - 1);

            if (viagem.localidade.getNome().equals(ultimaLocalidade.getNome())) throw new RuntimeException("Visitando a mesma cidade");
        }

        int novaCarga = viagem.localidade.getQtdItensReceber();
        
        qtdCarga += novaCarga;
        
        if (qtdCarga > maxCarga) {
            throw new RuntimeException("Carga ultrapassou a quantidade máxima do caminhão");
        }

        Localidade proxLocalidade = viagem.localidade;
        Localidade localidadeAtual = this.cidadesVisitadas.get(this.cidadesVisitadas.size() - 1);

        switch (viagem.tipoViagem) {
            case VIAGEM_ENTRE_CIDADES -> {
                double distanciaProxCidade = localidadeAtual.calcularDistancia(proxLocalidade);

                historico.append(" -> ").append(proxLocalidade.getNome()).append(" (").append(qtdCarga).append(") ");
                // minutos do descolamento
                reduzirJornadaTrabalho(distanciaProxCidade * velocidadeMediaKmPorMinuto);
                // minutos para descarga
                if(jornadaDeTrabalhoDia2 < 15) {
                    System.out.println("deu ruim");
                }
                if(proxLocalidade == this.cidadesVisitadas.get(0))
                reduzirJornadaTrabalho(tempoDescarga);

                historico.append(String.format("C-C: %.2f | %.2f | %.2f ", distanciaProxCidade * velocidadeMediaKmPorMinuto, jornadaDeTrabalhoDia1, jornadaDeTrabalhoDia2));

                cidadesVisitadas.add(proxLocalidade);
            }
            case ENTREGA_DORME -> {
                Localidade hotel = this.buscarHotelMaisProximo(proxLocalidade);

                double distanciaProxCidade = localidadeAtual.calcularDistancia(proxLocalidade);
                double distanciaIrDormirAPartirDaProxLocalidade = proxLocalidade.calcularDistancia(hotel);


                historico.append(" -> ").append(proxLocalidade.getNome()).append(" (").append(qtdCarga).append(") ");
                reduzirJornadaTrabalho(distanciaProxCidade * velocidadeMediaKmPorMinuto);
                reduzirJornadaTrabalho(tempoDescarga);

                historico.append(String.format("C-C: %.2f ", distanciaProxCidade * velocidadeMediaKmPorMinuto));
                cidadesVisitadas.add(proxLocalidade);

                //System.out.println("Indo ao hotel");
                reduzirJornadaTrabalho(distanciaIrDormirAPartirDaProxLocalidade * velocidadeMediaKmPorMinuto);
                historico.append("Fez a enterga e Dormiu em  ").append(hotel.getNome()).append(" ) ");
                historico.append(String.format("C-H: %.2f | %.2f | %.2f ", distanciaIrDormirAPartirDaProxLocalidade * velocidadeMediaKmPorMinuto, jornadaDeTrabalhoDia1, jornadaDeTrabalhoDia2));
                //System.out.println("Dormiu");

                primeiroDia = false;
                proxLocalidade.dormiu = true;
            }
            case DORME_ENTREGA -> {
                Localidade hotel = this.buscarHotelMaisProximo(proxLocalidade);
                double distanciaIrDormirAPartirDaLocalidadeAtual = localidadeAtual.calcularDistancia(hotel);
                double distanciaHotelParaProxCidade = hotel.calcularDistancia(proxLocalidade);

                reduzirJornadaTrabalho(distanciaIrDormirAPartirDaLocalidadeAtual * velocidadeMediaKmPorMinuto);
                historico.append("Dormiu em  ").append(hotel.getNome()).append(") ").append(" realizou a entrega no outro dia");
                historico.append(String.format("C-H: %.2f ", distanciaIrDormirAPartirDaLocalidadeAtual * velocidadeMediaKmPorMinuto));

                //System.out.println("Dormiu");
                localidadeAtual.dormiu = true;
                primeiroDia = false;

                //System.out.println(" entregando");
                reduzirJornadaTrabalho(distanciaHotelParaProxCidade * velocidadeMediaKmPorMinuto);
                reduzirJornadaTrabalho(tempoDescarga);
                historico.append(" -> ").append(proxLocalidade.getNome()).append(" (").append(qtdCarga).append(") ");
                historico.append(String.format("H-C:  %.2f | %.2f | %.2f ", distanciaHotelParaProxCidade * velocidadeMediaKmPorMinuto, jornadaDeTrabalhoDia1, jornadaDeTrabalhoDia2));
                cidadesVisitadas.add(proxLocalidade);
            }
            case IMPOSSIVEL -> throw new RuntimeException("Nenhuma categoria de entrega encontrada");

        }
        
    }
    
    public boolean podeVoltarDeposito() {
        if (qtdCarga == maxCarga) return true;
        
        boolean podeRealizarMaisAlgumaEntrega = false;
        for (Localidade localidade : localidades) {
            if (!localidade.recebeuEntrega()) {
                podeRealizarMaisAlgumaEntrega = podeVisitarCidade(localidade).tipoViagem != TipoViagem.IMPOSSIVEL;
                if (podeRealizarMaisAlgumaEntrega) break;
            }
            
        }
        
        return !podeRealizarMaisAlgumaEntrega;
    }
    
    public Localidade getUltimaLocalidade() {
        return cidadesVisitadas.get(cidadesVisitadas.size() - 1);
    }

    public Viagem podeVisitarCidade(Localidade proxLocalidade) {
        Localidade deposito = this.cidadesVisitadas.get(0);
        Localidade localidadeAtual = this.cidadesVisitadas.get(this.cidadesVisitadas.size() - 1);
        Localidade hotel = this.buscarHotelMaisProximo(proxLocalidade);

        if (proxLocalidade.recebeuEntrega()) return new Viagem(proxLocalidade, TipoViagem.IMPOSSIVEL);

        double distanciaProxCidade = localidadeAtual.calcularDistancia(proxLocalidade);
        double distanciaVoltar = proxLocalidade.calcularDistancia(deposito);
        double distanciaIrDormirAPartirDaProxLocalidade = proxLocalidade.calcularDistancia(hotel);
        double distanciaIrDormirAPartirDaLocalidadeAtual = localidadeAtual.calcularDistancia(hotel);
        double distanciaVoltarHotel = hotel.calcularDistancia(deposito);
        double distanciaHotelParaProxCidade = hotel.calcularDistancia(proxLocalidade);

        boolean restricaoDeCarga = (qtdCarga + proxLocalidade.getQtdItensReceber()) <= maxCarga;


        double tempoDistanciaProxCidadeEVoltarComTempoDescarga = (((distanciaProxCidade + distanciaVoltar) * velocidadeMediaKmPorMinuto) + tempoDescarga);

        boolean consegueRealizarEntregaNoMesmoDia = primeiroDia && jornadaDeTrabalhoDia1 > tempoDistanciaProxCidadeEVoltarComTempoDescarga && restricaoDeCarga;

        boolean consegueRealizarEntregaNoProxDia = !primeiroDia && jornadaDeTrabalhoDia2 > tempoDistanciaProxCidadeEVoltarComTempoDescarga && restricaoDeCarga;

        boolean consegueEntregarEIrAoHotelNoMesmoDiaEVoltarNoProximo = primeiroDia && jornadaDeTrabalhoDia1 > ((distanciaProxCidade + distanciaIrDormirAPartirDaProxLocalidade) * velocidadeMediaKmPorMinuto) + tempoDescarga && jornadaDeTrabalhoDia2 > (distanciaVoltarHotel * velocidadeMediaKmPorMinuto)  && restricaoDeCarga;

        boolean consegueIrParaHotelERealizarEntregaNoOutroDia = primeiroDia && jornadaDeTrabalhoDia1 > (distanciaIrDormirAPartirDaLocalidadeAtual * velocidadeMediaKmPorMinuto) && jornadaDeTrabalhoDia2 > ((distanciaHotelParaProxCidade + distanciaVoltar) * velocidadeMediaKmPorMinuto) + tempoDescarga  && restricaoDeCarga;

        if (consegueRealizarEntregaNoMesmoDia || consegueRealizarEntregaNoProxDia)
            return new Viagem(proxLocalidade, TipoViagem.VIAGEM_ENTRE_CIDADES);
        if (consegueEntregarEIrAoHotelNoMesmoDiaEVoltarNoProximo)
            return new Viagem(proxLocalidade, TipoViagem.ENTREGA_DORME);
        if (consegueIrParaHotelERealizarEntregaNoOutroDia) return new Viagem(proxLocalidade, TipoViagem.DORME_ENTREGA);

        return new Viagem(proxLocalidade, TipoViagem.IMPOSSIVEL);
    }
    
    private void reduzirJornadaTrabalho(double minutosTrabalhados) {
        if (primeiroDia) {
            jornadaDeTrabalhoDia1 -= minutosTrabalhados;
            if(jornadaDeTrabalhoDia1 < 0) {
                throw new  RuntimeException("Jornada de trabalho 1 negativa");
            }
        } else {
            jornadaDeTrabalhoDia2 -= minutosTrabalhados;
            if(jornadaDeTrabalhoDia2 < 0) {
                throw new  RuntimeException("Jornada de trabalho 2 negativa");
            }
        }
    }

    private Localidade buscarHotelMaisProximo(Localidade localidade) {
        Double menorDistancia = this.hoteis.get(0).calcularDistancia(localidade);
        Localidade hotel = this.hoteis.get(0);

        for(Localidade h: this.hoteis) {
            Double distancia = h.calcularDistancia(localidade);
            if( distancia< menorDistancia) {
                menorDistancia = distancia;
                hotel = h;
            }
        }

        return  hotel;
    }

    public int getQtdCarga() {
        return qtdCarga;
    }

    public StringBuilder getHistorico() {
        return historico;
    }
}
