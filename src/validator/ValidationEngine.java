package validator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class ValidationEngine 
{

    public static void main(String[] args) 
    {

        String inputCsv = "resources/raw.csv";
        String cleanCsv = "output/clean.csv";
        String rejectCsv = "output/rejects.csv";
        String auditTxt = "output/audit.txt";
        String statsCsv = "output/stats.csv";


        int totalRows = 0;
        int validRows = 0;
        int invalidRows = 0;
        int rowNumber = 0;

        // Regex patterns (as per schema.json)
        Pattern namePattern = Pattern.compile("^[A-Za-z ]+$");
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

        try (
            BufferedReader br = new BufferedReader(new FileReader(inputCsv));
            BufferedWriter cleanWriter = new BufferedWriter(new FileWriter(cleanCsv));
            BufferedWriter rejectWriter = new BufferedWriter(new FileWriter(rejectCsv));
            BufferedWriter auditWriter = new BufferedWriter(new FileWriter(auditTxt))
        ) 
        
        {

            // Read header
            String header = br.readLine();
            System.out.println("Header: " + header);

            cleanWriter.write(header);
            cleanWriter.newLine();

            rejectWriter.write(header);
            rejectWriter.newLine();

            String line;

            while ((line = br.readLine()) != null) 
            {
                rowNumber++;
                totalRows++;

                String[] values = line.split(",");

                // Normalize values
                String name  = values.length > 0 ? values[0].replace("\"", "").trim() : "";
                String email = values.length > 1 ? values[1].replace("\"", "").trim() : "";
                String ageStr = values.length > 2 ? values[2].replace("\"", "").trim() : "";

                boolean valid = true;
                StringBuilder error = new StringBuilder();

                // ===== Required checks =====
                if (name.isEmpty()) 
                {
                    valid = false;
                    error.append("Name is required. ");
                }

                if (email.isEmpty())
                {
                    valid = false;
                    error.append("Email is required. ");
                }

                if (ageStr.isEmpty()) 
                {
                    valid = false;
                    error.append("Age is required. ");
                }

                // ===== Regex checks =====
                if (!name.isEmpty() && !namePattern.matcher(name).matches()) 
                {
                    valid = false;
                    error.append("Invalid name format. ");
                }

                if (!email.isEmpty() && !emailPattern.matcher(email).matches()) 
                {
                    valid = false;
                    error.append("Invalid email format. ");
                }

                // ===== Type check =====
                if (!ageStr.isEmpty())
                {
                    try {
                        Integer.parseInt(ageStr);
                    } catch (NumberFormatException e) 
                    {
                        valid = false;
                        error.append("Age is not an integer. ");
                    }
                }

                // ===== Final decision =====
                if (valid) 
                {
                    validRows++;
                    System.out.println("Row " + rowNumber + ": VALID");
                    cleanWriter.write(line);
                    cleanWriter.newLine();
                } 
                else 
                {
                    invalidRows++;
                    System.out.println("Row " + rowNumber + ": INVALID → " + error.toString());
                    rejectWriter.write(line);
                    rejectWriter.newLine();
                    auditWriter.write("Row " + rowNumber + ": " + error.toString());
                    auditWriter.newLine();
                }
            }

        }
        catch (IOException e) 
        {
            System.out.println("File processing error.");
            e.printStackTrace();
            return;
        }

        // ===== Write statistics =====
        try (BufferedWriter statsWriter = new BufferedWriter(new FileWriter(statsCsv))) 
        {

            statsWriter.write("Total Rows,Valid Rows,Invalid Rows,Rejection Rate (%)");
            statsWriter.newLine();

            double rejectionRate = (invalidRows * 100.0) / totalRows;
            statsWriter.write(totalRows + "," + validRows + "," + invalidRows + "," + rejectionRate);
            statsWriter.newLine();

        } 
        catch (IOException e) 
        {
            System.out.println("Error writing stats file.");
            e.printStackTrace();
        }
    }
}
