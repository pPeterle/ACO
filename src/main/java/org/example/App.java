package org.example;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        
        AntColonyOptimization aco = new AntColonyOptimization(
                1.0,
            1,
            5,
            0.5,
            500,
            0.8,
            0.01,
            1000,
            30
    );
        aco.gerarMatrixAleatoria(20);
        aco.comecarOtimizacao();
        
        System.out.println(aco.s);
        
        System.out.println( "Finalizado " );
    }
}
