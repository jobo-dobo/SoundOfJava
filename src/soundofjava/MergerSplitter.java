/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soundofjava;

import java.util.ArrayList;

/**
 *
 * @author Jonathon Z
 */
public class MergerSplitter extends Component {
    protected ArrayList<Boolean> outputStates;
    protected double currentSample;
    ArrayList<InputOption> options;
    
    MergerSplitter(int numInputs, int numOutputs) {
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        inputSamples = new ArrayList<>();
        outputSamples = new ArrayList<>();
        inputPorts = new ArrayList<>();
        outputPorts = new ArrayList<>();
        options = new ArrayList<>();
        
        outputStates = new ArrayList<>();
        currentSample = 0.0;
        
        for (int i=0; i<numInputs; i++) {
            addInputPort("Input"+String.valueOf(i+1));
            options.add(new InputOption(0.5));
        }
        
        for (int i=0; i<numOutputs; i++) {
            outputStates.add(false);
            addOutputPort("Output"+String.valueOf(i+1));
        }
    }
    
    /**
     * Get current volume of a channel
     * 
     * @param c channel number
     * @return  volume of channel
     */
    public double getVolume(int c) {
        if (c>=0 && c<inputs.size()) {
            return options.get(c).volume;
        } else {
            return 0.0;
        }
    }
    
    /**
     * Get current mute state of a channel
     * 
     * @param c channel number
     * @return  true if muted
     */
    public boolean getMute(int c) {
        if (c>=0 && c<inputs.size()) {
            return options.get(c).mute;
        } else {
            return false;
        }
    }
    
    /**
     * Get solo state of a channel
     * 
     * @param c channel number
     * @return  true if in solo state
     */
    public boolean getSolo(int c) {
        if (c>=0 && c<inputs.size()) {
            return options.get(c).solo;
        } else {
            return false;
        }
    }
    
    /**
     * Set volume of a channel
     * 
     * @param c channel number
     * @param v volume to set to (multiplicative scale; 0.0 to 1.0 recommended)
     */
    public void setVolume(int c, double v) {
        if (c>=0 && c<inputs.size()) {
            if (v<0) { v=0; }
            options.get(c).volume = v;
        }
    }
    
    /**
     * Mute a channel
     * 
     * @param c channel number to mute
     */
    public void mute(int c) {
        if (c>=0 && c<inputs.size()) {
            options.get(c).mute = true;
        }
    }
    
    /** 
     * Unmute a channel
     * 
     * @param c channel number to unmute
     */
    public void unmute(int c) {
        if (c>=0 && c<inputs.size()) {
            options.get(c).mute = true;
        }
    }
    
    /**
     * Toggle solo state for channel. Toggling solo for any channel will set
     * solo state to off on all other channels
     * 
     * @param c channel number
     */
    public void toggleSolo(int c) {
        for (int i=0; i<inputs.size(); i++) {
            if (i==c) {
                options.get(i).solo = !options.get(i).solo;
            } else {
                options.get(i).solo = false;
            }
        }
    }
    
    /**
     * Generates a sample that mixes samples generated from all channels
     * according to the current channel states, and adds it to an audio output
     * queue
     * 
     * @return  generated sample value
     */
    @Override
    public double generate() {
        return generate(0);
    }
    
    /**
     * Generates a sample that mixes samples generated from all channels
     * according to the current channel states, and adds it to an audio output
     * queue
     * 
     * @param port output channel
     * @return  generated sample value
     */
    @Override
    public double generate(int port) {
        if (outputStates.get(port)) {
            currentSample = generateMixInput();
            resetOutputStates();
        }
        outputStates.set(port, true);
        
        return currentSample;
    }

    /**
     * Helper function to mix all inputs for the next sample
     * 
     * @return The sample mixed from inputs
     */
    protected double generateMixInput() {
        double samp = 0.0;
        boolean solo = false;
        int i;
        
        // check for a solo channel
        for (i = 0; !solo && i<inputs.size(); i++) {
            solo = solo || options.get(i).solo;
        }
        int soloIndex = i-1;
        
        // iterate through channels
        for (i=0; i<inputs.size(); i++) {
            ChainPort cp = inputs.get(i);
            
            // generate sample for this channel at current volume and store
            if (cp != null) {
                double currsample = getVolume(i)
                                * cp.chainable.generate(cp.port);
                if (currsample < -1.0) { currsample = -1.0; }
                if (currsample > 1.0) { currsample = 1.0; }
                inputSamples.set(i,currsample);
            } else {
                inputSamples.set(i, 0.0);
            }
            
            // mix in the channel sample to the final sample based on simple
            // algorithm to avoid clipping and whether channel is muted
            if (!options.get(i).mute) {
                double nextsamp = inputSamples.get(i);
                if (nextsamp<0 && samp<0) {
                    samp = (samp+nextsamp)+(samp*nextsamp);
                } else if (nextsamp>0 && samp>0) {
                    samp = (samp+nextsamp)-(samp*nextsamp);
                } else {
                    samp = samp+nextsamp;
                }
            }
        }
        
        // reassign to just solo channel's sample if a channel has solo on
        if (solo) { samp = inputSamples.get(soloIndex); }
        
        return samp;
    }
    
    /**
     * Reset output states after an output is generated again
     */
    protected void resetOutputStates() {
        for (int i=0; i<outputs.size(); i++) {
            outputStates.set(i, false);
        }
    }
    
        /**
     * Add a chainable to an input whose default output port is 0
     * 
     * @param c Chainable to add
     * @return  channel Chainable has been connected to
     */
    public int addInput(Chainable c) {
        return addInput(c,0);
    }
    
    /**
     * Add a Chainable's specified output port to the first available input
     * or create a new channel and add to it if all are filled
     * 
     * @param c         Chainable to add
     * @param outPort   output port to connect to an input
     * @return          port number the Chainable has been connected to
     */
    @SuppressWarnings("empty-statement")
    public int addInput(Chainable c, int outPort) {
        int inPort;
        for (inPort = 0;
                inPort<inputs.size() && inputs.get(inPort)!=null;
                inPort++);
        if (inPort==inputs.size()) {
            addInputPort("Input"+String.valueOf(inputs.size()+1));
            options.add(new InputOption(0.5));
        }
        c.connectTo(this,outPort,inPort);
        return inPort;
    }
    
    /**
     * Add a chainable whose default input port is 0
     * 
     * @param c Chainable to add
     * @return  channel Chainable has been connected to
     */
    public int addOutput(Chainable c) {
        return addOutput(c,0);
    }
    
    /**
     * Add a Chainable's specified input port to the first available output
     * or create a new output and add to it if all are filled
     * 
     * @param c         Chainable to connect to an output
     * @param inPort    input port to connect to
     * @return          channel number the Chainable has been connected to
     */
    @SuppressWarnings("empty-statement")
    public int addOutput(Chainable c, int inPort) {
        int outPort;
        for (outPort = 0;
                outPort<outputs.size() && outputs.get(outPort)!=null;
                outPort++);
        if (outPort==outputs.size()) {
            outputStates.add(false);
            addOutputPort("Output"+String.valueOf(outputs.size()+1));
        }
        connectTo(c,outPort,inPort);
        return outPort;
    }

    /**
     * Simple data struct to store input options
     */
    protected class InputOption {
        double volume;
        boolean mute;
        boolean solo;
        InputOption(double v) {
            volume = v;
            mute = false;
            solo = false;
        }
    }
}
