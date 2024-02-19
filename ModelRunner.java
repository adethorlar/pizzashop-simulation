package aoodebod.hw6;

import jsl.simulation.Simulation;
import rossetti.hw4.problem2.CallCenter;

public class ModelRunner {


    public static void main(String[] args) {
        System.out.println("PizzaShop Simulation");
        Simulation s = new Simulation("PizzaShop");
        // create the model element and attach it to the main model
        PizzaShop c = new PizzaShop(s.getModel(), "PizzaShop");
        // set the parameters of the simulation
        s.setNumberOfReplications(50);
        s.setLengthOfReplication(180);
        s.run();
        System.out.println("Done!");
        s.printHalfWidthSummaryReport();
    }
}
