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
    
    Mixer() {
        this(8);
    }
    
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
            channelOptions.add(new ChannelOption(50));
        }
        soundPrinter = new SoundPrinter();
        isPlaying = false;
    }
    
    public boolean isPlaying() { return isPlaying; }
    
    public void addChannel() { addChannels(1); }
    
    public void addChannels(int numChannels) {
        int currLen = inputs.size();
        for(int i=currLen;i<currLen + numChannels;i++) {
            addInputPort("Channel"+String.valueOf(i+1));
            channelOptions.add(new ChannelOption(50));
        }
    }
    
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
    
    public int add(Chainable c) {
        return add(c,0);
    }
    
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
    
    @Override
    public double generate() {
        double samp = 0.0;
        boolean solo = false;
        int i;
        
        for (i = 0; i<inputs.size(); i++) {
            solo = solo || channelOptions.get(i).solo;
        }
        int soloIndex = i-1;
        
        for (i=0; i<inputs.size(); i++) {
            ChainPort cp = inputs.get(i);
            if (cp != null) {
                double currsample = (channelOptions.get(i).volume/100)
                                * cp.chainable.generate(cp.port);
                if (currsample < -1.0) { currsample = -1.0; }
                if (currsample > 1.0) { currsample = 1.0; }
                inputSamples.set(i,currsample);
            } else {
                inputSamples.set(i, 0.0);
            }
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
        
        if (solo) { samp = inputSamples.get(soloIndex); }
        
        soundPrinter.put(samp);
        
        return samp;
    }
    
    @Override
    public double generate(int i) {
        return generate();
    }

    public void play() {
        // Return if already playing
        if (isPlaying == true || outThread != null && outThread.isAlive()) return;

        // Otherwise create writer and kick off thread
        isPlaying  = true;
        outThread = new Thread(new MixGenerator(this, soundPrinter));
        outThread.start();
        
        /*for (int i = 0; i < 2048 && isPlaying; i++) {
                generate();
            }
        if (isPlaying) { soundPrinter.play(); }
        while (isPlaying) { generate(); }*/
    }
        
    @Override
    public void stop() {
        soundPrinter.putEnd();
        soundPrinter.stop();
        isPlaying = false;
    }
    
    private class MixGenerator implements Runnable  {
        Mixer mixer;
        SoundPrinter sPrinter;
        
        MixGenerator(Mixer m, SoundPrinter sp) {
            mixer = m;
            sPrinter = sp;
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
