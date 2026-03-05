package es.uma.informatica.misia.ae.simpleea;

import java.util.Random;

public class QUBO implements Problem {
    private int n;
    private double[][] Q;

    public QUBO(int n, double[][] Q) {
        this.n = n;
        this.Q = Q;
    }

    @Override
    public double evaluate(Individual individual) {
        BinaryString binaryString = (BinaryString) individual;
        byte[] x = binaryString.getChromosome();  // corregido a byte[]
        double result = 0.0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result += x[i] * Q[i][j] * x[j];
            }
        }
        return result;
    }

    @Override
    public BinaryString generateRandomIndividual(Random rnd) {
        return new BinaryString(n, rnd);
    }
}
