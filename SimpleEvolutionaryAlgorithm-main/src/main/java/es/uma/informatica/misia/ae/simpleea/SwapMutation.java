package es.uma.informatica.misia.ae.simpleea;

import java.util.Random;

public class SwapMutation implements Mutation {

    private double swapProb;
    private Random rnd;
    public static final String SWAP_PROBABILITY_PARAM = "swapProbability";

    public SwapMutation(Random rnd, double swapProb) {
        this.rnd = rnd;
        this.swapProb = swapProb;
    }

    @Override
    public Individual apply(Individual individual) {
        BinaryString original = (BinaryString) individual;
        BinaryString mutated = new BinaryString(original);

        // Aplicamos la mutación con probabilidad swapProb
        if (rnd.nextDouble() < swapProb) {
            int len = mutated.getChromosome().length;

            // Elegimos dos posiciones distintas al azar
            int i = rnd.nextInt(len);
            int j = rnd.nextInt(len);
            while (j == i) {
                j = rnd.nextInt(len);
            }

            // Intercambiamos los bits
            byte temp = mutated.getChromosome()[i];
            mutated.getChromosome()[i] = mutated.getChromosome()[j];
            mutated.getChromosome()[j] = temp;
        }

        return mutated;
    }

    public double getSwapProb() {
        return swapProb;
    }

    public void setSwapProb(double swapProb) {
        this.swapProb = swapProb;
    }
}