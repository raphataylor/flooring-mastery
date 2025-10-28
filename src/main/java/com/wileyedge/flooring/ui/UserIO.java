package com.wileyedge.flooring.ui;

public interface UserIO {

    /**
     * Prints a message to the console
     * @param message the message to print
     */
    void print(String message);

    /**
     * Reads a string from the user
     * @param prompt the prompt to display
     * @return the string entered by the user
     */
    String readString(String prompt);

    /**
     * Reads an integer from the user
     * @param prompt the prompt to display
     * @return the integer entered by the user
     */
    int readInt(String prompt);

    /**
     * Reads an integer from the user within a range
     * @param prompt the prompt to display
     * @param min the minimum value
     * @param max the maximum value
     * @return the integer entered by the user
     */
    int readInt(String prompt, int min, int max);

    /**
     * Reads a double from the user
     * @param prompt the prompt to display
     * @return the double entered by the user
     */
    double readDouble(String prompt);

    /**
     * Reads a double from the user within a range
     * @param prompt the prompt to display
     * @param min the minimum value
     * @param max the maximum value
     * @return the double entered by the user
     */
    double readDouble(String prompt, double min, double max);
}