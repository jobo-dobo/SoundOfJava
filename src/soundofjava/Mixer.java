/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soundofjava;

/**
 *
 * @author Jonathon Zeitler
 */
public class Mixer extends MergerSplitter {
    SoundPrinter soundPrinter;
    boolean isPlaying;
    
    /**
     * Default constructor, creates 8 channels
     */
    Mixer() {
        this(8);
    }
    
    /**
     * Default constructor that takes number of channels to start with
     * 
     * @param numChannels   number of channels to create
     */
    Mixer(int numChannels) {
        super(numChannels, 0);
        
        for(int i=0;i<numChannels;i++) {
            addInputPort("Channel"+String.valueOf(i+1));
            options.add(new InputOption(0.5));
        }
        soundPrinter = new SoundPrinter();
        isPlaying = false;
    }
    
    /**
     * Returns whether Mixer is playing to audio output
     * @return true if playing
     */
    public boolean isPlaying() { return isPlaying; }
    
    /**
     * Adds one new channel to this Mixer
     */
    public void addChannel() { addChannels(1); }
    
    /**
     * Adds specified number of channels to this Mixer
     * @param numChannels   number of channels to add
     */
    public void addChannels(int numChannels) {
        int currLen = inputs.size();
        for(int i=currLen;i<currLen + numChannels;i++) {
            addInputPort("Channel"+String.valueOf(i+1));
            options.add(new InputOption(0.5));
        }
    }
    
    
    /**
     * Add a chainable to the mixer whose default output port is 0
     * 
     * @param c Chainable to add
     * @return  channel Chainable has been connected to
     */
    public int add(Chainable c) {
        return add(c,0);
    }
    
    /**
     * Add a Chainable's specified output port to the first available channel
     * in this Mixer, or create a new channel and add to it if all are filled
     * 
     * @param c         Chainable to add
     * @param outPort   output port to connect to a channel in this Mixer
     * @return          channel number the Chainable has been connected to
     */
    @SuppressWarnings("empty-statement")
    public int add(Chainable c, int outPort) {
        int inPort;
        for (inPort = 0;
                inPort<inputs.size() && inputs.get(inPort)!=null;
                inPort++);
        if (inPort==inputs.size()) { addChannel(); }
        c.connectTo(this,outPort,inPort);
        return inPort;
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
        double samp = generateMixInput();

        // add to audio output queue
        soundPrinter.put(samp);
        
        return samp;
    }
    
    /**
     * Generates a sample that mixes samples generated from all channels
     * according to the current channel states, and adds it to an audio output
     * queue
     * 
     * @param i output channel, ignored for Mixer, which has no output
     * @return  generated sample value
     */
    @Override
    public double generate(int i) {
        return generate();
    }

    /**
     * Start playing generated and queued samples to audio output
     */
    public void play() {
        if (!isPlaying) {
            isPlaying = true;
            soundPrinter.play();
        }
    }
    
    /**
     * Stop playing to audio output
     */
    @Override
    public void stop() {
        soundPrinter.putEnd();
        soundPrinter.stop();
        isPlaying = false;
    }
}
