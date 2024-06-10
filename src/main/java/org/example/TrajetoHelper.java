package org.example;

import java.util.ArrayList;

public class TrajetoHelper {
    
    // Holds our cities
    private static ArrayList<Cidade> cidades = new ArrayList<Cidade>();
    
    public static void addCidade(Cidade city) {
        cidades.add(city);
    }
    
    public static Cidade getCidade(int index){
        return (Cidade) cidades.get(index);
    }
    
    public static int numeroCidades(){
        return cidades.size();
    }
    
}