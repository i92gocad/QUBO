package es.uma.informatica.misia.ae.simpleea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EvolutionaryAlgorithm {
	public static final String MAX_FUNCTION_EVALUATIONS_PARAM = "maxFunctionEvaluations";
	public static final String RANDOM_SEED_PARAM = "randomSeed";
	public static final String POPULATION_SIZE_PARAM = "populationSize";
	
	private Problem problem;
	private int functionEvaluations;
	private int maxFunctionEvaluations;
	private List<Individual> population;
	private int populationSize;
	private Random rnd;
	private double crossProb;
	
	private Individual bestSolution;
	
	private Selection selection;
	private Replacement replacement;
	private Mutation mutation;
	private Crossover recombination;
	
	public EvolutionaryAlgorithm(Map<String, Double> parameters, Problem problem, Double crossProb) {
		configureAlgorithm(parameters, problem);
		this.crossProb = crossProb;
	}
	
	private void configureAlgorithm(Map<String, Double> parameters, Problem problem) {
		populationSize = parameters.get(POPULATION_SIZE_PARAM).intValue();
		maxFunctionEvaluations = parameters.get(MAX_FUNCTION_EVALUATIONS_PARAM).intValue();
		double bitFlipProb = parameters.get(BitFlipMutation.BIT_FLIP_PROBABILITY_PARAM);
		long randomSeed = parameters.get(RANDOM_SEED_PARAM).longValue();
		
		this.problem = problem; 
		
		rnd = new Random(randomSeed);
		
		selection = new BinaryTournament(rnd);
		replacement = new ElitistReplacement();
		mutation = new BitFlipMutation(rnd, bitFlipProb);
		recombination = new SinglePointCrossover(rnd);
	}
	
	public Individual run() {
		population = generateInitialPopulation();
		functionEvaluations = 0;
		double bestFitness = Double.POSITIVE_INFINITY;
		int noImprovementCounter = 0;
		int maxNoImprovement = 250; // Número máximo de evaluaciones sin mejora antes

		ArrayList <Double> fitnessHistory = new ArrayList<>();

		evaluatePopulation(population);
		functionEvaluations = 0;
		while (functionEvaluations < maxFunctionEvaluations) {
			Individual parent1 = selection.selectParent(population);
			Individual parent2 = selection.selectParent(population);
			Individual child = recombination.apply(parent1, parent2);
			if (rnd.nextDouble() > crossProb) {
				child = parent1;
			}

			child = mutation.apply(child);
			evaluateIndividual(child);
			fitnessHistory.add(child.getFitness());
			population = replacement.replacement(population, Arrays.asList(child));

			// double currentFitness = child.getFitness();
			// if (currentFitness < bestFitness) {
			// 	bestFitness = currentFitness;
			// 	noImprovementCounter = 0;
			// 	bestSolution = child;
			// } else {
			// 	noImprovementCounter++;
			// }		
			// if (noImprovementCounter >= maxNoImprovement) {
			// 	//System.out.println("Criterio de parada alcanzado "+functionEvaluations +": no mejora en " + maxNoImprovement + " evaluaciones");
			// 	System.out.println("Array de fitness: " + fitnessHistory);
			// 	return bestSolution;
			// }
		}
		
			//TODO: Descomentar para hacer un reemplazo generacional
			// List<Individual> offspring = new ArrayList<>();
			// while (offspring.size() < population.size()*0.8) {
			// 	Individual parent1 = selection.selectParent(population);
			// 	Individual parent2 = selection.selectParent(population);
			// 	Individual child = recombination.apply(parent1, parent2);
			// 	child = mutation.apply(child);
			// 	evaluateIndividual(child);
			// 	offspring.add(child);
			// }
			//population = replacement.replacement(population, offspring);
			
			//System.out.println("Current best fitness: " + child.getFitness() + " Function evaluations: " + functionEvaluations);


			//Descomentar para Criterio de parada de cuando no hay mejora

		System.out.println("Array de fitness: " + fitnessHistory);
		return bestSolution;
    }

	private void evaluateIndividual(Individual individual) {
		double fitness = problem.evaluate(individual);
		individual.setFitness(fitness);
		functionEvaluations++;
		checkIfBest(individual);
	}

	private void checkIfBest(Individual individual) {
		if (bestSolution == null || individual.getFitness() < bestSolution.getFitness()) {
			bestSolution = individual;
			//System.out.println("New best solution with fitness: " + bestSolution.getFitness() + " Function evaluations: " + functionEvaluations);
		}
	}

	private void evaluatePopulation(List<Individual> population) {
		for (Individual individual: population) {
			evaluateIndividual(individual);
		}
	}

	private List<Individual> generateInitialPopulation() {
		List<Individual> population = new ArrayList<>();
		for (int i=0; i < populationSize; i++) {
			population.add(problem.generateRandomIndividual(rnd));
		}
		return population;
	}
	
}
