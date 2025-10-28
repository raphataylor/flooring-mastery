package com.wileyedge.flooring;

import com.wileyedge.flooring.controller.Controller;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Main application entry point for Flooring Mastery
 * Uses Spring Dependency Injection to wire components
 */
public class App {

    public static void main(String[] args) {
        // Load Spring ApplicationContext from XML configuration
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

        // Get the controller bean from Spring
        Controller controller = ctx.getBean("controller", Controller.class);

        // Run the application
        controller.run();
    }
}