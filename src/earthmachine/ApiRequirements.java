/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Christopher Brett
 */
public class ApiRequirements {

    private final ArrayList<ParameterRequirement> parameterRequirements = new ArrayList<>();

    protected ApiRequirements(File requirementsFile) { //parse file (text), extract requirements 
        try ( BufferedReader br = new BufferedReader(new FileReader(requirementsFile))) {
            for (String line; (line = br.readLine()) != null;) {
                String[] requirements = line.split("#");
                parameterRequirements.add(new ParameterRequirement(requirements[0], requirements[1], requirements[2], (requirements[3].equals("not set") ? Long.MIN_VALUE : Long.parseLong(requirements[3])), (requirements[4].equals("not set") ? Long.MIN_VALUE : Long.parseLong(requirements[4])), requirements[5].equals("T"), requirements[6]));
            }
        }catch(IOException e){
            e.printStackTrace(System.out);
        }
    }

    protected ArrayList<ParameterRequirement> getParameterRequirements() {
        return this.parameterRequirements;
    }
}
