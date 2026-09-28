/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Function;
/**
 *
 * @author Lisa Petrov
 */
//Responsible for collecting player input of various types
public class InputHandler {
    private final Scanner scanner;
    private static final String QUIT_KEYWORD = "quit";
    
    public InputHandler(){
        this.scanner = new Scanner(System.in);
    }
    
    //read-line scanner method, throws quitException if user types 'quit'
    public String collectUserInput() throws QuitException {
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase(QUIT_KEYWORD)){
            throw new QuitException();
        }
        return input;
    }
    
    //Collects user input based on given valid options
    //Prompts until correct input is given
    public String collectValidInput(String prompt, ArrayList validOptions) {
        while (true) {
            System.out.println(prompt + "\n: ");
            String input = collectUserInput().trim();

            if (validOptions.contains(input)) {
                return input;
            }
            System.out.println("Invalid input: \"" + input + "\". Please choose one of: " + validOptions);
        }
    }
    
    //Collects user input of a certain type. 
    public <T> T collectValidType(String prompt, Function<String, T> parser) {
        while (true) {
            System.out.println(prompt);
            String input = collectUserInput().trim();

            try {
                return parser.apply(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: \"" + input + "\". Please try again.");
            }
        }
    }
}
