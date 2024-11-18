package org.example;

import java.util.ArrayList;
import java.util.List;

// JORNADA DE TRABALHO QUANDO DEVE IR PARA O PRÓXIMO DIA


public class Caminhao {
    
    final int velocidadeMediaKmPorMinuto = 1;
    
    final int maxCarga = 120;

    final int tempoDescarga = 15;
    
    double jornadaDeTrabalhoDia1 = (8 * 60) + 30;
    double jornadaDeTrabalhoDia2 = (8 * 60) + 30;
    
    boolean primeiroDia = true;
    
    int qtdCarga = 0;
    
    
    public ArrayList<Localidade> cidadesVisitadas;
    
    StringBuilder historico;
    
    List<Localidade> localidades;
    List<Localidade> hoteis;


    
    Caminhao(List<Localidade> localidades, List<Localidade> hoteis) {
        this.localidades = localidades;
        this.cidadesVisitadas = new ArrayList<>();
        
        // inicia o caminhão do depósito
        this.cidadesVisitadas.add(localidades.get(0));
        this.historico = new StringBuilder();
        this.historico.append(localidades.get(0).getNome());
        this.hoteis = hoteis;
    }
    
    
    public void visitarLocalidade(Localidade proxLocalidade) {
        if (!cidadesVisitadas.isEmpty()) {
            Localidade ultimaLocalidade = cidadesVisitadas.get(cidadesVisitadas.size() - 1);
            
            if (proxLocalidade.getNome().equals(ultimaLocalidade.getNome())) return;
        }
        
        int novaCarga = proxLocalidade.getQtdItensReceber();
        
        qtdCarga += novaCarga;
        
        if (qtdCarga > maxCarga) {
            throw new RuntimeException("Carga ultrapassou a quantidade máxima do caminhão");
        }

        Localidade deposito = this.cidadesVisitadas.get(0);
        Localidade localidadeAtual = this.cidadesVisitadas.get(this.cidadesVisitadas.size() - 1);
        Localidade hotel = this.buscarHotelMaisProximo(proxLocalidade);

        if(deposito.getNome().equals(proxLocalidade.getNome())) {
            double distancia = localidadeAtual.calcularDistancia(deposito);
            //System.out.println("Voltando ao deposito " + distancia);
            reduzirJornadaTrabalho(distancia * velocidadeMediaKmPorMinuto);
            cidadesVisitadas.add(proxLocalidade);
            historico.append(" -> ").append(proxLocalidade.getNome()).append(" (").append(qtdCarga).append(") ");
            historico.append(String.format("C-D:  %.2f | %.2f | %.2f ", distancia * velocidadeMediaKmPorMinuto, jornadaDeTrabalhoDia1, jornadaDeTrabalhoDia2));
            return;
        }


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
        
        if (consegueRealizarEntregaNoMesmoDia || consegueRealizarEntregaNoProxDia) {
            historico.append(" -> ").append(proxLocalidade.getNome()).append(" (").append(qtdCarga).append(") ");
            //System.out.println("Entregando no mesmo dia");
            // minutos do descolamento
            reduzirJornadaTrabalho(distanciaProxCidade * velocidadeMediaKmPorMinuto);
            // minutos para descarga
            reduzirJornadaTrabalho(tempoDescarga);

            historico.append(String.format("C-C: %.2f | %.2f | %.2f ", distanciaProxCidade * velocidadeMediaKmPorMinuto, jornadaDeTrabalhoDia1, jornadaDeTrabalhoDia2));
            
            cidadesVisitadas.add(proxLocalidade);
            return;
        }

        if(consegueEntregarEIrAoHotelNoMesmoDiaEVoltarNoProximo) {
            //System.out.println("Entregando no mesmo dia e depos viajando pro hotel");
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
            return;
        }

        if(consegueIrParaHotelERealizarEntregaNoOutroDia) {
            //System.out.println("Indo ao hotel e depois entregando");
            reduzirJornadaTrabalho(distanciaIrDormirAPartirDaLocalidadeAtual * velocidadeMediaKmPorMinuto);
            historico.append("Dormiu em  ").append(hotel.getNome()).append(") ").append(" realizou a entrega no outro dia");
            historico.append(String.format("C-H: %.2f ", distanciaIrDormirAPartirDaLocalidadeAtual * velocidadeMediaKmPorMinuto));

            //System.out.println("Dormiu");
            primeiroDia = false;

            //System.out.println(" entregando");
            reduzirJornadaTrabalho(distanciaHotelParaProxCidade * velocidadeMediaKmPorMinuto);
            reduzirJornadaTrabalho(tempoDescarga);
            historico.append(" -> ").append(proxLocalidade.getNome()).append(" (").append(qtdCarga).append(") ");
            historico.append(String.format("H-C:  %.2f | %.2f | %.2f ", distanciaHotelParaProxCidade * velocidadeMediaKmPorMinuto, jornadaDeTrabalhoDia1, jornadaDeTrabalhoDia2));
            cidadesVisitadas.add(proxLocalidade);
            return;
        }

        throw new RuntimeException("Nenhuma categoria de entrega encontrada");
        
        
    }
    
    public boolean podeVoltarDeposito() {
        if (qtdCarga == maxCarga) return true;
        
        boolean podeRealizarMaisAlgumaEntrega = false;
        for (Localidade localidade : localidades) {
            if (!localidade.recebeuEntrega()) {
                podeRealizarMaisAlgumaEntrega = podeVisitarCidade(localidade);
                if (podeRealizarMaisAlgumaEntrega) break;
            }
            
        }
        
        return !podeRealizarMaisAlgumaEntrega;
    }
    
    public Localidade getUltimaLocalidade() {
        return cidadesVisitadas.get(cidadesVisitadas.size() - 1);
    }
    
    public boolean podeVisitarCidade(Localidade proxLocalidade) {
        Localidade deposito = this.cidadesVisitadas.get(0);
        Localidade localidadeAtual = this.cidadesVisitadas.get(this.cidadesVisitadas.size() - 1);
        Localidade hotel = this.buscarHotelMaisProximo(proxLocalidade);
        
        if (proxLocalidade.recebeuEntrega()) return false;

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


        return consegueRealizarEntregaNoMesmoDia || consegueRealizarEntregaNoProxDia || consegueIrParaHotelERealizarEntregaNoOutroDia || consegueEntregarEIrAoHotelNoMesmoDiaEVoltarNoProximo;
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
    
}
