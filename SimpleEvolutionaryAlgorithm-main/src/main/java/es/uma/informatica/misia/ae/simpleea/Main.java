package es.uma.informatica.misia.ae.simpleea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        if (args.length < 2) {
            System.err.println("Invalid number of arguments");
            System.err.println("Arguments: <problem size> <CSV path>");
            return;
        }

        int problemSize = Integer.parseInt(args[0]);
        String csvPath = args[1];  // ahora definido correctamente

        // Leer matriz QUBO desde CSV
        double[][] Q = Utils.readQUBOFromCSV(csvPath);
		problemSize = Q.length;
        // Crear el problema QUBO
        Problem problem = new QUBO(problemSize, Q);
       
        double[] bitflipProbs = {0.1, 0.3, 0.6, 0.9};
        int[] poblaciones = {100, 500};
        double [] crosProbs = {0.3, 0.5,0.7, 0.9};
        //double [] crosProbs = {0.3};
        for (int pop : poblaciones) {
            for (double prob : bitflipProbs) {
                for (double crosProb : crosProbs) {
                    System.out.println(" Population " + pop + " bit-flip probability " + prob+  " and crossover probability " + crosProb);
                    List<Double> resultados = new ArrayList<>();
                    for (int i = 1; i <=1; i++) { 
                        long randomSeed = System.currentTimeMillis();
                        Map<String, Double> parameters = new HashMap<>();
                        parameters.put(EvolutionaryAlgorithm.POPULATION_SIZE_PARAM, (double) pop);
                        parameters.put(EvolutionaryAlgorithm.MAX_FUNCTION_EVALUATIONS_PARAM, (double) 500); // modificar en el caso de que se quiera poner un criterio de parada
                        parameters.put(BitFlipMutation.BIT_FLIP_PROBABILITY_PARAM, prob);
                        parameters.put(EvolutionaryAlgorithm.RANDOM_SEED_PARAM, (double) randomSeed);

                        // Ejecutar el algoritmo evolutivo
                        EvolutionaryAlgorithm ea = new EvolutionaryAlgorithm(parameters, problem, crosProb);
                        Individual bestSolution = ea.run();
                        
                        //System.out.println("Run "+i +" Best solution fitness: " + bestSolution.getFitness());
                        resultados.add(bestSolution.getFitness());
                        //System.out.println("Best solution: " + bestSolution);
                    }
                    // Calcular media y desviación típica
                    double media = calcularMedia(resultados);
                    double desviacion = calcularDesviacionTipica(resultados, media);
                    
                    System.out.println(media + "+-" + desviacion);
                   
                }
            }
        }
    }

    public static double calcularMedia(List<Double> valores) {
        double suma = 0.0;
        for (double v : valores) {
            suma += v;
        }
        return suma / valores.size();
    }

    public static double calcularDesviacionTipica(List<Double> valores, double media) {
        double suma = 0.0;
        for (double v : valores) {
            suma += Math.pow(v - media, 2);
        }
        return Math.sqrt(suma / valores.size());
    }
}
