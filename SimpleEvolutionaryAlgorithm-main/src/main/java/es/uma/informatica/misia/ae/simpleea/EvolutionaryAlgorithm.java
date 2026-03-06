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

	public int NoImprovementCounter;
	
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
		int maxNoImprovement = 200;
		NoImprovementCounter = 0;

		ArrayList<Double> fitnessHistory = new ArrayList<>();

		// Evalúa población inicial y guarda el mejor
		evaluatePopulation(population);
		fitnessHistory.add(bestSolution.getFitness());

		while (functionEvaluations < maxFunctionEvaluations) {

			functionEvaluations ++;
			List<Individual> offspring = new ArrayList<>();

			// Generar offspring del mismo tamaño que la población
			for (int i = 0; i < populationSize; i++) {

				// Selección de padres
				Individual parent1 = selection.selectParent(population);
				Individual parent2 = selection.selectParent(population);

				// Crossover
				Individual child;
				if (rnd.nextDouble() < crossProb) {
					child = recombination.apply(parent1, parent2);
				} else {
					child = new BinaryString((BinaryString) parent1);	
			}

				// Mutación
				child = mutation.apply(child);

				// Evaluar hijo
				evaluateIndividual(child);

				offspring.add(child);
			}

			// Reemplazo
			population = replacement.replacement(population, offspring);

			// Guardar historial del mejor
			fitnessHistory.add(bestSolution.getFitness());

			// Criterio de parada por no mejora
			if (NoImprovementCounter >= maxNoImprovement) {
				System.out.println("Criterio de parada alcanzado " + functionEvaluations +
						": no mejora en " + maxNoImprovement + " evaluaciones");
				System.out.println("Historial del mejor fitness: " + fitnessHistory);
				return bestSolution;
			}
		}

		System.out.println("Historial del mejor fitness: " + fitnessHistory);
		return bestSolution;
	}

	private void checkIfBest(Individual individual) {
		if (bestSolution == null || individual.getFitness() < bestSolution.getFitness()) {
			bestSolution = individual;
			NoImprovementCounter = 0; // Reinicia contador solo si hay mejora
			System.out.println("New best solution with fitness: " + bestSolution.getFitness() + 
							" Function evaluations: " + functionEvaluations);
		} 
		// No incrementamos aquí por cada hijo, lo hacemos al final de cada generación
	}

	private void evaluateIndividual(Individual individual) {
		double fitness = problem.evaluate(individual);
		individual.setFitness(fitness);
		checkIfBest(individual);
	}

	// private void checkIfBest(Individual individual) {
	// 	if (bestSolution == null || individual.getFitness() < bestSolution.getFitness()) {
	// 		bestSolution = individual;
	// 		NoImprovementCounter = 0; // Reinicia el contador de no mejora
	// 		System.out.println("New best solution with fitness: " + bestSolution.getFitness() + " Function evaluations: " + functionEvaluations);
	// 	}
	// 	else {
	// 		NoImprovementCounter++;
	// 	}
	// }

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
