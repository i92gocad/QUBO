package es.uma.informatica.misia.ae.simpleea;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Utils {
    public static double[][] readQUBOFromCSV(String filePath) {
        double[][] Q = null;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (Q == null) Q = new double[values.length][values.length];
                for (int col = 0; col < values.length; col++) {
                    Q[row][col] = Double.parseDouble(values[col].trim());
                }
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Q;
    }
}
