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
    
    List<Localidade> localidades;
    
    Caminhao(List<Localidade> localidades) {
        this.localidades = localidades;
        this.cidadesVisitadas = new ArrayList<>();
        
        // inicia o caminhão do depósito
        this.cidadesVisitadas.add(localidades.get(0));
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
        
        Localidade localidadeAtual = cidadesVisitadas.get(cidadesVisitadas.size() - 1);
        Localidade deposito = this.cidadesVisitadas.get(0);
        
        double distanciaProxCidade = localidadeAtual.calcularDistancia(proxLocalidade);
        double distanciaVoltar = proxLocalidade.calcularDistancia(deposito);
        
        boolean consegueRealizarEntregaNoMesmoDia = getJornadaDeTrabalhoAtual() >= (((distanciaProxCidade + distanciaVoltar) * velocidadeMediaKmPorMinuto) + tempoDescarga);
        
        if (consegueRealizarEntregaNoMesmoDia) {
            // minutos do descolamento
            reduzirJornadaTrabalho(distanciaProxCidade * velocidadeMediaKmPorMinuto);
            // minutos para descarga
            reduzirJornadaTrabalho(tempoDescarga);
            
            cidadesVisitadas.add(proxLocalidade);
            
            return;
        }
        
        boolean consegueViajarParaProxCidadeNoMesmoDia = jornadaDeTrabalhoDia1 >= (distanciaProxCidade * velocidadeMediaKmPorMinuto);
        
        if (consegueViajarParaProxCidadeNoMesmoDia) {
            // Viaja para localidade de destino -> dorme -> faz a entrega
            
            
            // minutos de deslocamento
            reduzirJornadaTrabalho(distanciaProxCidade * velocidadeMediaKmPorMinuto);
            
            // Dormiu na proxima cidade
            primeiroDia = false;
            
            if(proxLocalidade.getQtdItensReceber() > 0) {
                reduzirJornadaTrabalho(tempoDescarga);
            }
            
            
            cidadesVisitadas.add(proxLocalidade);
            
        } else {
            // Dorme na localidade atual -> viaja para cidade no proximo dia -> faz a entrega
            
            primeiroDia = false;
            reduzirJornadaTrabalho(distanciaProxCidade * velocidadeMediaKmPorMinuto);
            
            if(proxLocalidade.getQtdItensReceber() > 0) {
                reduzirJornadaTrabalho(tempoDescarga);
            }
            
            cidadesVisitadas.add(proxLocalidade);
        }
        
        
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
        
        if (proxLocalidade.recebeuEntrega()) return false;
        
        double distanciaProxCidade = localidadeAtual.calcularDistancia(proxLocalidade);
        double distanciaVoltar = proxLocalidade.calcularDistancia(deposito);
        
        boolean consegueRealizarEntregaNoMesmoDia = primeiroDia && jornadaDeTrabalhoDia1 > (((distanciaProxCidade + distanciaVoltar) * velocidadeMediaKmPorMinuto) + tempoDescarga) && (qtdCarga + proxLocalidade.getQtdItensReceber()) <= maxCarga;
        
        boolean consegueRealizarEntregaNoProxDia = !primeiroDia && jornadaDeTrabalhoDia2 > (((distanciaProxCidade + distanciaVoltar) * velocidadeMediaKmPorMinuto) + tempoDescarga) && (qtdCarga + proxLocalidade.getQtdItensReceber()) <= maxCarga;
        
        boolean consegueEntregarEVoltarNoProximoDia = primeiroDia && (jornadaDeTrabalhoDia1 + jornadaDeTrabalhoDia2) > (((distanciaProxCidade + distanciaVoltar) * velocidadeMediaKmPorMinuto) + tempoDescarga) && (qtdCarga + proxLocalidade.getQtdItensReceber()) <= maxCarga;
        
        return consegueRealizarEntregaNoMesmoDia || consegueRealizarEntregaNoProxDia || consegueEntregarEVoltarNoProximoDia;
    }
    
    private double getJornadaDeTrabalhoAtual() {
        if (primeiroDia) {
            return jornadaDeTrabalhoDia1;
        } else {
            return jornadaDeTrabalhoDia2;
        }
    }
    
    private void reduzirJornadaTrabalho(double minutosTrabalhados) {
        if (primeiroDia) {
            jornadaDeTrabalhoDia1 -= minutosTrabalhados;
            
        } else {
            jornadaDeTrabalhoDia2 -= minutosTrabalhados;
        }
    }
    
}
