package com.mycompany.pulido_lab2;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSV_separator {
    public static void main(String[] args) throws CsvException {
        String filePath = "C:\\Users\\Francis\\Documents\\NetBeansProjects\\Pulido_Lab2\\src\\main\\java\\household_energy_consumption.csv"; // Update your CSV path
        List<double[]> mainData = new ArrayList<>();
        List<Double> lastColumnData = new ArrayList<>();
        String[] headerArray = null; // To store headers

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> allRows = reader.readAll(); // Reads all rows into a List
            
            boolean isHeader = true;
            for (String[] row : allRows) {
                if (isHeader) { 
                    headerArray = row; // Store the header row
                    isHeader = false;
                    continue;
                }

                try {
                    int columnCount = row.length; // Get total columns dynamically
                    if (columnCount < 2) continue; // Skip if too few columns
                    
                    // Separate last column
                    double lastValue = Double.parseDouble(row[columnCount - 1].trim());
                    lastColumnData.add(lastValue);

                    // Store other columns (excluding last one)
                    double[] dataRow = new double[columnCount - 1]; 
                    for (int i = 0; i < columnCount - 1; i++) { 
                        dataRow[i] = Double.parseDouble(row[i].trim());
                    }
                    mainData.add(dataRow);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("Skipping invalid row: " + String.join(", ", row));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert Lists to arrays
        double[][] mainArray = mainData.toArray(new double[0][0]);
        double[] lastColumnArray = lastColumnData.stream().mapToDouble(Double::doubleValue).toArray();

        // Print Headers
        System.out.println("Headers:");
        if (headerArray != null) {
            System.out.println(java.util.Arrays.toString(headerArray));
        }

        // Print Main Data
        System.out.println("\nMain Data (All columns except last):");
        for (double[] row : mainArray) {
            System.out.println(java.util.Arrays.toString(row));
        }

        // Print Last Column Data
        System.out.println("\nLast Column Data:");
        System.out.println(java.util.Arrays.toString(lastColumnArray));
    }
}
