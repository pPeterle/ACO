package org.example.modelos;

public class Resultado {
    private final double custoTotal;
    private final long tempo;
    private final double index;

    public double getCustoTotal() {
        return custoTotal;
    }

    public long getTempo() {
        return tempo;
    }

    public double getIndex() {
        return index;
    }

    public Resultado(double custoTotal, long tempo, double index) {
        this.custoTotal = custoTotal;
        this.tempo = tempo;
        this.index = index;
    }
}
