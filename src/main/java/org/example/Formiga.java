package org.example;

public class Formiga
{
    protected int tamanhoCaminho;
    protected int[] caminho;
    protected boolean[] visitou;
    
    public Formiga(int tourSize)
    {
        this.tamanhoCaminho = tourSize;
        this.caminho = new int[tourSize];
        this.visitou = new boolean[tourSize];
    }
    
    protected void visitCity(int currentIndex, int city)
    {
        caminho[currentIndex + 1] = city; //add to trail
        visitou[city] = true;           //update flag
    }
    
    protected boolean visited(int i)
    {
        return visitou[i];
    }
    
    protected double trailLength(double[][] graph)
    {
        double length = graph[caminho[tamanhoCaminho - 1]][caminho[0]];
        for (int i = 0; i < tamanhoCaminho - 1; i++)
            length += graph[caminho[i]][caminho[i + 1]];
        return length;
    }
    
    protected void clear()
    {
        for (int i = 0; i < tamanhoCaminho; i++)
            visitou[i] = false;
    }
}