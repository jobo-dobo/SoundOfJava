/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soundofjava;

import java.util.ArrayList;


/**
 * Interface which represents the ability to generate a sample or samples to one
 * or more outputs, and take inputs from others with this ability
 * 
 * @author Jonathon Zeitler
 */
public interface Chainable {

    /**
     * Creates a sample from default output
     * 
     * @return  the double value of the generated sample
     */
    double generate();
    
    /**
     * Creates a sample from specified output port
     * 
     * @param outputPort the output to generate for
     * @return           the double value of the generated sample
     */
    double generate(int outputPort);

    /**
     * Tells this Chainable to create a sample when generate is called
     */
    void start();

    /**
     * Tells this Chainable to suspend generating samples and give nothing or an
     * unaltered default based on its input(s)
     */
    void stop();

    /**
     * Specifies to connect an output port from this Chainable to an input of 
     * another Chainable
     * 
     * @param dest       the Chainable to connect an output to
     * @param sourcePort the output port to connect
     * @param destPort   the input port to connect to
     * @return           true if successful
     */
    boolean connectTo(Chainable dest, int sourcePort, int destPort);

    /**
     * Specifies to connect output to another Chainable's input according to
     * default behavior
     * 
     * @param dest  the Chainable to connect output to
     * @return      true if successful
     */
    boolean connectTo(Chainable dest);
    
    /**
     * Specifies to take an output port from another Chainable to an input of 
     * this Chainable. Validation should occur in connectTo, so this should not
     * return false.
     * 
     * @param source       the Chainable to connect an input from
     * @param sourcePort the output port to connect
     * @param destPort   the input port to connect to
     * @return           true if successful
     */
    boolean accept(Chainable source, int sourcePort, int destPort);
    
    /**
     * Clear the specified input.
     * 
     * @param destPort   the input port to connect to
     * @return           true if successful
     */
    boolean discard(int destPort);

    /**
     * Remove any current connection to specified input port
     *
     * @param inputPort the input port to disconnect
     * @return          true if successful
     */
    public boolean disconnectInput(int inputPort);

    /**
     * Disconnect input according to this Chainable's default behavior
     * 
     * @return  true if successful
     */
    boolean disconnectInput();

    /**
     * Remove any current connection from specified output port
     * 
     * @param outputPort    the output port to disconnect
     * @return              true if successful
     */
    boolean disconnectOutput(int outputPort);

    /**
     * Disconnect output according to default behavior
     */
    void disconnectOutput();
    
    /**
     * Disconnect all of this Chainable's input ports
     */
    public default void disconnectAllInput() {
        for (int i = 0; i<getInputs().size(); i++) {
            disconnectInput(i);
        }
    }
    
    /**
     * Disconnect all of this Chainable's output ports
     */
    public default void disconnectAllOuput() {
        for (int i = 0; i<getOutputs().size(); i++) {
            disconnectOutput(i);
        }
    }
    
    /**
     * Disconnect all of this Chainable's inputs and outputs
     */
    public default void disconnectAll() {
        disconnectAllInput();
        disconnectAllOuput();
    }

    /**
     * Returns a list of descriptors for this Chainable's input ports, port
     * number must directly correspond to index in the list
     * 
     * @return  ArrayList of input port descriptors
     */
    ArrayList<String> getInputs();

    /**
     * Returns a list of descriptors for this Chainable's output ports, port
     * number must directly correspond to index in the list
     * 
     * @return  ArrayList of output port descriptors
     */
    ArrayList<String> getOutputs();
    
    /**
     * Returns whether specified input port number is available for connection
     * 
     * @param inputPort input port number to check
     * @return          true if input port exists and is not connected
     */
    boolean inputAvailable(int inputPort);
    
    /**
     * Returns whether default input is available
     * 
     * @return  true if default input is available
     */
    boolean inputAvailable();
    
    /**
     * Returns whether specified output port number is available for connection
     * 
     * @param outputPort    output port number to check
     * @return              true if output port exists and is not connected
     */
    boolean outputAvailable(int outputPort);
    
    /**
     * Returns whether default output is available
     * 
     * @return  true if default output is available
     */
    boolean outputAvailable();
}
