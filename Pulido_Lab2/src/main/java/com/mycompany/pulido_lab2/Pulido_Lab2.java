/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.pulido_lab2;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.math3.linear.*;

/**
 *
 * @author Francis
 */
public class Pulido_Lab2 {

    public static void main(String[] args) throws CsvException {
        
        //locate the csv file 
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
         // Example matrix X (features matrix) and y (target values)
        double[][] X_data = mainData.toArray(new double[0][0]);
        double[] y_data = lastColumnData.stream().mapToDouble(Double::doubleValue).toArray(); // Target house prices

        // Create RealMatrix objects for X and y
        RealMatrix X = MatrixUtils.createRealMatrix(X_data);
        RealVector y = MatrixUtils.createRealVector(y_data);

        // Calculate X^T (transpose of X)
        RealMatrix Xt = X.transpose();

        // Calculate (X^T * X) and its inverse
        RealMatrix Xt_MultipliedByX = Xt.multiply(X);
        RealMatrix Inverse_Xt_MultipliedByX = new LUDecomposition(Xt_MultipliedByX).getSolver().getInverse();

        // Convert y to a RealMatrix column vector (n x 1 matrix)
        RealMatrix yMatrix = MatrixUtils.createColumnRealMatrix(y.toArray());

        // Calculate beta (coefficients) using the formula: beta = (X^T * X)^(-1) * X^T * y
        RealMatrix beta = Inverse_Xt_MultipliedByX.multiply(Xt).multiply(yMatrix);

        // Extract the coefficients as a RealVector (1D array)
        RealVector betaVector = beta.getColumnVector(0);

        // Display the coefficients (beta values)
        System.out.println("Calculated Beta Coefficients:");
        System.out.println("Beta0 (Daily Electricity Usage (kWh)): " + betaVector.getEntry(0));
        System.out.println("Beta1 (Number of Household Members): " + betaVector.getEntry(1));
        System.out.println("Beta2 (Appliance Used): " + betaVector.getEntry(2));
        System.out.println("Beta3 (Average Monthly Income (USD)): " + betaVector.getEntry(3));
        System.out.println("Beta4 (Home Size(sqm)): " + betaVector.getEntry(4));

        // Allow user to input features for prediction
        Scanner scanner = new Scanner(System.in);
         System.out.print("\n Enter the Daily Electricity Usage (kWh): ");
        double col1 = scanner.nextDouble();
        
        System.out.print("\nEnter Number of Household Members (sq ft): ");
        double size = scanner.nextDouble();

        System.out.print("Enter the number Appliances Used: ");
        int bedrooms = scanner.nextInt();

        System.out.print("Enter your Average Monthly Income (USD): ");
        int location = scanner.nextInt();
        
         System.out.print("\nEnter the Home Size (sqm): ");
        double col5 = scanner.nextDouble();

        // Create a new feature vector (including the intercept term)
        double[] newHouse = {col1, size, bedrooms, location, col5}; // Intercept term + size, bedrooms, location

        // Convert the new house row to a RealVector
        RealVector row = MatrixUtils.createRealVector(newHouse);

        // Calculate the predicted price using the dot product of the feature vector and the beta coefficients
        double predictedPrice = row.dotProduct(betaVector);

        // Output the predicted price
        System.out.println("\nPredicted Monthly Energy Bill: $" + predictedPrice);
    }
    
    }

