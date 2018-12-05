/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soundofjava;

import java.util.ArrayList;

/**
 *
 * @author Jonathon Zeitler
 */
abstract class Component implements Chainable {
    protected ArrayList<ChainPort> inputs;
    protected ArrayList<ChainPort> outputs;
    protected ArrayList<Double> inputSamples;
    protected ArrayList<Double> outputSamples;
    protected ArrayList<String> inputPorts;
    protected ArrayList<String> outputPorts;
    protected boolean generateActive;
    
    protected class ChainPort {
        public Chainable chainable;
        public int port;
        
        ChainPort(Chainable c, int p) {
            chainable = c;
            port = p;
        }
    }
    
    Component() {
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        inputSamples = new ArrayList<>();
        outputSamples = new ArrayList<>();
        inputPorts = new ArrayList<>();
        outputPorts = new ArrayList<>();
        
        generateActive = true;
        
        inputs.add(0,null);
        outputs.add(0,null);
        inputSamples.add(0,0.0);
        outputSamples.add(0,0.0);
        inputPorts.add(0,"Primary");
        outputPorts.add(0,"Primary");
    }
    
    protected final int addInputPort(String portName) {
        inputs.add(null);
        inputSamples.add(0.0);
        inputPorts.add(portName);
        return inputPorts.size()-1;
    }
    
    protected final int addOutputPort(String portName) {
        outputs.add(null);
        outputSamples.add(0.0);
        outputPorts.add(portName);
        return outputPorts.size()-1;
    }
    
    @Override
    public abstract double generate();
    
    @Override
    public abstract double generate(int outputPort);
    
    protected final double generateInput(int inputPort) {
        ChainPort port = inputs.get(inputPort);
        if (port == null) { return 0.0; }
        return port.chainable.generate(port.port);
    }
    
    @Override
    public void start() { generateActive = true; }
    
    @Override
    public void stop() { generateActive = false; }
    
    @Override
    public boolean connectTo(Chainable dest, int sourcePort, int destPort) {
        if (outputAvailable(sourcePort)
                && dest.inputAvailable(destPort)) {
            outputs.set(sourcePort, new ChainPort(dest,destPort));
            return dest.accept(this, sourcePort, destPort);
        } else {
            return false;
        }
    }
    
    @Override
    public boolean connectTo(Chainable dest) {
        return connectTo(dest, 0, 0);
    }
    
    @Override
    public boolean accept(Chainable source, int sourcePort, int destPort) {
        inputs.set(destPort, new ChainPort(source,sourcePort));
        return true;
    }
    
    @Override
    public boolean discard(int destPort) {
        if (destPort<inputs.size()) {
            inputs.set(destPort, null);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean disconnectInput(int inputPort) {
        Chainable source = inputs.get(inputPort).chainable;
        int outputPort = inputs.get(inputPort).port;
        return source.disconnectOutput(outputPort);
    }

    @Override
    public boolean disconnectInput() {
        return disconnectInput(0);
    }

    @Override
    public boolean disconnectOutput(int outputPort) {
        if (outputPort<outputs.size()) {
            if (outputs.get(outputPort) == null) return true;
            Chainable source = outputs.get(outputPort).chainable;
            int inputPort = outputs.get(outputPort).port;
            outputs.set(outputPort,null);
            return source.discard(inputPort);
        } else {
            return false;
        }
    }

    @Override
    public void disconnectOutput() {
        disconnectOutput(0);
    }
    
    @Override
    public ArrayList<String> getInputs() {
        return inputPorts;
    }

    @Override
    public ArrayList<String> getOutputs() {
        return outputPorts;
    }
    
    
    @Override
    public boolean inputAvailable(int inputPort) {
        return (inputPort<inputs.size() && inputs.get(inputPort)==null);
    }
    
    @Override
    public boolean inputAvailable() {
        return inputAvailable(0);
    }
    
    @Override
    public boolean outputAvailable(int outputPort) {
        return (outputPort<outputs.size() && outputs.get(outputPort)==null);
    }
    
    @Override
    public boolean outputAvailable() {
        return outputAvailable(0);
    }
}
