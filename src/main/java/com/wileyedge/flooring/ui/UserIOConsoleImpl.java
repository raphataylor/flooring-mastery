package com.wileyedge.flooring.ui;

import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class UserIOConsoleImpl implements UserIO {

    private final Scanner console = new Scanner(System.in);

    @Override
    public void print(String message) {
        System.out.println(message);
    }

    @Override
    public String readString(String prompt) {
        System.out.println(prompt);
        return console.nextLine();
    }

    @Override
    public int readInt(String prompt) {
        while (true) {
            try {
                String input = readString(prompt);
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                print("Invalid input. Please enter a valid integer.");
            }
        }
    }

    @Override
    public int readInt(String prompt, int min, int max) {
        while (true) {
            int result = readInt(prompt);
            if (result >= min && result <= max) {
                return result;
            }
            print("Value must be between " + min + " and " + max + ".");
        }
    }

    @Override
    public double readDouble(String prompt) {
        while (true) {
            try {
                String input = readString(prompt);
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                print("Invalid input. Please enter a valid number.");
            }
        }
    }

    @Override
    public double readDouble(String prompt, double min, double max) {
        while (true) {
            double result = readDouble(prompt);
            if (result >= min && result <= max) {
                return result;
            }
            print("Value must be between " + min + " and " + max + ".");
        }
    }
}