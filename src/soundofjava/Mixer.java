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
public class Mixer extends Component {
    ArrayList<ChannelOption> channelOptions;
    SoundPrinter soundPrinter;
    boolean isPlaying;
    private Thread outThread;
    
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
        channelOptions = new ArrayList<>();
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        inputSamples = new ArrayList<>();
        outputSamples = new ArrayList<>();
        inputPorts = new ArrayList<>();
        outputPorts = new ArrayList<>();
        
        for(int i=0;i<numChannels;i++) {
            addInputPort("Channel"+String.valueOf(i+1));
            channelOptions.add(new ChannelOption(0.5));
        }
        soundPrinter = new SoundPrinter();
        isPlaying = false;
    }
    
    /**
     * Get current volume of a channel
     * 
     * @param c channel number
     * @return  volume of channel
     */
    public double getVolume(int c) {
        if (c>=0 && c<inputs.size()) {
            return channelOptions.get(c).volume;
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
            return channelOptions.get(c).mute;
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
            return channelOptions.get(c).solo;
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
            channelOptions.get(c).volume = v;
        }
    }
    
    /**
     * Mute a channel
     * 
     * @param c channel number to mute
     */
    public void mute(int c) {
        if (c>=0 && c<inputs.size()) {
            channelOptions.get(c).mute = true;
        }
    }
    
    /** 
     * Unmute a channel
     * 
     * @param c channel number to unmute
     */
    public void unmute(int c) {
        if (c>=0 && c<inputs.size()) {
            channelOptions.get(c).mute = true;
        }
    }
    
    /**
     * Toggle solo state for channel. Toggling solo for amy channel will set
     * solo state to off on all other channels
     * 
     * @param c channel number
     */
    public void toggleSolo(int c) {
        for (int i=0; i<inputs.size(); i++) {
            if (i==c) {
                channelOptions.get(i).solo = !channelOptions.get(i).solo;
            } else {
                channelOptions.get(i).solo = false;
            }
        }
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
            channelOptions.add(new ChannelOption(0.5));
        }
    }
    
    /**
     * Simple data struct to store channel options
     */
    private class ChannelOption {
        double volume;
        boolean mute;
        boolean solo;
        ChannelOption(double v) {
            volume = v;
            mute = false;
            solo = false;
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
        double samp = 0.0;
        boolean solo = false;
        int i;
        
        // check for a solo channel
        for (i = 0; !solo && i<inputs.size(); i++) {
            solo = solo || channelOptions.get(i).solo;
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
            if (!channelOptions.get(i).mute) {
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
        // Return if already playing
        if (isPlaying == true || outThread != null && outThread.isAlive()) return;

        // Otherwise create writer and kick off thread
        isPlaying  = true;
        outThread = new Thread(new MixGenerator(this));
        outThread.start();
        
        /*for (int i = 0; i < 2048 && isPlaying; i++) {
                generate();
            }
        if (isPlaying) { soundPrinter.play(); }
        while (isPlaying) { generate(); }*/
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
    
    /**
     * Nested class used for thread that writes to audio output
     */
    private class MixGenerator implements Runnable  {
        Mixer mixer;
        SoundPrinter sPrinter;
        
        /**
         * Constructor which takes the Mixer
         * 
         * @param m the Mixer writing to output
         */
        MixGenerator(Mixer m) {
            mixer = m;
            sPrinter = m.soundPrinter;
        }
        
        @Override
        public void run() {
            for (int i = 0; i < 2048 && mixer.isPlaying(); i++) {
                mixer.generate();
            }
            if (mixer.isPlaying()) { sPrinter.play(); }
            while (mixer.isPlaying()) { mixer.generate(); }
        }
    }
}
